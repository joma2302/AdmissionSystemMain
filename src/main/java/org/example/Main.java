package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Точка входу в додаток «Приймальна комісія».
 * Використовує Spring Boot для автоконфігурації всіх компонентів.
 * {@code @EnableCaching} активує підтримку кешування (Caffeine).
 */
@SpringBootApplication
@EnableCaching
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        System.out.println("Сайт запущен: http://localhost:8090");
    }
}
