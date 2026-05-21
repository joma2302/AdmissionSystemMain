package org.example.infrastructure.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.application.service.ApplicationService;
import org.example.domain.model.Application;
import org.example.domain.repository.FacultyRepository;
import org.example.infrastructure.web.dto.ApplicationResponse;
import org.example.infrastructure.web.dto.ResponseMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Spring MVC контролер подачі заявок на факультет.
 * Тонкий контролер — валідує вхідні дані та делегує бізнес-логіку сервісу.
 */
@Controller
public class ApplicationController {

    private static final Logger logger = LogManager.getLogger(ApplicationController.class);
    private final ApplicationService applicationService;
    private final FacultyRepository facultyRepository;

    public ApplicationController(ApplicationService applicationService, FacultyRepository facultyRepository) {
        this.applicationService = applicationService;
        this.facultyRepository = facultyRepository;
    }

    /** Форма подачі заявки на факультет. */
    @GetMapping("/apply")
    public String showApplyForm(Model model) {
        model.addAttribute("faculties", facultyRepository.findAll());
        return "apply";
    }

    /** Обробка подачі заявки. */
    @PostMapping("/apply")
    public String apply(@RequestParam String applicantId,
                        @RequestParam String facultyName,
                        RedirectAttributes redirectAttributes) {
        try {
            applicationService.apply(applicantId, facultyName);
            logger.info("Заявку подано через веб: {} -> {}", applicantId, facultyName);
            redirectAttributes.addFlashAttribute("success", true);
            return "redirect:/apply?success";
        } catch (Exception e) {
            logger.error("Помилка подачі заявки", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/apply";
        }
    }

    /** Перегляд заявок абітурієнта. */
    @GetMapping("/applications")
    public String showApplications(@RequestParam(required = false) String applicantId, Model model) {
        if (applicantId != null && !applicantId.isBlank()) {
            List<Application> apps = applicationService.getApplicationsByApplicant(applicantId);
            List<ApplicationResponse> responses = ResponseMapper.toResponseList(apps);
            model.addAttribute("applications", responses);
            model.addAttribute("applicantId", applicantId);
        }
        return "applications";
    }
}
