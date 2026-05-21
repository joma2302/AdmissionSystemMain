package org.example.application.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.domain.model.Application;
import org.example.domain.model.ApplicationStatus;
import org.example.domain.model.ApplicationStatusHistory;
import org.example.domain.repository.ApplicantRepository;
import org.example.domain.repository.ApplicationRepository;
import org.example.domain.repository.ApplicationStatusHistoryRepository;
import org.example.domain.repository.FacultyRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Сервіс подачі заявок на факультет.
 * Перевіряє, що абітурієнт може подати лише одну заявку (бізнес-правило).
 */
@Service
public class ApplicationService {

    private static final Logger logger = LogManager.getLogger(ApplicationService.class);

    private final ApplicationRepository applicationRepository;
    private final ApplicantRepository applicantRepository;
    private final FacultyRepository facultyRepository;
    private final ApplicationStatusHistoryRepository statusHistoryRepository;

    public ApplicationService(ApplicationRepository applicationRepository,
                              ApplicantRepository applicantRepository,
                              FacultyRepository facultyRepository,
                              ApplicationStatusHistoryRepository statusHistoryRepository) {
        this.applicationRepository = applicationRepository;
        this.applicantRepository = applicantRepository;
        this.facultyRepository = facultyRepository;
        this.statusHistoryRepository = statusHistoryRepository;
    }

    /**
     * Подає заявку абітурієнта на факультет.
     * Перевіряє існування абітурієнта та факультету, а також що заявка ще не подана.
     *
     * @param applicantId ID абітурієнта
     * @param facultyName назва факультету
     */
    public void apply(String applicantId, String facultyName) {
        var applicant = applicantRepository.findById(applicantId)
                .orElseThrow(() -> new IllegalArgumentException("Абітурієнт не знайдений: " + applicantId));
        var faculty = facultyRepository.findByName(facultyName)
                .orElseThrow(() -> new IllegalArgumentException("Факультет не знайдений: " + facultyName));

        // Бізнес-правило: абітурієнт може подати лише одну заявку
        List<Application> existing = applicationRepository.findByApplicantId(applicantId);
        if (!existing.isEmpty()) {
            logger.warn("Абітурієнт {} вже подав заявку на {}", applicantId,
                    existing.get(0).getFaculty().getName());
            throw new IllegalStateException("Абітурієнт вже подав заявку на факультет: "
                    + existing.get(0).getFaculty().getName());
        }

        applicationRepository.save(new Application(applicant, faculty));
        statusHistoryRepository.save(new ApplicationStatusHistory(applicantId, facultyName, ApplicationStatus.PENDING, LocalDateTime.now()));
        logger.info("Подано заявку: {} -> {}", applicantId, facultyName);
    }

    /**
     * Повертає список заявок абітурієнта.
     *
     * @param applicantId ID абітурієнта
     * @return список заявок
     */
    public List<Application> getApplicationsByApplicant(String applicantId) {
        return applicationRepository.findByApplicantId(applicantId);
    }
}
