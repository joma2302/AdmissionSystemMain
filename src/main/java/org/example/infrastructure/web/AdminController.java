package org.example.infrastructure.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.application.service.AdmissionService;
import org.example.domain.model.AdmissionSheet;
import org.example.domain.repository.FacultyRepository;
import org.example.infrastructure.web.dto.AdmissionResultResponse;
import org.example.infrastructure.web.dto.ResponseMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Spring MVC контролер адміністратора.
 * Забезпечує загальну статистику (dashboard) та процес зарахування.
 */
@Controller
public class AdminController {

    private static final Logger logger = LogManager.getLogger(AdminController.class);
    private final AdmissionService admissionService;
    private final FacultyRepository facultyRepository;

    public AdminController(AdmissionService admissionService, FacultyRepository facultyRepository) {
        this.admissionService = admissionService;
        this.facultyRepository = facultyRepository;
    }

    /** Бізнес-панель адміністратора зі зведенням приймальної кампанії. */
    @GetMapping("/admin")
    public String dashboard(Model model) {
        model.addAttribute("stats", admissionService.getDashboardStats());
        model.addAttribute("facultyDemand", admissionService.getFacultyDemand().stream()
                .map(d -> ResponseMapper.toFacultyViewModel(null, d))
                .toList());
        return "admin/dashboard";
    }

    /** Форма зарахування на факультет. */
    @GetMapping("/admission")
    public String showAdmissionForm(Model model) {
        model.addAttribute("faculties", facultyRepository.findAll());
        return "admission";
    }

    /** Обробка зарахування. */
    @PostMapping("/admission")
    public String processAdmission(@RequestParam String facultyName, Model model) {
        try {
            AdmissionSheet sheet = admissionService.processAdmission(facultyName);
            AdmissionResultResponse result = new AdmissionResultResponse(
                    facultyName,
                    ResponseMapper.toResponseList(sheet.getAdmitted()),
                    ResponseMapper.toResponseList(sheet.getRejected()));
            model.addAttribute("result", result);
            logger.info("Зарахування проведено для факультету: {}", facultyName);
        } catch (Exception e) {
            logger.error("Помилка зарахування", e);
            model.addAttribute("error", e.getMessage());
        }
        model.addAttribute("faculties", facultyRepository.findAll());
        return "admission";
    }
}
