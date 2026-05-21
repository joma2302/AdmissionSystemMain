package org.example.infrastructure.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.application.service.AdmissionService;
import org.example.domain.model.Application;
import org.example.domain.model.ApplicationStatus;
import org.example.domain.repository.FacultyRepository;
import org.example.infrastructure.web.dto.ResponseMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Контролер управління заявками для адмін-панелі.
 */
@Controller
@RequestMapping("/admin/applications")
public class AdminApplicationController {

    private static final Logger logger = LogManager.getLogger(AdminApplicationController.class);
    private final AdmissionService admissionService;
    private final FacultyRepository facultyRepository;

    public AdminApplicationController(AdmissionService admissionService, FacultyRepository facultyRepository) {
        this.admissionService = admissionService;
        this.facultyRepository = facultyRepository;
    }

    @GetMapping
    public String listApplications(@RequestParam(required = false) String facultyName,
                                   @RequestParam(required = false) ApplicationStatus status,
                                   @RequestParam(required = false) Integer minScore,
                                   @RequestParam(required = false) Integer maxScore,
                                   Model model) {
        List<Application> applications = filterApplications(facultyName, status, minScore, maxScore);

        model.addAttribute("applications", ResponseMapper.toResponseList(applications));
        model.addAttribute("faculties", facultyRepository.findAll().stream()
                .map(f -> ResponseMapper.toFacultyViewModel(f, null))
                .toList());
        model.addAttribute("statuses", ApplicationStatus.values());
        model.addAttribute("facultyName", facultyName);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("minScore", minScore);
        model.addAttribute("maxScore", maxScore);
        return "admin/applications";
    }

    @GetMapping("/export")
    public void exportApplications(@RequestParam(required = false) String facultyName,
                                   @RequestParam(required = false) ApplicationStatus status,
                                   @RequestParam(required = false) Integer minScore,
                                   @RequestParam(required = false) Integer maxScore,
                                   jakarta.servlet.http.HttpServletResponse response) throws java.io.IOException {
        List<Application> applications = filterApplications(facultyName, status, minScore, maxScore);

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"applicants.csv\"");

        try (java.io.PrintWriter writer = response.getWriter()) {
            writer.println("Applicant Name,Faculty,Score,Status");
            for (Application app : applications) {
                writer.printf("%s,%s,%d,%s%n",
                        app.getApplicant().getFullName(),
                        app.getFaculty().getName(),
                        app.getTotalScore(),
                        app.getStatus());
            }
        }
    }

    private List<Application> filterApplications(String facultyName, ApplicationStatus status, Integer minScore, Integer maxScore) {
        List<Application> applications = admissionService.getAllApplications();
        if (facultyName != null && !facultyName.isBlank()) {
            applications = applications.stream()
                    .filter(app -> app.getFaculty().getName().equals(facultyName))
                    .toList();
        }
        if (status != null) {
            applications = applications.stream()
                    .filter(app -> app.getStatus() == status)
                    .toList();
        }
        if (minScore != null) {
            applications = applications.stream()
                    .filter(app -> app.getTotalScore() >= minScore)
                    .toList();
        }
        if (maxScore != null) {
            applications = applications.stream()
                    .filter(app -> app.getTotalScore() <= maxScore)
                    .toList();
        }
        return applications;
    }

    @PostMapping("/status")
    public String updateApplicationStatus(@RequestParam String applicantId,
                                          @RequestParam String facultyName,
                                          @RequestParam ApplicationStatus status,
                                          RedirectAttributes redirectAttributes) {
        try {
            admissionService.updateApplicationStatus(applicantId, facultyName, status);
            redirectAttributes.addFlashAttribute("success", "Статус заявки оновлено.");
        } catch (Exception e) {
            logger.error("Помилка ручного оновлення статусу заявки", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/applications";
    }

    @GetMapping("/history/{applicantId}/{facultyName}")
    public String showHistory(@PathVariable String applicantId, @PathVariable String facultyName, Model model) {
        model.addAttribute("history", admissionService.getStatusHistory(applicantId, facultyName));
        model.addAttribute("applicantId", applicantId);
        model.addAttribute("facultyName", facultyName);
        return "admin/application-history";
    }
}
