package org.example.infrastructure.web.dto;

import java.util.List;

/**
 * DTO для відображення факультету в адмін-панелі.
 */
public record FacultyViewModel(
    String name,
    int maxStudents,
    List<RequirementViewModel> requirements,
    int applicationCount,
    double competition,
    int admittedCount
) {
    public record RequirementViewModel(String subject, int minimumScore) {}
}
