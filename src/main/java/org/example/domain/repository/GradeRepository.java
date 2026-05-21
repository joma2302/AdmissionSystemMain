package org.example.domain.repository;

import org.example.domain.model.Grade;
import java.util.List;

/**
 * Репозиторій для роботи з оцінками абітурієнтів.
 */
public interface GradeRepository {
    void save(String applicantId, Grade grade);
    List<Grade> findByApplicantId(String applicantId);
    void deleteByApplicantId(String applicantId);
}
