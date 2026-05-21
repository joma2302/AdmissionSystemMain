package org.example.application.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.application.dto.ApplicantDto;
import org.example.domain.model.Applicant;
import org.example.domain.model.Grade;
import org.example.domain.repository.ApplicantRepository;
import org.example.domain.repository.GradeRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Сервіс реєстрації та пошуку абітурієнтів.
 * Інкапсулює бізнес-логіку створення абітурієнта з оцінками.
 */
@Service
public interface ApplicantService {
    void registerApplicant(ApplicantDto dto);
    void updateApplicant(Applicant applicant);
    Optional<Applicant> getApplicant(String id);
    java.util.List<Applicant> getAllApplicants();
}
