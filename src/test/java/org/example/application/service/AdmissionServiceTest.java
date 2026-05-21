package org.example.application.service;

import org.example.domain.model.*;
import org.example.domain.repository.ApplicantRepository;
import org.example.domain.repository.ApplicationRepository;
import org.example.domain.repository.ApplicationStatusHistoryRepository;
import org.example.domain.repository.AuditLogRepository;
import org.example.domain.repository.FacultyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdmissionServiceTest {

    private ApplicationRepository applicationRepository;
    private FacultyRepository facultyRepository;
    private ApplicantRepository applicantRepository;
    private ApplicationStatusHistoryRepository statusHistoryRepository;
    private AuditLogRepository auditLogRepository;
    private AdmissionService admissionService;

    @BeforeEach
    void setUp() {
        applicationRepository = mock(ApplicationRepository.class);
        facultyRepository = mock(FacultyRepository.class);
        applicantRepository = mock(ApplicantRepository.class);
        statusHistoryRepository = mock(ApplicationStatusHistoryRepository.class);
        auditLogRepository = mock(AuditLogRepository.class);
        AuditLogService auditLogService = new AuditLogService(auditLogRepository);
        admissionService = new DefaultAdmissionService(applicationRepository, facultyRepository, applicantRepository, statusHistoryRepository, auditLogService);
    }

    @Test
    void testProcessAdmission() {
        // Arrange
        String facultyName = "IT";
        Faculty faculty = new Faculty(facultyName, 1);
        
        Applicant a1 = new Applicant("1", "Ivan", "Ivanov");
        a1.addGrade(new Grade(Subject.MATHEMATICS, 12));
        Application app1 = new Application(a1, faculty);

        Applicant a2 = new Applicant("2", "Petro", "Petrov");
        a2.addGrade(new Grade(Subject.MATHEMATICS, 10));
        Application app2 = new Application(a2, faculty);

        when(facultyRepository.findByName(facultyName)).thenReturn(Optional.of(faculty));
        // Repo returns in desc order by score
        when(applicationRepository.findByFacultyNameOrderByScoreDesc(facultyName)).thenReturn(List.of(app1, app2));

        // Act
        AdmissionSheet result = admissionService.processAdmission(facultyName);

        // Assert
        assertEquals(1, result.getAdmitted().size());
        assertEquals(app1, result.getAdmitted().get(0));
        assertEquals(ApplicationStatus.ADMITTED, app1.getStatus());
        assertEquals(ApplicationStatus.REJECTED, app2.getStatus());

        verify(applicationRepository, times(2)).updateStatus(any(Application.class));
    }

    @Test
    void testGetDashboardStats() {
        // Arrange
        Faculty f1 = new Faculty("IT", 10);
        Applicant a1 = new Applicant("1", "A", "B");
        Application app1 = new Application(a1, f1);
        app1.admit();

        when(facultyRepository.findAll()).thenReturn(List.of(f1));
        when(applicantRepository.findAll()).thenReturn(List.of(a1));
        when(applicationRepository.findAll()).thenReturn(List.of(app1));

        // Act
        AdmissionService.AdminDashboardStats stats = admissionService.getDashboardStats();

        // Assert
        assertEquals(1, stats.applicants());
        assertEquals(1, stats.applications());
        assertEquals(1, stats.admitted());
        assertEquals(10, stats.seats());
        assertEquals(9, stats.freeSeats());
    }

    @Test
    void testUpdateApplicationStatus() {
        // Arrange
        SecurityContextHolder.setContext(new SecurityContextImpl(
                new UsernamePasswordAuthenticationToken("admin", "pass", Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")))
        ));

        // Act
        admissionService.updateApplicationStatus("1", "IT", ApplicationStatus.ADMITTED);

        // Assert
        verify(applicationRepository).updateStatus("1", "IT", ApplicationStatus.ADMITTED);
        verify(auditLogRepository).save(any());
    }
}
