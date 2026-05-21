package org.example.infrastructure.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Глобальний обробник виключень та контролер порад.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

    @ModelAttribute("currentUser")
    public Authentication getCurrentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) ? auth : null;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDeniedException(AccessDeniedException e, Model model) {
        logger.warn("Доступ заборонено: {}", e.getMessage());
        return "403";
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public String handleNotFoundException(NoHandlerFoundException e, Model model) {
        logger.warn("Сторінку не знайдено: {}", e.getRequestURL());
        return "404";
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public String handleNoResourceFoundException(NoResourceFoundException e, Model model) {
        logger.warn("Ресурс не знайдено: {}", e.getResourcePath());
        return "404";
    }

    /** Обробка всіх неперехоплених виключень. */
    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {
        logger.error("Необроблена помилка: {}", e.getMessage(), e);
        model.addAttribute("error", e.getMessage());
        return "error";
    }
}
