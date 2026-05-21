package org.example.application.dto;

import org.example.domain.model.Subject;

import java.util.Map;

/**
 * DTO для передачі даних абітурієнта між шарами (контролер → сервіс).
 *
 * @param id        ідентифікатор абітурієнта
 * @param firstName ім'я
 * @param lastName  прізвище
 * @param grades    оцінки за предметами
 */
public record ApplicantDto(String id, String firstName, String lastName, Map<Subject, Integer> grades) {
}
