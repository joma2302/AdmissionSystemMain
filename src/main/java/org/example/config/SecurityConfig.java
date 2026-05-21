package org.example.config;

import org.example.application.service.AuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

/**
 * Конфігурація Spring Security.
 * Розподіляє ролі: ADMIN, APPLICANT, гість (неавторизований).
 * Інтегрує автентифікацію через форму входу.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Налаштування ланцюга фільтрів безпеки (Strategy pattern — різні стратегії доступу для ролей).
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   DaoAuthenticationProvider daoAuthenticationProvider) throws Exception {
        HttpSessionRequestCache requestCache = new HttpSessionRequestCache();
        requestCache.setMatchingRequestParameterName(null);

        http
            .requestCache(cache -> cache.requestCache(requestCache))
            .authorizeHttpRequests(auth -> auth
                // Публічні сторінки — доступні гостям
                .requestMatchers("/", "/signup", "/login", "/error", "/css/**", "/js/**").permitAll()
                // Зарахування та адмін-панель — для ADMIN та MANAGER
                .requestMatchers("/admission/**").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers("/admin/**").hasAnyRole("ADMIN", "MANAGER")
                // API перевірки токенів — доступний усім авторизованим
                .requestMatchers("/api/**").authenticated()
                // Решта — потребує автентифікації
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error=true")
                .failureHandler((request, response, exception) -> {
                    System.out.println("[DEBUG_LOG] Login failed: " + exception.getMessage());
                    request.getSession().setAttribute("SPRING_SECURITY_LAST_EXCEPTION", exception);
                    response.sendRedirect("/login?error=true");
                })
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/login?error=denied")
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .sessionManagement(session -> session
                .sessionFixation(sessionFixation -> sessionFixation.migrateSession())
                .maximumSessions(1)
            )
            .csrf(csrf -> csrf
                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            )
            .headers(headers -> headers
                .contentSecurityPolicy(csp -> csp
                    .policyDirectives("default-src 'self'; script-src 'self' https://cdn.jsdelivr.net; style-src 'self' 'unsafe-inline'; img-src 'self' data:;")
                )
                .frameOptions(frame -> frame.deny())
                .httpStrictTransportSecurity(hsts -> hsts.includeSubDomains(true).maxAgeInSeconds(31536000))
            )
            // Явно реєструємо провайдер автентифікації (DaoAuthenticationProvider)
            .authenticationProvider(daoAuthenticationProvider);

        return http.build();
    }

    /**
     * Кодувальник паролів BCrypt (Factory Method pattern).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Сервіс завантаження користувачів для Spring Security.
     * Делегує пошук до AuthService.
     */
    @Bean
    public UserDetailsService userDetailsService(AuthService authService) {
        return authService;
    }

    /**
     * Провайдер автентифікації Dao, який використовує наш UserDetailsService та BCrypt.
     */
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(UserDetailsService userDetailsService,
                                                               PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        provider.setHideUserNotFoundExceptions(false);
        return provider;
    }

    /**
     * Явний AuthenticationManager (необов'язково, але усуває попередження і гарантує наявність менеджера).
     */
    @Bean
    public AuthenticationManager authenticationManager(DaoAuthenticationProvider daoAuthenticationProvider) {
        return new ProviderManager(daoAuthenticationProvider);
    }
}
