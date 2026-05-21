package org.example.infrastructure.web.dto;

/**
 * DTO відповіді з даними заявки для відображення на веб-сторінці.
 */
public record ApplicationResponse(String applicantId, String applicantName, String facultyName,
                                   String status, String statusName, int totalScore) {
    public String getApplicantId() { return applicantId(); }
    public String getApplicantName() { return applicantName(); }
    public String getFacultyName() { return facultyName(); }
    public String getStatus() { return status(); }
    public String getStatusName() { return statusName(); }
    public int getTotalScore() { return totalScore(); }
}
