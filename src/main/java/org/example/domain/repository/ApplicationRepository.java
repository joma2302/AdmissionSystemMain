package org.example.domain.repository;

import org.example.domain.model.Application;
import org.example.domain.model.ApplicationStatus;

import java.util.List;

/**
 * Інтерфейс репозиторію заявок (абстракція доступу до даних).
 * Реалізація — MySqlApplicationRepository.
 */
public interface ApplicationRepository {

    /** Зберігає або оновлює заявку. */
    void save(Application application);

    /** Знаходить заявки за назвою факультету. */
    List<Application> findByFacultyName(String facultyName);

    /** Знаходить заявки за ID абітурієнта. */
    List<Application> findByApplicantId(String applicantId);

    /** Повертає всі заявки для адміністративної аналітики. */
    List<Application> findAll();

    /** Знаходить заявки за факультетом, відсортовані за балами (для ранжування). */
    List<Application> findByFacultyNameOrderByScoreDesc(String facultyName);

    /** Оновлює статус заявки. */
    void updateStatus(Application application);

    /** Оновлює статус заявки за ключем без побудови доменної моделі. */
    void updateStatus(String applicantId, String facultyName, ApplicationStatus status);
}
