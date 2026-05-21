package org.example.infrastructure.web;

import org.example.application.service.ApplicantService;
import org.example.domain.model.Applicant;
import org.example.domain.model.Application;
import org.example.domain.model.Grade;
import org.example.domain.repository.ApplicationRepository;
import org.example.domain.repository.GradeRepository;
import org.example.infrastructure.web.dto.ResponseMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Контролер управління абітурієнтами для адмін-панелі.
 */
@Controller
@RequestMapping("/admin/applicants")
public class AdminApplicantController {

    private final ApplicantService applicantService;
    private final GradeRepository gradeRepository;
    private final ApplicationRepository applicationRepository;

    public AdminApplicantController(ApplicantService applicantService,
                                   GradeRepository gradeRepository,
                                   ApplicationRepository applicationRepository) {
        this.applicantService = applicantService;
        this.gradeRepository = gradeRepository;
        this.applicationRepository = applicationRepository;
    }

    @GetMapping
    public String listApplicants(Model model) {
        List<Applicant> applicants = applicantService.getAllApplicants();
        model.addAttribute("applicants", applicants.stream()
                .map(a -> ResponseMapper.toApplicantViewModel(a, List.of(), List.of()))
                .toList());
        return "admin/applicants";
    }

    @GetMapping("/{id}")
    public String showApplicantDetails(@PathVariable String id, Model model) {
        applicantService.getApplicant(id).ifPresent(applicant -> {
            List<Grade> grades = gradeRepository.findByApplicantId(id);
            List<Application> applications = applicationRepository.findByApplicantId(id);
            model.addAttribute("applicant", ResponseMapper.toApplicantViewModel(applicant, grades, applications));
        });
        return "admin/applicant-details";
    }
}
