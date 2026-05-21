package org.example.infrastructure.web;

import org.example.application.service.AdmissionService;
import org.example.domain.repository.FacultyRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RankingController {

    private final AdmissionService admissionService;
    private final FacultyRepository facultyRepository;

    public RankingController(AdmissionService admissionService, FacultyRepository facultyRepository) {
        this.admissionService = admissionService;
        this.facultyRepository = facultyRepository;
    }

    @GetMapping("/ranking")
    public String ranking(@RequestParam(required = false) String facultyName, Model model) {
        model.addAttribute("faculties", facultyRepository.findAll());
        if (facultyName != null && !facultyName.isEmpty()) {
            model.addAttribute("facultyName", facultyName);
            model.addAttribute("ranked", admissionService.getRankedApplications(facultyName));
        }
        return "ranking";
    }
}
