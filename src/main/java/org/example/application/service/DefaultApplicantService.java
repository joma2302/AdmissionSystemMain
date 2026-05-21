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

@Service
public class DefaultApplicantService implements ApplicantService {

    private static final Logger logger = LogManager.getLogger(DefaultApplicantService.class);

    private final ApplicantRepository applicantRepository;
    private final GradeRepository gradeRepository;

    public DefaultApplicantService(ApplicantRepository applicantRepository, GradeRepository gradeRepository) {
        this.applicantRepository = applicantRepository;
        this.gradeRepository = gradeRepository;
    }

    @Override
    public void registerApplicant(ApplicantDto dto) {
        Applicant applicant = new Applicant(dto.id(), dto.firstName(), dto.lastName());
        applicantRepository.save(applicant);
        logger.info("Зареєстровано абітурієнта: {} {} ({})", dto.firstName(), dto.lastName(), dto.id());

        if (dto.grades() != null) {
            dto.grades().forEach((subject, score) -> {
                gradeRepository.save(dto.id(), new Grade(subject, score));
                logger.debug("Збережено оцінку: {} = {} для {}", subject, score, dto.id());
            });
        }
    }

    @Override
    public void updateApplicant(Applicant applicant) {
        applicantRepository.save(applicant);
        logger.info("Оновлено абітурієнта: {}", applicant.getId());
    }

    @Override
    public Optional<Applicant> getApplicant(String id) {
        return applicantRepository.findById(id);
    }

    @Override
    public java.util.List<Applicant> getAllApplicants() {
        return applicantRepository.findAll();
    }
}