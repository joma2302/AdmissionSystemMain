package org.example.infrastructure.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.application.service.AuthService;
import org.example.domain.model.Role;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Spring MVC контролер автентифікації та реєстрації користувачів.
 * Сторінка входу обробляється Spring Security, тут — реєстрація та відображення форми.
 */
@Controller
public class AuthController {

    private static final Logger logger = LogManager.getLogger(AuthController.class);
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /** Форма входу (обробка POST — Spring Security). */
    @GetMapping("/login")
    public String showLoginForm(@RequestParam(required = false) String error,
                                @RequestParam(required = false) String logout,
                                Model model) {
        if (error != null) {
            if ("denied".equals(error)) {
                model.addAttribute("error", "У вас немає прав доступу до цього розділу.");
            } else {
                model.addAttribute("error", "Невірне ім'я користувача або пароль.");
            }
        }
        if (logout != null) {
            model.addAttribute("success", "Ви успішно вийшли з системи");
        }
        return "login";
    }

    /** Форма реєстрації нового користувача. */
    @GetMapping("/signup")
    public String showSignupForm() {
        return "signup";
    }

    /** Обробка реєстрації нового користувача. */
    @PostMapping("/signup")
    public String signup(@RequestParam String username,
                         @RequestParam String password,
                         @RequestParam String role,
                         RedirectAttributes redirectAttributes) {
        try {
            authService.registerUser(username, password, Role.valueOf(role));
            logger.info("Зареєстровано нового користувача через веб: {}", username);
            redirectAttributes.addFlashAttribute("success", "Реєстрація успішна! Увійдіть до системи.");
            return "redirect:/login";
        } catch (Exception e) {
            logger.error("Помилка реєстрації користувача: {}", username, e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/signup";
        }
    }
}
