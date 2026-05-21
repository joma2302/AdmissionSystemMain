package org.example.application.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.domain.model.*;
import org.example.domain.repository.ApplicantRepository;
import org.example.domain.repository.ApplicationRepository;
import org.example.domain.repository.ApplicationStatusHistoryRepository;
import org.example.domain.repository.AuditLogRepository;
import org.example.domain.repository.FacultyRepository;
import org.example.application.service.AuditLogService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DefaultAdmissionService implements AdmissionService {

    private static final Logger logger = LogManager.getLogger(DefaultAdmissionService.class);

    private final ApplicationRepository applicationRepository;
    private final FacultyRepository facultyRepository;
    private final ApplicantRepository applicantRepository;
    private final ApplicationStatusHistoryRepository statusHistoryRepository;
    private final AuditLogService auditLogService;

    public DefaultAdmissionService(ApplicationRepository applicationRepository,
                                  FacultyRepository facultyRepository,
                                  ApplicantRepository applicantRepository,
                                  ApplicationStatusHistoryRepository statusHistoryRepository,
                                  AuditLogService auditLogService) {
        this.applicationRepository = applicationRepository;
        this.facultyRepository = facultyRepository;
        this.applicantRepository = applicantRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.auditLogService = auditLogService;
    }

    @Override
    @CacheEvict(value = "rankings", allEntries = true)
    public AdmissionSheet processAdmission(String facultyName) {
        Faculty faculty = facultyRepository.findByName(facultyName)
                .orElseThrow(() -> new IllegalArgumentException("Факультет не знайдений: " + facultyName));

        List<Application> ranked = applicationRepository.findByFacultyNameOrderByScoreDesc(facultyName);
        logger.info("Зарахування на факультет '{}': {} заявок", facultyName, ranked.size());

        AdmissionSheet sheet = new AdmissionSheet(faculty);
        ranked.forEach(sheet::register);
        sheet.determineAdmitted();

        for (Application app : sheet.getApplications()) {
            applicationRepository.updateStatus(app);
            statusHistoryRepository.save(new ApplicationStatusHistory(app.getApplicant().getId(), facultyName, app.getStatus(), LocalDateTime.now()));
            logger.debug("Статус заявки {} -> {}: {}", app.getApplicant().getId(),
                    facultyName, app.getStatus());
        }

        logger.info("Зарахування завершено: {} зараховано, {} відхилено",
                sheet.getAdmitted().size(), sheet.getRejected().size());
        return sheet;
    }

    @Override
    public List<Application> getRankedApplications(String facultyName) {
        return applicationRepository.findByFacultyNameOrderByScoreDesc(facultyName);
    }

    @Override
    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }

    @Override
    @CacheEvict(value = "rankings", allEntries = true)
    public void updateApplicationStatus(String applicantId, String facultyName, ApplicationStatus status) {
        applicationRepository.updateStatus(applicantId, facultyName, status);
        statusHistoryRepository.save(new ApplicationStatusHistory(applicantId, facultyName, status, LocalDateTime.now()));
        
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        auditLogService.log(currentUser, "UPDATE_STATUS", "Applicant: " + applicantId + ", Faculty: " + facultyName + ", Status: " + status);
        
        logger.info("Адміністратор змінив статус заявки: {} -> {} = {}", applicantId, facultyName, status);
    }

    @Override
    public AdminDashboardStats getDashboardStats() {
        List<Application> applications = applicationRepository.findAll();
        List<Faculty> faculties = facultyRepository.findAll();
        int totalSeats = faculties.stream().mapToInt(Faculty::getMaxStudents).sum();
        int admitted = countByStatus(applications, ApplicationStatus.ADMITTED);
        return new AdminDashboardStats(
                applicantRepository.findAll().size(),
                applications.size(),
                countByStatus(applications, ApplicationStatus.PENDING),
                admitted,
                countByStatus(applications, ApplicationStatus.REJECTED),
                totalSeats,
                Math.max(totalSeats - admitted, 0));
    }

    @Override
    public List<ApplicationStatusHistory> getStatusHistory(String applicantId, String facultyName) {
        return statusHistoryRepository.findByApplication(applicantId, facultyName);
    }

    @Override
    public List<FacultyDemand> getFacultyDemand() {
        List<Application> applications = applicationRepository.findAll();
        Map<String, List<Application>> byFaculty = applications.stream()
                .collect(Collectors.groupingBy(app -> app.getFaculty().getName()));

        return facultyRepository.findAll().stream()
                .map(faculty -> buildFacultyDemand(faculty, byFaculty.getOrDefault(faculty.getName(), List.of())))
                .sorted(Comparator.comparing(FacultyDemand::applications).reversed()
                        .thenComparing(FacultyDemand::facultyName))
                .toList();
    }

    @Override
    public int getTotalScore(String applicantId) {
        List<Application> apps = applicationRepository.findByApplicantId(applicantId);
        if (apps.isEmpty()) {
            throw new IllegalArgumentException("Заявки абітурієнта не знайдені: " + applicantId);
        }
        return apps.get(0).getTotalScore();
    }

    private int countByStatus(List<Application> applications, ApplicationStatus status) {
        return (int) applications.stream().filter(app -> app.getStatus() == status).count();
    }

    private FacultyDemand buildFacultyDemand(Faculty faculty, List<Application> applications) {
        Map<ApplicationStatus, Long> counts = applications.stream()
                .map(Application::getStatus)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        int admitted = counts.getOrDefault(ApplicationStatus.ADMITTED, 0L).intValue();
        return new FacultyDemand(
                faculty.getName(),
                faculty.getMaxStudents(),
                applications.size(),
                counts.getOrDefault(ApplicationStatus.PENDING, 0L).intValue(),
                admitted,
                counts.getOrDefault(ApplicationStatus.REJECTED, 0L).intValue(),
                Math.max(faculty.getMaxStudents() - admitted, 0),
                faculty.getMaxStudents() == 0 ? 0 : (double) applications.size() / faculty.getMaxStudents());
    }
}