package org.example.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Доменна сутність «Факультет».
 * Визначає назву факультету та максимальну кількість місць для зарахування.
 */
public class Faculty {

    private final String name;
    private final int maxStudents;
    private final List<SubjectRequirement> requirements;

    /**
     * Створює факультет з валідацією інваріантів.
     *
     * @param name        назва факультету (не порожня)
     * @param maxStudents максимальна кількість місць (більше 0)
     */
    public Faculty(String name, int maxStudents) {
        Objects.requireNonNull(name, "Назва факультету не може бути null");
        if (name.isBlank()) throw new IllegalArgumentException("Назва факультету не може бути порожньою");
        if (maxStudents <= 0) throw new IllegalArgumentException("Кількість місць повинна бути більше 0");
        this.name = name;
        this.maxStudents = maxStudents;
        this.requirements = new ArrayList<>();
    }

    /**
     * Додає вимогу до предмету для цього факультету.
     *
     * @param requirement вимога до предмету
     */
    public void addRequirement(SubjectRequirement requirement) {
        Objects.requireNonNull(requirement, "Вимога не може бути null");
        requirements.add(requirement);
    }

    public String getName() { return name; }
    public int getMaxStudents() { return maxStudents; }
    public List<SubjectRequirement> getRequirements() { return Collections.unmodifiableList(requirements); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Faculty that)) return false;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() { return Objects.hash(name); }

    @Override
    public String toString() {
        return "Faculty{name='" + name + "', maxStudents=" + maxStudents + "}";
    }
}
