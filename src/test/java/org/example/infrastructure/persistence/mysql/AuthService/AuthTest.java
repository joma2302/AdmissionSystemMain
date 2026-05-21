package org.example.infrastructure.persistence.mysql.AuthService;

import org.example.application.service.AuthService;
import org.example.domain.model.Role;
import org.example.domain.model.User;
import org.example.domain.repository.UserRepository;
import org.example.domain.repository.ApplicantRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

public class AuthTest {

    public static void main(String[] args) {
        // --- Мокаем репозиторії ---
        UserRepository repo = new UserRepository() {
            User storedUser = null;
            @Override public void save(User user) { storedUser = user; }
            @Override public Optional<User> findByUsername(String username) {
                if (storedUser != null && storedUser.getUsername().equals(username)) return Optional.of(storedUser);
                return Optional.empty();
            }
            @Override public boolean existsByUsername(String username) {
                return storedUser != null && storedUser.getUsername().equals(username);
            }
            @Override public java.util.List<User> findAll() { return java.util.Collections.emptyList(); }
        };

        ApplicantRepository applicantRepo = new ApplicantRepository() {
            @Override public void save(org.example.domain.model.Applicant applicant) {}
            @Override public Optional<org.example.domain.model.Applicant> findById(String id) { return Optional.empty(); }
            @Override public java.util.List<org.example.domain.model.Applicant> findAll() { return java.util.Collections.emptyList(); }
            @Override public java.util.List<org.example.domain.model.Applicant> findByFacultyName(String facultyName) { return java.util.Collections.emptyList(); }
            @Override public void deleteById(String id) {}
        };

        // --- PasswordEncoder ---
        PasswordEncoder encoder = new BCryptPasswordEncoder();

        // --- AuthService ---
        AuthService authService = new AuthService(repo, applicantRepo, encoder);

        // --- Регистрируем тестового пользователя ---
        String testUsername = "testuser";
        String testPassword = "12345";
        authService.registerUser(testUsername, testPassword, Role.APPLICANT);
        System.out.println("Пользователь зарегистрирован: " + testUsername);

        // --- Проверяем вход через loadUserByUsername ---
        try {
            var userDetails = authService.loadUserByUsername(testUsername);
            System.out.println("UserDetails загружен: " + userDetails.getUsername());

            // --- Проверка пароля ---
            boolean matches = encoder.matches(testPassword, userDetails.getPassword());
            System.out.println("Пароль совпадает: " + matches);

            if (!matches) {
                System.err.println("Пароль не совпадает! Что-то с BCrypt или сохранением.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
