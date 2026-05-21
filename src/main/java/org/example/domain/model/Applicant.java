package org.example.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Доменна сутність «Абітурієнт».
 * Інкапсулює дані абітурієнта та його оцінки.
 * Забезпечує інваріанти: непорожні поля, унікальність оцінок за предметами.
 */
public class Applicant {

    private final String id;
    private String firstName;
    private String lastName;
    private String documentsPath;
    private final List<Grade> grades;

    /**
     * Створює абітурієнта з валідацією обов'язкових полів.
     *
     * @param id        унікальний ідентифікатор
     * @param firstName ім'я
     * @param lastName  прізвище
     */
    public Applicant(String id, String firstName, String lastName) {
        Objects.requireNonNull(id, "ID не може бути null");
        Objects.requireNonNull(firstName, "Ім'я не може бути null");
        Objects.requireNonNull(lastName, "Прізвище не може бути null");
        if (id.isBlank()) throw new IllegalArgumentException("ID не може бути порожнім");
        if (firstName.isBlank()) throw new IllegalArgumentException("Ім'я не може бути порожнім");
        if (lastName.isBlank()) throw new IllegalArgumentException("Прізвище не може бути порожнім");
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.grades = new ArrayList<>();
    }

    public void setFirstName(String firstName) {
        Objects.requireNonNull(firstName, "Ім'я не може бути null");
        if (firstName.isBlank()) throw new IllegalArgumentException("Ім'я не може бути порожнім");
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        Objects.requireNonNull(lastName, "Прізвище не може бути null");
        if (lastName.isBlank()) throw new IllegalArgumentException("Прізвище не може бути порожнім");
        this.lastName = lastName;
    }

    /**
     * Додає оцінку абітурієнту (без дублювання за предметом).
     *
     * @param grade оцінка за предмет
     */
    public void addGrade(Grade grade) {
        Objects.requireNonNull(grade, "Оцінка не може бути null");
        boolean exists = grades.stream()
                .anyMatch(g -> g.getSubject() == grade.getSubject());
        if (exists) {
            throw new IllegalArgumentException("Оцінка з предмету " + grade.getSubject() + " вже існує");
        }
        grades.add(grade);
    }

    /** Повертає суму всіх балів абітурієнта. */
    public int getTotalScore() {
        return grades.stream().mapToInt(Grade::getScore).sum();
    }

    /** Повертає повне ім'я абітурієнта. */
    public String getFullName() {
        return lastName + " " + firstName;
    }

    public String getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getDocumentsPath() { return documentsPath; }
    public void setDocumentsPath(String documentsPath) { this.documentsPath = documentsPath; }
    public List<Grade> getGrades() { return Collections.unmodifiableList(grades); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Applicant that)) return false;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Applicant{id='" + id + "', name='" + getFullName() + "', totalScore=" + getTotalScore() + "}";
    }
}
