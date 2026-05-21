package org.example.config;

import org.example.infrastructure.persistence.mysql.DatabaseInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Конфігурація бази даних.
 * Автоматично створює таблиці та seed-дані при запуску додатку.
 * Використовує HikariCP пул з'єднань (налаштований в application.properties).
 */
@Configuration
public class DatabaseConfig {

    private static final Logger logger = LogManager.getLogger(DatabaseConfig.class);

    /**
     * Ініціалізація БД при старті додатку (Command pattern — виконання команди ініціалізації).
     *
     * @param dataSource джерело даних з пулом з'єднань HikariCP
     * @return CommandLineRunner для виконання при старті
     */
    @Bean
    public CommandLineRunner initDatabase(DataSource dataSource) {
        return args -> {
            logger.info("Ініціалізація бази даних...");
            try (Connection connection = dataSource.getConnection()) {
                DatabaseInitializer.initialize(connection);
                logger.info("База даних успішно ініціалізована");
            } catch (Exception e) {
                logger.error("Помилка ініціалізації бази даних", e);
                throw e;
            }
        };
    }
}
