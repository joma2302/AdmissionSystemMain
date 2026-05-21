package org.example.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DomainModelTest {

    @Test
    void gradeAcceptsMinAndMaxScore() {
        assertDoesNotThrow(() -> new Grade(Subject.CHEMISTRY, 1));
        assertDoesNotThrow(() -> new Grade(Subject.CHEMISTRY, 12));
    }

    @Test
    void gradeRejectsTooLowScore() {
        assertThrows(IllegalArgumentException.class, () -> new Grade(Subject.BIOLOGY, 0));
        assertThrows(IllegalArgumentException.class, () -> new Grade(Subject.BIOLOGY, -5));
    }

    @Test
    void gradeRejectsTooHighScore() {
        assertThrows(IllegalArgumentException.class, () -> new Grade(Subject.ENGLISH, 13));
        assertThrows(IllegalArgumentException.class, () -> new Grade(Subject.ENGLISH, 100));
    }

    @Test
    void gradeDoesNotAllowNullSubject() {
        assertThrows(NullPointerException.class, () -> new Grade(null, 5));
    }

    @Test
    void applicantComputesTotalScoreFromMultipleGrades() {
        Applicant applicant = new Applicant("10", "Марія", "Шевченко");
        applicant.addGrade(new Grade(Subject.CHEMISTRY, 11));
        applicant.addGrade(new Grade(Subject.BIOLOGY, 9));
        applicant.addGrade(new Grade(Subject.ENGLISH, 7));
        assertEquals(27, applicant.getTotalScore());
    }

    @Test
    void applicantForbidsDuplicateSubjectGrade() {
        Applicant applicant = new Applicant("11", "Андрій", "Бондаренко");
        applicant.addGrade(new Grade(Subject.HISTORY, 6));
        assertThrows(IllegalArgumentException.class,
                () -> applicant.addGrade(new Grade(Subject.HISTORY, 10)));
    }

    @Test
    void applicantRejectsEmptyId() {
        assertThrows(IllegalArgumentException.class, () -> new Applicant("", "Марія", "Шевченко"));
    }

    @Test
    void applicantRejectsEmptyFirstName() {
        assertThrows(IllegalArgumentException.class, () -> new Applicant("10", "", "Шевченко"));
    }

    @Test
    void applicantRejectsEmptyLastName() {
        assertThrows(IllegalArgumentException.class, () -> new Applicant("10", "Марія", ""));
    }

    @Test
    void facultyRejectsEmptyName() {
        assertThrows(IllegalArgumentException.class, () -> new Faculty("", 5));
    }

    @Test
    void facultyRejectsZeroCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new Faculty("Медицина", 0));
    }

    @Test
    void facultyRejectsNegativeCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new Faculty("Медицина", -3));
    }

    @Test
    void applicationHasPendingStatusByDefault() {
        Application app = new Application(
                new Applicant("12", "Тетяна", "Мельник"),
                new Faculty("Медицина", 5));
        assertEquals(ApplicationStatus.PENDING, app.getStatus());
    }

    @Test
    void admissionSheetSelectsTopApplicants() {
        Faculty faculty = new Faculty("Медицина", 2);
        AdmissionSheet sheet = new AdmissionSheet(faculty);

        Applicant a1 = new Applicant("20", "Марія", "Шевченко");
        a1.addGrade(new Grade(Subject.BIOLOGY, 11));
        Applicant a2 = new Applicant("21", "Андрій", "Бондаренко");
        a2.addGrade(new Grade(Subject.BIOLOGY, 6));
        Applicant a3 = new Applicant("22", "Тетяна", "Мельник");
        a3.addGrade(new Grade(Subject.BIOLOGY, 9));

        sheet.register(new Application(a1, faculty));
        sheet.register(new Application(a2, faculty));
        sheet.register(new Application(a3, faculty));
        sheet.determineAdmitted();

        assertEquals(2, sheet.getAdmitted().size());
        assertEquals(1, sheet.getRejected().size());
        assertTrue(sheet.getAdmitted().stream()
                .anyMatch(a -> a.getApplicant().getFullName().equals("Шевченко Марія")));
        assertTrue(sheet.getAdmitted().stream()
                .anyMatch(a -> a.getApplicant().getFullName().equals("Мельник Тетяна")));
        assertEquals("Бондаренко Андрій", sheet.getRejected().get(0).getApplicant().getFullName());
    }

    @Test
    void admissionSheetRejectsSameApplicationTwice() {
        Faculty faculty = new Faculty("Медицина", 3);
        AdmissionSheet sheet = new AdmissionSheet(faculty);
        Applicant applicant = new Applicant("30", "Марія", "Шевченко");
        Application app = new Application(applicant, faculty);
        sheet.register(app);
        assertThrows(IllegalArgumentException.class, () -> sheet.register(app));
    }

    @Test
    void admissionSheetRejectsApplicationForDifferentFaculty() {
        Faculty medicine = new Faculty("Медицина", 3);
        Faculty law = new Faculty("Право", 3);
        AdmissionSheet sheet = new AdmissionSheet(medicine);
        Application app = new Application(new Applicant("31", "Андрій", "Бондаренко"), law);
        assertThrows(IllegalArgumentException.class, () -> sheet.register(app));
    }

    @Test
    void adminCreationAndProperties() {
        Admin admin = new Admin("admin1", "Олексій", "Іванов", "admin@example.com");
        assertEquals("admin1", admin.getUsername());
        assertEquals("Іванов Олексій", admin.getFullName());
        assertEquals("admin@example.com", admin.getEmail());
    }

    @Test
    void adminRejectsInvalidEmail() {
        assertThrows(IllegalArgumentException.class, () -> new Admin("admin1", "Олексій", "Іванов", "invalid-email"));
    }

    @Test
    void facultySubjectRequirements() {
        Faculty faculty = new Faculty("Комп'ютерні науки", 20);
        SubjectRequirement req = new SubjectRequirement(Subject.MATHEMATICS, 150);
        faculty.addRequirement(req);

        assertEquals(1, faculty.getRequirements().size());
        assertEquals(Subject.MATHEMATICS, faculty.getRequirements().get(0).getSubject());
        assertEquals(150, faculty.getRequirements().get(0).getMinimumScore());
    }

    @Test
    void subjectRequirementScoreValidation() {
        assertThrows(IllegalArgumentException.class, () -> new SubjectRequirement(Subject.PHYSICS, -1));
        assertThrows(IllegalArgumentException.class, () -> new SubjectRequirement(Subject.PHYSICS, 201));
    }
}
