package org.example.infrastructure.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.application.dto.ApplicantDto;
import org.example.application.service.ApplicationService;
import org.example.application.service.ApplicantService;
import org.example.domain.model.Applicant;
import org.example.domain.model.Subject;
import org.example.infrastructure.web.validation.InputValidator;
import org.example.infrastructure.web.dto.RegisterApplicantRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Spring MVC контролер реєстрації абітурієнтів.
 * Тонкий контролер — лише приймає дані, валідує та делегує сервісу.
 */
@Controller
public class ApplicantController {

    private static final Logger logger = LogManager.getLogger(ApplicantController.class);
    private final ApplicantService applicantService;
    private final ApplicationService applicationService;

    public ApplicantController(ApplicantService applicantService, ApplicationService applicationService) {
        this.applicantService = applicantService;
        this.applicationService = applicationService;
    }

    /** Форма реєстрації абітурієнта. */
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("subjects", Subject.values());
        return "register";
    }

    /** Обробка реєстрації абітурієнта. */
    @PostMapping("/register")
    public String register(@RequestParam String id,
                           @RequestParam String firstName,
                           @RequestParam String lastName,
                           @RequestParam Map<String, String> allParams,
                           RedirectAttributes redirectAttributes) {
        try {
            // Збір оцінок з параметрів форми (grade_SUBJECT_NAME)
            Map<String, Integer> grades = new HashMap<>();
            for (Map.Entry<String, String> entry : allParams.entrySet()) {
                if (entry.getKey().startsWith("grade_") && !entry.getValue().isBlank()) {
                    String subject = entry.getKey().substring(6);
                    grades.put(subject, Integer.parseInt(entry.getValue()));
                }
            }

            RegisterApplicantRequest request = new RegisterApplicantRequest(id, firstName, lastName, grades);
            InputValidator.validate(request);

            // Перетворення назв предметів у enum (Strategy pattern — маппінг)
            Map<Subject, Integer> subjectGrades = new EnumMap<>(Subject.class);
            grades.forEach((key, value) -> subjectGrades.put(Subject.valueOf(key), value));

            ApplicantDto dto = new ApplicantDto(id, firstName, lastName, subjectGrades);
            applicantService.registerApplicant(dto);

            logger.info("Абітурієнта зареєстровано через веб: {}", id);
            redirectAttributes.addFlashAttribute("success", true);
            return "redirect:/register?success";
        } catch (Exception e) {
            logger.error("Помилка реєстрації абітурієнта", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }

    /** Форма завантаження документів. */
    @GetMapping("/upload")
    public String showUploadForm() {
        return "upload";
    }

    /** Форма редагування профілю. */
    @GetMapping("/profile/edit")
    public String showEditProfileForm(@RequestParam String applicantId, Model model) {
        var applicant = applicantService.getApplicant(applicantId)
                .orElseThrow(() -> new IllegalArgumentException("Абітурієнт не знайдений"));
        
        // Перевірка наявності заявок
        boolean hasApplications = !applicationService.getApplicationsByApplicant(applicantId).isEmpty();
        
        model.addAttribute("applicant", applicant);
        model.addAttribute("hasApplications", hasApplications);
        return "edit-profile";
    }

    /** Обробка оновлення профілю. */
    @PostMapping("/profile/edit")
    public String updateProfile(@RequestParam String applicantId,
                                @RequestParam String firstName,
                                @RequestParam String lastName,
                                RedirectAttributes redirectAttributes) {
        try {
            var applicant = applicantService.getApplicant(applicantId)
                    .orElseThrow(() -> new IllegalArgumentException("Абітурієнт не знайдений"));
            
            // Перевірка наявності заявок
            if (!applicationService.getApplicationsByApplicant(applicantId).isEmpty()) {
                throw new IllegalStateException("Ви не можете редагувати профіль після подачі заявки");
            }

            applicant.setFirstName(firstName);
            applicant.setLastName(lastName);
            applicantService.updateApplicant(applicant);

            logger.info("Профіль оновлено для абітурієнта: {}", applicantId);
            redirectAttributes.addFlashAttribute("success", true);
            return "redirect:/profile/edit?applicantId=" + applicantId + "&success";
        } catch (Exception e) {
            logger.error("Помилка оновлення профілю", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/profile/edit?applicantId=" + applicantId;
        }
    }

    /** Обробка завантаження документів. */
    @PostMapping("/upload")
    public String uploadDocuments(@RequestParam String applicantId,
                                  @RequestParam("file") MultipartFile file,
                                  RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Файл порожній");
            return "redirect:/upload";
        }

        try {
            org.example.domain.model.Applicant applicant = applicantService.getApplicant(applicantId)
                    .orElseThrow(() -> new IllegalArgumentException("Абітурієнт не знайдений"));

            // Спрощене збереження файлу (у папку uploads)
            String fileName = applicantId + "_" + file.getOriginalFilename();
            Path path = Paths.get("uploads/" + fileName);
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());

            applicant.setDocumentsPath(path.toString());
            // Потрібно зберегти зміни в БД
            applicantService.updateApplicant(applicant);

            logger.info("Документи завантажено для абітурієнта: {}", applicantId);
            redirectAttributes.addFlashAttribute("success", true);
            return "redirect:/upload?success";
        } catch (Exception e) {
            logger.error("Помилка завантаження документів", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/upload";
        }
    }
}
