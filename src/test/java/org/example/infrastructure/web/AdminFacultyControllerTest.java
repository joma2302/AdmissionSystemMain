package org.example.infrastructure.web;

import org.example.application.service.AdmissionService;
import org.example.domain.repository.FacultyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AdminFacultyControllerTest {

    private AdminFacultyController controller;
    private AdmissionService admissionService;
    private FacultyRepository facultyRepository;

    @BeforeEach
    void setUp() {
        admissionService = mock(AdmissionService.class);
        facultyRepository = mock(FacultyRepository.class);
        controller = new AdminFacultyController(admissionService, facultyRepository);
    }

    @Test
    void testListFaculties() {
        when(facultyRepository.findAll()).thenReturn(List.of());
        when(admissionService.getFacultyDemand()).thenReturn(List.of());

        Model model = new ConcurrentModel();
        String view = controller.listFaculties(model);

        assertEquals("admin/faculties", view);
        assertEquals(List.of(), model.getAttribute("faculties"));
    }
}
