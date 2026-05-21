package org.example.infrastructure.persistence.mysql;

import org.example.domain.model.*;
import org.example.domain.repository.*;
import org.junit.jupiter.api.*;

import org.example.TestDataSource;
import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MySqlRepositoryTest {

    private static Connection sharedConnection;
    private static DataSource dataSource;

    private FacultyRepository facultyRepository;
    private ApplicantRepository applicantRepository;
    private GradeRepository gradeRepository;
    private ApplicationRepository applicationRepository;

    @BeforeAll
    static void initDatabase() throws SQLException {
        sharedConnection = DriverManager.getConnection(
                "jdbc:h2:mem:test;MODE=MYSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
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
        facultyRepository = new MySqlFacultyRepository(dataSource);
        applicantRepository = new MySqlApplicantRepository(dataSource);
        gradeRepository = new MySqlGradeRepository(dataSource);
        applicationRepository = new MySqlApplicationRepository(dataSource);

        sharedConnection.createStatement().execute("DELETE FROM applications");
        sharedConnection.createStatement().execute("DELETE FROM grades");
        sharedConnection.createStatement().execute("DELETE FROM applicants");
        sharedConnection.createStatement().execute("DELETE FROM faculties");
    }

    @Test
    void savesAndFindsFacultyByName() {
        Faculty faculty = new Faculty("Медицина", 25);
        facultyRepository.save(faculty);

        var found = facultyRepository.findByName("Медицина");
        assertTrue(found.isPresent());
        assertEquals("Медицина", found.get().getName());
        assertEquals(25, found.get().getMaxStudents());
    }

    @Test
    void findsAllSavedFaculties() {
        facultyRepository.save(new Faculty("Медицина", 25));
        facultyRepository.save(new Faculty("Біологія", 15));

        List<Faculty> all = facultyRepository.findAll();
        assertEquals(2, all.size());
    }

    @Test
    void savesApplicantAndLoadsWithGrades() {
        Applicant applicant = new Applicant("100", "Марія", "Шевченко");
        applicantRepository.save(applicant);

        gradeRepository.save("100", new Grade(Subject.CHEMISTRY, 11));
        gradeRepository.save("100", new Grade(Subject.BIOLOGY, 9));

        var found = applicantRepository.findById("100");
        assertTrue(found.isPresent());
        assertEquals("Шевченко Марія", found.get().getFullName());
        assertEquals(20, found.get().getTotalScore());
    }

    @Test
    void findsGradesForApplicant() {
        applicantRepository.save(new Applicant("101", "Андрій", "Бондаренко"));
        gradeRepository.save("101", new Grade(Subject.ENGLISH, 8));
        gradeRepository.save("101", new Grade(Subject.GEOGRAPHY, 10));

        List<Grade> grades = gradeRepository.findByApplicantId("101");
        assertEquals(2, grades.size());
    }

    @Test
    void findsApplicantsByFacultyName() {
        facultyRepository.save(new Faculty("Медицина", 25));
        applicantRepository.save(new Applicant("102", "Марія", "Шевченко"));
        applicantRepository.save(new Applicant("103", "Тетяна", "Мельник"));

        Faculty faculty = new Faculty("Медицина", 25);
        applicationRepository.save(new Application(new Applicant("102", "Марія", "Шевченко"), faculty));
        applicationRepository.save(new Application(new Applicant("103", "Тетяна", "Мельник"), faculty));

        List<Applicant> applicants = applicantRepository.findByFacultyName("Медицина");
        assertEquals(2, applicants.size());
    }

    @Test
    void findsApplicationsByApplicant() {
        facultyRepository.save(new Faculty("Медицина", 25));
        facultyRepository.save(new Faculty("Біологія", 15));
        applicantRepository.save(new Applicant("104", "Андрій", "Бондаренко"));

        Applicant applicant = new Applicant("104", "Андрій", "Бондаренко");
        applicationRepository.save(new Application(applicant, new Faculty("Медицина", 25)));
        applicationRepository.save(new Application(applicant, new Faculty("Біологія", 15)));

        List<Application> apps = applicationRepository.findByApplicantId("104");
        assertEquals(2, apps.size());
    }

    @Test
    void findsApplicationsOrderedByScoreDescending() {
        facultyRepository.save(new Faculty("Медицина", 5));
        applicantRepository.save(new Applicant("105", "Марія", "Шевченко"));
        applicantRepository.save(new Applicant("106", "Тетяна", "Мельник"));

        gradeRepository.save("105", new Grade(Subject.BIOLOGY, 7));
        gradeRepository.save("106", new Grade(Subject.BIOLOGY, 11));

        Faculty faculty = new Faculty("Медицина", 5);
        applicationRepository.save(new Application(new Applicant("105", "Марія", "Шевченко"), faculty));
        applicationRepository.save(new Application(new Applicant("106", "Тетяна", "Мельник"), faculty));

        List<Application> ranked = applicationRepository.findByFacultyNameOrderByScoreDesc("Медицина");
        assertEquals(2, ranked.size());
        assertEquals("Мельник Тетяна", ranked.get(0).getApplicant().getFullName());
        assertEquals("Шевченко Марія", ranked.get(1).getApplicant().getFullName());
    }

    @Test
    void updatesApplicationStatusCorrectly() {
        facultyRepository.save(new Faculty("Медицина", 25));
        applicantRepository.save(new Applicant("107", "Андрій", "Бондаренко"));

        Application app = new Application(
                new Applicant("107", "Андрій", "Бондаренко"),
                new Faculty("Медицина", 25));
        applicationRepository.save(app);

        app.admit();
        applicationRepository.updateStatus(app);

        List<Application> apps = applicationRepository.findByApplicantId("107");
        assertEquals(1, apps.size());
        assertEquals(ApplicationStatus.ADMITTED, apps.get(0).getStatus());
    }

    @Test
    void deletesApplicantAndCascadesGrades() {
        applicantRepository.save(new Applicant("108", "Тетяна", "Мельник"));
        gradeRepository.save("108", new Grade(Subject.CHEMISTRY, 10));

        applicantRepository.deleteById("108");

        assertTrue(applicantRepository.findById("108").isEmpty());
        assertTrue(gradeRepository.findByApplicantId("108").isEmpty());
    }
}
