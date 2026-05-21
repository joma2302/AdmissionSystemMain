package org.example.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdmissionSheetTest {

    private Faculty faculty;
    private AdmissionSheet admissionSheet;

    @BeforeEach
    void setUp() {
        faculty = new Faculty("IT", 2);
        admissionSheet = new AdmissionSheet(faculty);
    }

    @Test
    void testDetermineAdmitted_ByScore() {
        Applicant a1 = new Applicant("1", "Ivan", "Ivanov");
        a1.addGrade(new Grade(Subject.MATHEMATICS, 11));
        Application app1 = new Application(a1, faculty);

        Applicant a2 = new Applicant("2", "Petro", "Petrov");
        a2.addGrade(new Grade(Subject.MATHEMATICS, 9));
        Application app2 = new Application(a2, faculty);

        Applicant a3 = new Applicant("3", "Sidor", "Sidorov");
        a3.addGrade(new Grade(Subject.MATHEMATICS, 12));
        Application app3 = new Application(a3, faculty);

        admissionSheet.register(app1);
        admissionSheet.register(app2);
        admissionSheet.register(app3);

        admissionSheet.determineAdmitted();

        assertEquals(2, admissionSheet.getAdmitted().size());
        assertEquals(1, admissionSheet.getRejected().size());

        assertTrue(admissionSheet.getAdmitted().contains(app1));
        assertTrue(admissionSheet.getAdmitted().contains(app3));
        assertFalse(admissionSheet.getAdmitted().contains(app2));
    }

    @Test
    void testDetermineAdmitted_WithRequirements() {
        faculty.addRequirement(new SubjectRequirement(Subject.MATHEMATICS, 10));
        
        Applicant a1 = new Applicant("1", "Ivan", "Ivanov");
        a1.addGrade(new Grade(Subject.MATHEMATICS, 11)); // Meets req
        Application app1 = new Application(a1, faculty);

        Applicant a2 = new Applicant("2", "Petro", "Petrov");
        a2.addGrade(new Grade(Subject.MATHEMATICS, 9)); // Does NOT meet req
        Application app2 = new Application(a2, faculty);

        admissionSheet.register(app1);
        admissionSheet.register(app2);

        admissionSheet.determineAdmitted();

        assertEquals(1, admissionSheet.getAdmitted().size());
        assertTrue(admissionSheet.getAdmitted().contains(app1));
        assertEquals(ApplicationStatus.REJECTED, app2.getStatus());
    }

    @Test
    void testDetermineAdmitted_EmptyApplications() {
        admissionSheet.determineAdmitted();
        assertEquals(0, admissionSheet.getAdmitted().size());
    }

    @Test
    void testRegister_DifferentFaculty() {
        Faculty other = new Faculty("Bio", 5);
        Applicant a1 = new Applicant("1", "Ivan", "Ivanov");
        Application app = new Application(a1, other);
        
        assertThrows(IllegalArgumentException.class, () -> admissionSheet.register(app));
    }
}
