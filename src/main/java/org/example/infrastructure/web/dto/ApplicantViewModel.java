package org.example.infrastructure.web.dto;

import java.util.List;
import java.util.Map;

/**
 * DTO для відображення деталей абітурієнта в адмін-панелі.
 */
public record ApplicantViewModel(
    String id,
    String firstName,
    String lastName,
    String fullName,
    String documentsPath,
    Map<String, Integer> grades,
    List<ApplicationResponse> applications
) {}
