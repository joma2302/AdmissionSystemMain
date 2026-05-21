package org.example.domain.model;

import java.util.Objects;

/**
 * Доменна сутність «Адміністратор».
 * Представляє користувача з адміністративними повноваженнями у системі.
 * Інкапсулює дані адміністратора та зв'язок з обліковим записом користувача.
 */
public class Admin {

    private final String username;
    private final String firstName;
    private final String lastName;
    private final String email;

    /**
     * Створює об'єкт адміністратора.
     *
     * @param username  ім'я користувача (логін)
     * @param firstName ім'я
     * @param lastName  прізвище
     * @param email     електронна пошта
     */
    public Admin(String username, String firstName, String lastName, String email) {
        Objects.requireNonNull(username, "Логін не може бути null");
        Objects.requireNonNull(firstName, "Ім'я не може бути null");
        Objects.requireNonNull(lastName, "Прізвище не може бути null");
        Objects.requireNonNull(email, "Email не може бути null");
        
        if (username.isBlank()) throw new IllegalArgumentException("Логін не може бути порожнім");
        if (firstName.isBlank()) throw new IllegalArgumentException("Ім'я не може бути порожнім");
        if (lastName.isBlank()) throw new IllegalArgumentException("Прізвище не може бути порожнім");
        if (!email.contains("@")) throw new IllegalArgumentException("Некоректний формат email");

        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public String getUsername() { return username; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }

    /** Повертає повне ім'я адміністратора. */
    public String getFullName() {
        return lastName + " " + firstName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Admin admin)) return false;
        return username.equals(admin.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public String toString() {
        return "Admin{username='" + username + "', name='" + getFullName() + "'}";
    }
}
