package org.example.infrastructure.web.dto;

import java.util.Map;

/**
 * DTO запиту на реєстрацію абітурієнта з оцінками.
 */
public record RegisterApplicantRequest(String id, String firstName, String lastName,
                                        Map<String, Integer> grades) {
}
