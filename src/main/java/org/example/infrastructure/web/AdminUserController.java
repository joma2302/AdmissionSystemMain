package org.example.infrastructure.web;

import org.example.application.service.AuthService;
import org.example.domain.model.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private final AuthService authService;

    public AdminUserController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String listUsers(Model model) {
        model.addAttribute("users", authService.findAllUsers());
        return "admin/users";
    }

    @PostMapping("/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    public String resetPassword(@RequestParam String username, @RequestParam String newPassword) {
        authService.resetPassword(username, newPassword);
        return "redirect:/admin/users?success=true";
    }
}
