package org.example.infrastructure.web;

import org.example.application.service.AdmissionService;
import org.example.application.service.ApplicantService;
import org.example.domain.model.AdmissionSheet;
import org.example.domain.model.Faculty;
import org.example.domain.repository.ApplicationRepository;
import org.example.domain.repository.FacultyRepository;
import org.example.domain.repository.GradeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AdminControllerTest {

    private AdminController adminController;
    private AdmissionService admissionService;
    private FacultyRepository facultyRepository;
    private ApplicantService applicantService;
    private GradeRepository gradeRepository;
    private ApplicationRepository applicationRepository;

    @BeforeEach
    void setUp() {
        admissionService = mock(AdmissionService.class);
        facultyRepository = mock(FacultyRepository.class);
        adminController = new AdminController(admissionService, facultyRepository);
    }

    @Test
    void testDashboard() {
        AdmissionService.AdminDashboardStats stats = new AdmissionService.AdminDashboardStats(1, 1, 0, 1, 0, 10, 9);
        when(admissionService.getDashboardStats()).thenReturn(stats);
        when(admissionService.getFacultyDemand()).thenReturn(List.of());

        Model model = new ConcurrentModel();
        String view = adminController.dashboard(model);

        assertEquals("admin/dashboard", view);
        assertEquals(stats, model.getAttribute("stats"));
    }

    @Test
    void testProcessAdmission() {
        String facultyName = "IT";
        Faculty faculty = new Faculty(facultyName, 5);
        AdmissionSheet sheet = new AdmissionSheet(faculty);
        
        when(admissionService.processAdmission(facultyName)).thenReturn(sheet);
        when(facultyRepository.findAll()).thenReturn(List.of(faculty));

        Model model = new ConcurrentModel();
        String view = adminController.processAdmission(facultyName, model);

        assertEquals("admission", view);
        assertEquals(List.of(faculty), model.getAttribute("faculties"));
    }
}
