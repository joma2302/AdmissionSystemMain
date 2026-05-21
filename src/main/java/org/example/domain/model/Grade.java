package org.example.domain.model;

import java.util.Objects;

/**
 * Value Object «Оцінка за предмет».
 * Інкапсулює предмет та бал з валідацією діапазону (1–12).
 */
public class Grade {

    private final Subject subject;
    private final int score;

    /**
     * Створює оцінку з валідацією.
     *
     * @param subject предмет
     * @param score   бал (від 1 до 12)
     */
    public Grade(Subject subject, int score) {
        Objects.requireNonNull(subject, "Предмет не може бути null");
        if (score < 1 || score > 12) {
            throw new IllegalArgumentException("Бал повинен бути від 1 до 12, отримано: " + score);
        }
        this.subject = subject;
        this.score = score;
    }

    public Subject getSubject() { return subject; }
    public int getScore() { return score; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Grade grade)) return false;
        return score == grade.score && subject == grade.subject;
    }

    @Override
    public int hashCode() { return Objects.hash(subject, score); }

    @Override
    public String toString() {
        return "Grade{subject=" + subject + ", score=" + score + "}";
    }
}
