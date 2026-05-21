package org.example.infrastructure.web;

import org.example.application.service.AdmissionService;
import org.example.domain.model.ApplicationStatus;
import org.example.domain.repository.FacultyRepository;
import org.example.infrastructure.web.dto.ApplicationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AdminApplicationControllerTest {

    private AdminApplicationController controller;
    private AdmissionService admissionService;
    private FacultyRepository facultyRepository;

    @BeforeEach
    void setUp() {
        admissionService = mock(AdmissionService.class);
        facultyRepository = mock(FacultyRepository.class);
        controller = new AdminApplicationController(admissionService, facultyRepository);
    }

    @Test
    void testListApplications() {
        when(admissionService.getAllApplications()).thenReturn(List.of());
        when(facultyRepository.findAll()).thenReturn(List.of());

        Model model = new ConcurrentModel();
        String view = controller.listApplications(null, null, null, null, model);

        assertEquals("admin/applications", view);
        assertEquals(List.of(), model.getAttribute("applications"));
        assertEquals(List.of(), model.getAttribute("faculties"));
    }

    @Test
    void testListApplicationsWithFilters() {
        // Setup
        var applicant1 = new org.example.domain.model.Applicant("1", "John", "Doe");
        applicant1.addGrade(new org.example.domain.model.Grade(org.example.domain.model.Subject.MATHEMATICS, 10));
        var applicant2 = new org.example.domain.model.Applicant("2", "Jane", "Smith");
        applicant2.addGrade(new org.example.domain.model.Grade(org.example.domain.model.Subject.MATHEMATICS, 12));
        
        var faculty = new org.example.domain.model.Faculty("IT", 30);
        
        var app1 = new org.example.domain.model.Application(applicant1, faculty);
        var app2 = new org.example.domain.model.Application(applicant2, faculty);
        
        when(admissionService.getAllApplications()).thenReturn(List.of(app1, app2));
        when(facultyRepository.findAll()).thenReturn(List.of(faculty));

        Model model = new ConcurrentModel();
        
        // Execute - filter 9 to 11 (applicant1 has 10, applicant2 has 12)
        String view = controller.listApplications(null, null, 9, 11, model);

        // Verify
        assertEquals("admin/applications", view);
        List<ApplicationResponse> apps = (List<ApplicationResponse>) model.getAttribute("applications");
        assertEquals(1, apps.size());
        assertEquals("Doe John", apps.get(0).applicantName()); 
        assertEquals(10, apps.get(0).totalScore());
    }

    @Test
    void testUpdateApplicationStatus() {
        String applicantId = "1";
        String facultyName = "IT";
        ApplicationStatus status = ApplicationStatus.ADMITTED;
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        String view = controller.updateApplicationStatus(applicantId, facultyName, status, redirectAttributes);

        assertEquals("redirect:/admin/applications", view);
        assertEquals("Статус заявки оновлено.", redirectAttributes.getFlashAttributes().get("success"));
    }
}
