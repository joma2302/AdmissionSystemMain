package org.example.application.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.domain.model.Role;
import org.example.domain.model.User;
import org.example.domain.model.Applicant;
import org.example.domain.repository.UserRepository;
import org.example.domain.repository.ApplicantRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервіс автентифікації та управління користувачами.
 * Реалізує {@link UserDetailsService} для інтеграції зі Spring Security.
 * Забезпечує реєстрацію, автентифікацію та управління ролями.
 */
@Service
public class AuthService implements UserDetailsService {

    private static final Logger logger = LogManager.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final ApplicantRepository applicantRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, 
                       ApplicantRepository applicantRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.applicantRepository = applicantRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Завантажує користувача за ім'ям для Spring Security (Observer pattern — Security слухає цей сервіс).
     *
     * @param username ім'я користувача
     * @return UserDetails для автентифікації
     * @throws UsernameNotFoundException якщо користувача не знайдено
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("[DEBUG_LOG] Attempting login for user: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("[DEBUG_LOG] User NOT FOUND in database: {}", username);
                    return new UsernameNotFoundException("Користувача не знайдено: " + username);
                });

        logger.info("[DEBUG_LOG] User FOUND: {}. Password hash: {}", username, user.getPasswordHash());
        
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }

    /**
     * Реєстрація нового користувача з хешуванням пароля через BCrypt.
     *
     * @param username ім'я користувача
     * @param rawPassword пароль у відкритому вигляді
     * @param role роль користувача
     */
    public void registerUser(String username, String rawPassword, Role role) {
        if (userRepository.existsByUsername(username)) {
            logger.warn("Спроба реєстрації існуючого користувача: {}", username);
            throw new IllegalArgumentException("Користувач з таким ім'ям вже існує");
        }
        String hash = passwordEncoder.encode(rawPassword);
        userRepository.save(new User(username, hash, role));
        
        // Якщо це абітурієнт, створюємо для нього запис у таблиці applicants
        if (role == Role.APPLICANT) {
            applicantRepository.save(new org.example.domain.model.Applicant(username, "Не вказано", "Не вказано"));
            logger.info("Створено профіль абітурієнта для: {}", username);
        }
        
        logger.info("Зареєстровано нового користувача: {} з роллю {}", username, role);
    }

    public void resetPassword(String username, String newRawPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Користувача не знайдено"));
        
        String newHash = passwordEncoder.encode(newRawPassword);
        userRepository.save(new User(user.getUsername(), newHash, user.getRole()));
        logger.info("Пароль скинуто для користувача: {}", username);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Створення адміністратора та менеджера за замовчуванням.
     */
    @Bean
    public CommandLineRunner ensureDefaultUsers() {
        return args -> {
            // Створення ADMIN
            userRepository.findByUsername("admin").ifPresentOrElse(
                user -> {
                    if (!user.getPasswordHash().startsWith("$2a$")) {
                        logger.warn("Пароль адміністратора застарілий. Оновлення до BCrypt...");
                        String hash = passwordEncoder.encode("admin");
                        userRepository.save(new User("admin", hash, Role.ADMIN));
                    }
                },
                () -> {
                    String hash = passwordEncoder.encode("admin");
                    userRepository.save(new User("admin", hash, Role.ADMIN));
                    logger.info("Створено адміністратора за замовчуванням: admin/admin");
                }
            );

            // Створення MANAGER
            if (!userRepository.existsByUsername("manager")) {
                String hash = passwordEncoder.encode("manager");
                userRepository.save(new User("manager", hash, Role.MANAGER));
                logger.info("Створено менеджера за замовчуванням: manager/manager");
            }
        };
    }
}
