package org.example.infrastructure.web;

import org.example.application.service.ApplicantService;
import org.example.domain.model.Applicant;
import org.example.domain.model.Subject;
import org.example.domain.repository.ApplicationRepository;
import org.example.domain.repository.GradeRepository;
import org.example.infrastructure.web.dto.ApplicantViewModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AdminApplicantControllerTest {

    private AdminApplicantController controller;
    private ApplicantService applicantService;
    private GradeRepository gradeRepository;
    private ApplicationRepository applicationRepository;

    @BeforeEach
    void setUp() {
        applicantService = mock(ApplicantService.class);
        gradeRepository = mock(GradeRepository.class);
        applicationRepository = mock(ApplicationRepository.class);
        controller = new AdminApplicantController(applicantService, gradeRepository, applicationRepository);
    }

    @Test
    void testListApplicants() {
        List<Applicant> applicants = List.of(new Applicant("1", "Ivan", "Ivanov"));
        when(applicantService.getAllApplicants()).thenReturn(applicants);

        Model model = new ConcurrentModel();
        String view = controller.listApplicants(model);

        assertEquals("admin/applicants", view);
        List<?> modelApplicants = (List<?>) model.getAttribute("applicants");
        assertEquals(1, modelApplicants.size());
        assertTrue(modelApplicants.get(0) instanceof ApplicantViewModel);
        assertEquals("Ivanov Ivan", ((ApplicantViewModel)modelApplicants.get(0)).fullName());
    }

    @Test
    void testShowApplicantDetails() {
        String id = "1";
        Applicant applicant = new Applicant(id, "Ivan", "Ivanov");
        when(applicantService.getApplicant(id)).thenReturn(Optional.of(applicant));
        when(gradeRepository.findByApplicantId(id)).thenReturn(List.of());
        when(applicationRepository.findByApplicantId(id)).thenReturn(List.of());

        Model model = new ConcurrentModel();
        String view = controller.showApplicantDetails(id, model);

        assertEquals("admin/applicant-details", view);
        assertTrue(model.getAttribute("applicant") instanceof ApplicantViewModel);
        ApplicantViewModel vm = (ApplicantViewModel) model.getAttribute("applicant");
        assertEquals("Ivanov Ivan", vm.fullName());
    }
}
