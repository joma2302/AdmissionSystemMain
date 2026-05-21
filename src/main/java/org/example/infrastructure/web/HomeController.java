package org.example.infrastructure.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Контролер головної сторінки.
 * Відображає стартову сторінку системи «Приймальна комісія».
 */
@Controller
public class HomeController {

    /** Головна сторінка. */
    @GetMapping("/")
    public String index() {
        return "index";
    }
}
