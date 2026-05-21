package org.example.application.service;

import org.example.application.dto.ApplicantDto;
import org.example.domain.model.ApplicationStatus;
import org.example.domain.model.Faculty;
import org.example.domain.model.Subject;
import org.example.domain.repository.ApplicantRepository;
import org.example.domain.repository.ApplicationRepository;
import org.example.domain.repository.ApplicationStatusHistoryRepository;
import org.example.domain.repository.AuditLogRepository;
import org.example.domain.repository.FacultyRepository;
import org.example.domain.repository.GradeRepository;
import org.example.infrastructure.persistence.mysql.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.example.TestDataSource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApplicationServiceIntegrationTest {

    private static Connection sharedConnection;
    private static DataSource dataSource;

    private ApplicantService applicantService;
    private ApplicationService applicationService;
    private AdmissionService admissionService;
    private FacultyRepository facultyRepository;

    @BeforeAll
    static void initDatabase() throws SQLException {
        sharedConnection = DriverManager.getConnection(
                "jdbc:h2:mem:service_test;MODE=MYSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        dataSource = new TestDataSource(sharedConnection);
        DatabaseInitializer.initialize(sharedConnection);
    }

    @AfterAll
    static void closeDatabase() throws SQLException {
        if (sharedConnection != null) {
            sharedConnection.close();
        }
    }

    @BeforeEach
    void setUp() throws SQLException {
        ApplicantRepository applicantRepository = new MySqlApplicantRepository(dataSource);
        GradeRepository gradeRepository = new MySqlGradeRepository(dataSource);
        ApplicationRepository applicationRepository = new MySqlApplicationRepository(dataSource);
        facultyRepository = new MySqlFacultyRepository(dataSource);
        ApplicationStatusHistoryRepository statusHistoryRepository = new MySqlApplicationStatusHistoryRepository(dataSource);
        AuditLogRepository auditLogRepository = new MySqlAuditLogRepository(dataSource);

        applicantService = new DefaultApplicantService(applicantRepository, gradeRepository);
        applicationService = new ApplicationService(applicationRepository, applicantRepository, facultyRepository, statusHistoryRepository);
        AuditLogService auditLogService = new AuditLogService(auditLogRepository);
        admissionService = new DefaultAdmissionService(applicationRepository, facultyRepository, applicantRepository, statusHistoryRepository, auditLogService);

        sharedConnection.createStatement().execute("DELETE FROM applications");
        sharedConnection.createStatement().execute("DELETE FROM grades");
        sharedConnection.createStatement().execute("DELETE FROM applicants");
        sharedConnection.createStatement().execute("DELETE FROM faculties");
    }

    @Test
    void fullAdmissionProcess() {
        // 1. Створюємо факультет з одним місцем
        facultyRepository.save(new Faculty("Медицина", 1));

        // 2. Реєструємо абітурієнтів з оцінками
        applicantService.registerApplicant(new ApplicantDto("40", "Марія", "Шевченко",
                Map.of(Subject.BIOLOGY, 9)));
        applicantService.registerApplicant(new ApplicantDto("41", "Андрій", "Бондаренко",
                Map.of(Subject.BIOLOGY, 11)));

        // 3. Подаємо заявки
        applicationService.apply("40", "Медицина");
        applicationService.apply("41", "Медицина");

        // 4. Проводимо вступ
        admissionService.processAdmission("Медицина");

        // 5. Перевіряємо результати
        var apps40 = applicationService.getApplicationsByApplicant("40");
        var apps41 = applicationService.getApplicationsByApplicant("41");

        assertEquals(ApplicationStatus.REJECTED, apps40.get(0).getStatus());
        assertEquals(ApplicationStatus.ADMITTED, apps41.get(0).getStatus());
    }
}
