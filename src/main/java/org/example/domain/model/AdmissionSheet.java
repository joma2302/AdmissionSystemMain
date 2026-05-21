package org.example.domain.model;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Доменна сутність «Відомість зарахування».
 * Реєструє заявки на факультет, ранжує за балами та визначає зарахованих/відхилених.
 * Реалізує Strategy pattern — алгоритм зарахування інкапсульований у методі determineAdmitted().
 */
public class AdmissionSheet {

    private final Faculty faculty;
    private final List<Application> applications;

    /**
     * Створює відомість для конкретного факультету.
     *
     * @param faculty факультет для зарахування
     */
    public AdmissionSheet(Faculty faculty) {
        Objects.requireNonNull(faculty, "Факультет не може бути null");
        this.faculty = faculty;
        this.applications = new ArrayList<>();
    }

    /**
     * Реєструє заявку у відомості з перевіркою інваріантів.
     *
     * @param application заявка на факультет
     */
    public void register(Application application) {
        Objects.requireNonNull(application, "Заявка не може бути null");
        if (!application.getFaculty().equals(faculty)) {
            throw new IllegalArgumentException("Заявка належить іншому факультету");
        }
        if (applications.contains(application)) {
            throw new IllegalArgumentException("Заявка вже зареєстрована у відомості");
        }
        applications.add(application);
    }

    /**
     * Визначає зарахованих та відхилених за сумою балів та відповідністю вимогам.
     * Сортує заявки за балами (спадання) та зараховує перших maxStudents,
     * які відповідають усім вимогам факультету.
     */
    public void determineAdmitted() {
        List<Application> sorted = applications.stream()
                .sorted(Comparator.comparingInt(Application::getTotalScore).reversed())
                .collect(Collectors.toList());

        int admittedCount = 0;
        for (Application app : sorted) {
            if (admittedCount < faculty.getMaxStudents() && meetsRequirements(app)) {
                app.admit();
                admittedCount++;
            } else {
                app.reject();
            }
        }
    }

    private boolean meetsRequirements(Application app) {
        List<SubjectRequirement> reqs = faculty.getRequirements();
        if (reqs.isEmpty()) return true;

        List<Grade> grades = app.getApplicant().getGrades();
        for (SubjectRequirement req : reqs) {
            boolean satisfied = grades.stream()
                    .anyMatch(g -> g.getSubject() == req.getSubject() && g.getScore() >= req.getMinimumScore());
            if (!satisfied) return false;
        }
        return true;
    }

    /** Повертає список зарахованих заявок. */
    public List<Application> getAdmitted() {
        return applications.stream()
                .filter(a -> a.getStatus() == ApplicationStatus.ADMITTED)
                .collect(Collectors.toList());
    }

    /** Повертає список відхилених заявок. */
    public List<Application> getRejected() {
        return applications.stream()
                .filter(a -> a.getStatus() == ApplicationStatus.REJECTED)
                .collect(Collectors.toList());
    }

    /** Повертає незмінний список усіх заявок. */
    public List<Application> getApplications() {
        return Collections.unmodifiableList(applications);
    }

    public Faculty getFaculty() { return faculty; }
}
