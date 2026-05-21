package org.example.application.service;

import org.example.application.dto.ApplicantDto;
import org.example.domain.model.*;
import org.example.domain.repository.*;
import org.example.infrastructure.persistence.mysql.*;
import org.junit.jupiter.api.*;

import org.example.TestDataSource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class ServiceLayerTest {

    private static Connection sharedConnection;
    private static DataSource dataSource;

    private ApplicantService applicantService;
    private ApplicationService applicationService;
    private AdmissionService admissionService;
    private AuditLogService auditLogService;
    private FacultyRepository facultyRepository;

    @BeforeAll
    static void initDatabase() throws SQLException {
        sharedConnection = DriverManager.getConnection(
                "jdbc:h2:mem:service_layer_test;MODE=MYSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        dataSource = new TestDataSource(sharedConnection);
        DatabaseInitializer.initialize(sharedConnection);
    }

    @AfterAll
    static void closeDatabase() throws SQLException {
        if (sharedConnection != null) sharedConnection.close();
    }

    @BeforeEach
    void setUp() throws SQLException {
        ApplicantRepository applicantRepo = new MySqlApplicantRepository(dataSource);
        GradeRepository gradeRepo = new MySqlGradeRepository(dataSource);
        ApplicationRepository appRepo = new MySqlApplicationRepository(dataSource);
        facultyRepository = new MySqlFacultyRepository(dataSource);

        ApplicationStatusHistoryRepository statusHistoryRepo = new MySqlApplicationStatusHistoryRepository(dataSource);
        AuditLogRepository auditLogRepo = new MySqlAuditLogRepository(dataSource);

        applicantService = new DefaultApplicantService(applicantRepo, gradeRepo);
        applicationService = new ApplicationService(appRepo, applicantRepo, facultyRepository, statusHistoryRepo);
        auditLogService = new AuditLogService(auditLogRepo);
        admissionService = new DefaultAdmissionService(appRepo, facultyRepository, applicantRepo, statusHistoryRepo, auditLogService);

        sharedConnection.createStatement().execute("DELETE FROM applications");
        sharedConnection.createStatement().execute("DELETE FROM grades");
        sharedConnection.createStatement().execute("DELETE FROM applicants");
        sharedConnection.createStatement().execute("DELETE FROM faculties");
    }

    @Test
    void registersApplicantWithMultipleGrades() {
        applicantService.registerApplicant(new ApplicantDto("50", "Марія", "Шевченко",
                Map.of(Subject.CHEMISTRY, 11, Subject.BIOLOGY, 9)));

        var applicant = applicantService.getApplicant("50");
        assertTrue(applicant.isPresent());
        assertEquals(20, applicant.get().getTotalScore());
    }

    @Test
    void registersApplicantWithEmptyGrades() {
        applicantService.registerApplicant(new ApplicantDto("51", "Тетяна", "Мельник", null));

        var applicant = applicantService.getApplicant("51");
        assertTrue(applicant.isPresent());
        assertEquals(0, applicant.get().getTotalScore());
    }

    @Test
    void forbidsApplyingToSecondFaculty() {
        facultyRepository.save(new Faculty("Медицина", 15));
        facultyRepository.save(new Faculty("Біологія", 15));
        applicantService.registerApplicant(new ApplicantDto("52", "Андрій", "Бондаренко", null));

        applicationService.apply("52", "Медицина");

        assertThrows(IllegalStateException.class, () -> applicationService.apply("52", "Біологія"));
    }

    @Test
    void throwsWhenApplicantDoesNotExist() {
        facultyRepository.save(new Faculty("Медицина", 15));
        assertThrows(IllegalArgumentException.class, () -> applicationService.apply("777", "Медицина"));
    }

    @Test
    void throwsWhenFacultyDoesNotExist() {
        applicantService.registerApplicant(new ApplicantDto("53", "Тетяна", "Мельник", null));
        assertThrows(IllegalArgumentException.class, () -> applicationService.apply("53", "Невідомий"));
    }

    @Test
    void ranksApplicantsByScoreDescending() {
        facultyRepository.save(new Faculty("Медицина", 10));
        applicantService.registerApplicant(new ApplicantDto("60", "Марія", "Шевченко",
                Map.of(Subject.BIOLOGY, 7)));
        applicantService.registerApplicant(new ApplicantDto("61", "Андрій", "Бондаренко",
                Map.of(Subject.BIOLOGY, 11)));
        applicantService.registerApplicant(new ApplicantDto("62", "Тетяна", "Мельник",
                Map.of(Subject.BIOLOGY, 9)));

        applicationService.apply("60", "Медицина");
        applicationService.apply("61", "Медицина");
        applicationService.apply("62", "Медицина");

        var ranked = admissionService.getRankedApplications("Медицина");
        assertEquals(3, ranked.size());
        assertEquals("Бондаренко Андрій", ranked.get(0).getApplicant().getFullName());
        assertEquals("Мельник Тетяна", ranked.get(1).getApplicant().getFullName());
        assertEquals("Шевченко Марія", ranked.get(2).getApplicant().getFullName());
    }

    @Test
    void processAdmissionBuildsCorrectLists() {
        facultyRepository.save(new Faculty("Медицина", 2));
        applicantService.registerApplicant(new ApplicantDto("70", "Марія", "Шевченко",
                Map.of(Subject.BIOLOGY, 7)));
        applicantService.registerApplicant(new ApplicantDto("71", "Андрій", "Бондаренко",
                Map.of(Subject.BIOLOGY, 11)));
        applicantService.registerApplicant(new ApplicantDto("72", "Тетяна", "Мельник",
                Map.of(Subject.BIOLOGY, 9)));

        applicationService.apply("70", "Медицина");
        applicationService.apply("71", "Медицина");
        applicationService.apply("72", "Медицина");

        AdmissionSheet sheet = admissionService.processAdmission("Медицина");

        assertEquals(2, sheet.getAdmitted().size());
        assertEquals(1, sheet.getRejected().size());
        assertEquals("Бондаренко Андрій", sheet.getAdmitted().get(0).getApplicant().getFullName());
        assertEquals("Шевченко Марія", sheet.getRejected().get(0).getApplicant().getFullName());
    }

    @Test
    void getTotalScoreComputesCorrectly() {
        facultyRepository.save(new Faculty("Медицина", 10));
        applicantService.registerApplicant(new ApplicantDto("80", "Марія", "Шевченко",
                Map.of(Subject.CHEMISTRY, 11, Subject.BIOLOGY, 9)));
        applicationService.apply("80", "Медицина");

        assertEquals(20, admissionService.getTotalScore("80"));
    }

    @Test
    void getTotalScoreThrowsForMissingApplicant() {
        assertThrows(IllegalArgumentException.class, () -> admissionService.getTotalScore("888"));
    }
}
