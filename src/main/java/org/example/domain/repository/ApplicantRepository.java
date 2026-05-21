package org.example.domain.repository;

import org.example.domain.model.Applicant;

import java.util.List;
import java.util.Optional;

/**
 * Інтерфейс репозиторію абітурієнтів (абстракція доступу до даних).
 * Реалізація — MySqlApplicantRepository.
 */
public interface ApplicantRepository {

    /** Зберігає або оновлює абітурієнта. */
    void save(Applicant applicant);

    /** Знаходить абітурієнта за ID. */
    Optional<Applicant> findById(String id);

    /** Повертає список усіх абітурієнтів. */
    List<Applicant> findAll();

    /** Знаходить абітурієнтів за назвою факультету. */
    List<Applicant> findByFacultyName(String facultyName);

    /** Видаляє абітурієнта за ID. */
    void deleteById(String id);
}
