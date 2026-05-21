package org.example.infrastructure.web.dto;

/**
 * DTO запиту на подачу заявки на факультет.
 */
public record ApplyRequest(String applicantId, String facultyName) {
}
