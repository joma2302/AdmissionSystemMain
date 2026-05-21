package org.example.domain.model;

import java.util.Objects;

/**
 * Доменна сутність «Заявка на факультет».
 * Зв'язує абітурієнта з факультетом та відстежує статус заявки.
 * Реалізує State pattern — зміна статусу через методи admit()/reject().
 */
public class Application {

    private final Applicant applicant;
    private final Faculty faculty;
    private ApplicationStatus status;

    /**
     * Створює заявку зі статусом PENDING.
     *
     * @param applicant абітурієнт
     * @param faculty   факультет
     */
    public Application(Applicant applicant, Faculty faculty) {
        Objects.requireNonNull(applicant, "Абітурієнт не може бути null");
        Objects.requireNonNull(faculty, "Факультет не може бути null");
        this.applicant = applicant;
        this.faculty = faculty;
        this.status = ApplicationStatus.PENDING;
    }

    /** Зарахувати заявку (State pattern — перехід стану). */
    public void admit() {
        this.status = ApplicationStatus.ADMITTED;
    }

    /** Відхилити заявку (State pattern — перехід стану). */
    public void reject() {
        this.status = ApplicationStatus.REJECTED;
    }

    /** Делегує підрахунок балів абітурієнту. */
    public int getTotalScore() {
        return applicant.getTotalScore();
    }

    public Applicant getApplicant() { return applicant; }
    public Faculty getFaculty() { return faculty; }
    public ApplicationStatus getStatus() { return status; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Application that)) return false;
        return applicant.equals(that.applicant) && faculty.equals(that.faculty);
    }

    @Override
    public int hashCode() { return Objects.hash(applicant, faculty); }

    @Override
    public String toString() {
        return "Application{applicant=" + applicant.getId() + ", faculty=" + faculty.getName() +
                ", status=" + status + ", score=" + getTotalScore() + "}";
    }
}
