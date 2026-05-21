package org.example.infrastructure.web.validation;

import org.example.infrastructure.web.dto.ApplyRequest;
import org.example.infrastructure.web.dto.RegisterApplicantRequest;

import java.util.Map;

/**
 * Утилітний клас валідації вхідних даних від користувача.
 * Перевіряє обов'язкові поля та діапазони значень перед передачею в сервісний шар.
 */
public final class InputValidator {

    private InputValidator() {
    }

    /** Валідація запиту реєстрації абітурієнта. */
    public static void validate(RegisterApplicantRequest request) {
        requireNonBlank(request.id(), "ID абітурієнта");
        requireNonBlank(request.firstName(), "Ім'я");
        requireNonBlank(request.lastName(), "Прізвище");
        if (request.grades() == null || request.grades().isEmpty()) {
            throw new IllegalArgumentException("Оцінки не можуть бути порожніми");
        }
        for (Map.Entry<String, Integer> entry : request.grades().entrySet()) {
            requireNonBlank(entry.getKey(), "Назва предмету");
            if (entry.getValue() == null || entry.getValue() < 1 || entry.getValue() > 12) {
                throw new IllegalArgumentException(
                        "Бал повинен бути від 1 до 12 для предмету: " + entry.getKey());
            }
        }
    }

    /** Валідація запиту подачі заявки. */
    public static void validate(ApplyRequest request) {
        requireNonBlank(request.applicantId(), "ID абітурієнта");
        requireNonBlank(request.facultyName(), "Назва факультету");
    }

    /** Валідація назви факультету. */
    public static void validateFacultyName(String facultyName) {
        requireNonBlank(facultyName, "Назва факультету");
    }

    /** Перевірка, що рядок не порожній. */
    private static void requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " не може бути порожнім");
        }
    }
}
