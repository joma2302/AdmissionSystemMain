package org.example.infrastructure.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.application.service.AdmissionService;
import org.example.domain.model.Faculty;
import org.example.domain.model.Subject;
import org.example.domain.model.SubjectRequirement;
import org.example.domain.repository.FacultyRepository;
import org.example.infrastructure.web.dto.ResponseMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Контролер управління факультетами для адмін-панелі.
 */
@Controller
@RequestMapping("/admin/faculties")
public class AdminFacultyController {

    private static final Logger logger = LogManager.getLogger(AdminFacultyController.class);
    private final AdmissionService admissionService;
    private final FacultyRepository facultyRepository;

    public AdminFacultyController(AdmissionService admissionService, FacultyRepository facultyRepository) {
        this.admissionService = admissionService;
        this.facultyRepository = facultyRepository;
    }

    @GetMapping
    public String listFaculties(Model model) {
        List<Faculty> faculties = facultyRepository.findAll();
        Map<String, AdmissionService.FacultyDemand> demandMap = admissionService.getFacultyDemand().stream()
                .collect(Collectors.toMap(AdmissionService.FacultyDemand::facultyName, d -> d));

        model.addAttribute("faculties", faculties.stream()
                .map(f -> ResponseMapper.toFacultyViewModel(f, demandMap.get(f.getName())))
                .toList());
        model.addAttribute("subjects", Subject.values());
        return "admin/faculties";
    }

    @PostMapping("/add")
    public String addFaculty(@RequestParam String name, @RequestParam int maxStudents) {
        facultyRepository.save(new Faculty(name, maxStudents));
        return "redirect:/admin/faculties";
    }

    @PostMapping("/requirements/add")
    public String addRequirement(@RequestParam String facultyName,
                                 @RequestParam Subject subject,
                                 @RequestParam int minimumScore,
                                 RedirectAttributes redirectAttributes) {
        try {
            facultyRepository.findByName(facultyName).ifPresent(faculty -> {
                faculty.addRequirement(new SubjectRequirement(subject, minimumScore));
                facultyRepository.save(faculty);
            });
            redirectAttributes.addFlashAttribute("success", "Вимогу додано.");
        } catch (Exception e) {
            logger.error("Помилка додавання вимоги", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/faculties";
    }

    @PostMapping("/requirements/clear")
    public String clearRequirements(@RequestParam String facultyName) {
        facultyRepository.findByName(facultyName).ifPresent(faculty -> {
            Faculty f = new Faculty(faculty.getName(), faculty.getMaxStudents());
            facultyRepository.save(f);
        });
        return "redirect:/admin/faculties";
    }

    @PostMapping("/delete")
    public String deleteFaculty(@RequestParam String name) {
        facultyRepository.deleteByName(name);
        return "redirect:/admin/faculties";
    }
}
