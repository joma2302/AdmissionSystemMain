package org.example.domain.repository;

import org.example.domain.model.Faculty;

import java.util.List;
import java.util.Optional;

/**
 * Інтерфейс репозиторію факультетів (абстракція доступу до даних).
 * Реалізація — MySqlFacultyRepository з кешуванням через Caffeine.
 */
public interface FacultyRepository {
    void save(Faculty faculty);
    Optional<Faculty> findByName(String name);
    List<Faculty> findAll();
    void deleteByName(String name);
}
