package org.example.domain.model;

import java.util.Objects;

/**
 * Доменна сутність «Предметна вимога».
 * Визначає обов'язковий предмет для вступу на факультет та мінімальний бал.
 * Використовується адміністратором для налаштування правил вступу.
 */
public class SubjectRequirement {

    private final Subject subject;
    private final int minimumScore;

    /**
     * Створює вимогу до предмету.
     *
     * @param subject      предмет
     * @param minimumScore мінімальний допустимий бал (зазвичай від 0 до 200)
     */
    public SubjectRequirement(Subject subject, int minimumScore) {
        Objects.requireNonNull(subject, "Предмет не може бути null");
        if (minimumScore < 0 || minimumScore > 200) {
            throw new IllegalArgumentException("Мінімальний бал повинен бути в межах від 0 до 200");
        }
        this.subject = subject;
        this.minimumScore = minimumScore;
    }

    public Subject getSubject() { return subject; }
    public int getMinimumScore() { return minimumScore; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubjectRequirement that)) return false;
        return minimumScore == that.minimumScore && subject == that.subject;
    }

    @Override
    public int hashCode() {
        return Objects.hash(subject, minimumScore);
    }

    @Override
    public String toString() {
        return "SubjectRequirement{subject=" + subject + ", minScore=" + minimumScore + "}";
    }
}
