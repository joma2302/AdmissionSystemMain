package org.example.domain.model;

import java.util.Objects;

/**
 * Доменна сутність «Користувач системи».
 * Зберігає облікові дані для автентифікації через Spring Security.
 */
public class User {

    private final String username;
    private final String passwordHash;
    private final Role role;

    /**
     * Створює користувача з валідацією інваріантів.
     *
     * @param username     ім'я користувача (унікальне)
     * @param passwordHash хеш пароля (BCrypt)
     * @param role         роль у системі
     */
    public User(String username, String passwordHash, Role role) {
        Objects.requireNonNull(username, "Ім'я користувача не може бути null");
        Objects.requireNonNull(passwordHash, "Хеш пароля не може бути null");
        Objects.requireNonNull(role, "Роль не може бути null");
        if (username.isBlank()) {
            throw new IllegalArgumentException("Ім'я користувача не може бути порожнім");
        }
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public Role getRole() { return role; }

    /** Перевіряє, чи є користувач адміністратором. */
    public boolean isAdmin() { return role == Role.ADMIN; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return username.equals(user.username);
    }

    @Override
    public int hashCode() { return Objects.hash(username); }
}
