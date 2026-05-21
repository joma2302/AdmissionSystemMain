package org.example.infrastructure.web.dto;

import org.example.domain.model.Application;

import java.util.List;

/**
 * Маппер доменних моделей у DTO відповідей (Adapter pattern).
 * Перетворює Application у ApplicationResponse для відображення на веб-сторінках.
 */
public final class ResponseMapper {

    private ResponseMapper() {
    }

    /** Перетворює заявку у DTO відповіді. */
    public static ApplicationResponse toResponse(Application application) {
        return new ApplicationResponse(
                application.getApplicant().getId(),
                application.getApplicant().getFullName(),
                application.getFaculty().getName(),
                application.getStatus().getDisplayName(),
                application.getStatus().name(),
                application.getTotalScore());
    }

    /** Перетворює список заявок у список DTO відповідей. */
    public static List<ApplicationResponse> toResponseList(List<org.example.domain.model.Application> applications) {
        return applications.stream().map(ResponseMapper::toResponse).toList();
    }

    /** Створює ViewModel для абітурієнта. */
    public static ApplicantViewModel toApplicantViewModel(org.example.domain.model.Applicant applicant,
                                                          List<org.example.domain.model.Grade> grades,
                                                          List<org.example.domain.model.Application> applications) {
        java.util.Map<String, Integer> gradeMap = new java.util.HashMap<>();
        grades.forEach(g -> gradeMap.put(g.getSubject().getDisplayName(), g.getScore()));

        return new ApplicantViewModel(
                applicant.getId(),
                applicant.getFirstName(),
                applicant.getLastName(),
                applicant.getFullName(),
                applicant.getDocumentsPath(),
                gradeMap,
                toResponseList(applications)
        );
    }

    /** Створює ViewModel для факультету. */
    public static FacultyViewModel toFacultyViewModel(org.example.domain.model.Faculty faculty,
                                                      org.example.application.service.AdmissionService.FacultyDemand demand) {
        List<FacultyViewModel.RequirementViewModel> requirements = faculty != null ? faculty.getRequirements().stream()
                .map(r -> new FacultyViewModel.RequirementViewModel(r.getSubject().getDisplayName(), r.getMinimumScore()))
                .toList() : java.util.Collections.emptyList();

        return new FacultyViewModel(
                faculty != null ? faculty.getName() : (demand != null ? demand.facultyName() : ""),
                faculty != null ? faculty.getMaxStudents() : (demand != null ? demand.seats() : 0),
                requirements,
                demand != null ? demand.applications() : 0,
                demand != null ? demand.competition() : 0.0,
                demand != null ? demand.admitted() : 0
        );
    }
}
