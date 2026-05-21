package org.example.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Конфігурація кешування з використанням Caffeine.
 * Кешує дані факультетів та рейтингів для зменшення навантаження на БД.
 */
@Configuration
public class CacheConfig {

    /**
     * Створення менеджера кешу (Factory Method pattern).
     * Максимум 200 записів, термін дії — 10 хвилин.
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager("faculties", "rankings");
        manager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(200)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .recordStats());
        return manager;
    }
}
