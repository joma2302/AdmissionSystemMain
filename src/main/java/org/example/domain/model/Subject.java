package org.example.domain.model;

/**
 * Перелік навчальних предметів для оцінювання абітурієнтів.
 * Кожен предмет має локалізовану назву для відображення в UI.
 */
public enum Subject {
    MATHEMATICS("Математика"),
    UKRAINIAN_LANGUAGE("Українська мова"),
    HISTORY("Історія"),
    PHYSICS("Фізика"),
    CHEMISTRY("Хімія"),
    BIOLOGY("Біологія"),
    ENGLISH("Англійська мова"),
    GEOGRAPHY("Географія"),
    INFORMATICS("Інформатика"),
    LITERATURE("Література");

    private final String displayName;

    Subject(String displayName) {
        this.displayName = displayName;
    }

    /** Повертає локалізовану назву предмету. */
    public String getDisplayName() {
        return displayName;
    }
}
