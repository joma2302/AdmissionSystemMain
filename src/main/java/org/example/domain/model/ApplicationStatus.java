package org.example.domain.model;

/**
 * Статус заявки абітурієнта (State pattern).
 * PENDING — очікує розгляду, ADMITTED — зараховано, REJECTED — відхилено.
 */
public enum ApplicationStatus {
    PENDING("Очікує"),
    ADMITTED("Зараховано"),
    REJECTED("Відхилено");

    private final String displayName;

    ApplicationStatus(String displayName) {
        this.displayName = displayName;
    }

    /** Повертає локалізовану назву статусу. */
    public String getDisplayName() {
        return displayName;
    }
}
