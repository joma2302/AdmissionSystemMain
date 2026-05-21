package org.example.domain.model;

import java.time.LocalDateTime;

public class ApplicationStatusHistory {
    private final String applicantId;
    private final String facultyName;
    private final ApplicationStatus status;
    private final LocalDateTime changedAt;

    public ApplicationStatusHistory(String applicantId, String facultyName, ApplicationStatus status, LocalDateTime changedAt) {
        this.applicantId = applicantId;
        this.facultyName = facultyName;
        this.status = status;
        this.changedAt = changedAt;
    }

    public String getApplicantId() { return applicantId; }
    public String getFacultyName() { return facultyName; }
    public ApplicationStatus getStatus() { return status; }
    public LocalDateTime getChangedAt() { return changedAt; }
}
