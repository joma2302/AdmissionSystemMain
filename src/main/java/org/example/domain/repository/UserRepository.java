package org.example.domain.repository;

import org.example.domain.model.User;

import java.util.Optional;

/**
 * Інтерфейс репозиторію користувачів (абстракція доступу до даних).
 * Реалізація — MySqlUserRepository.
 */
public interface UserRepository {
    void save(User user);
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    java.util.List<User> findAll();
}
