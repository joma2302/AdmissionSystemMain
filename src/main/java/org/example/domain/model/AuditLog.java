package org.example.domain.model;

import java.time.LocalDateTime;

public class AuditLog {
    private final String userId;
    private final String action;
    private final String target;
    private final LocalDateTime timestamp;

    public AuditLog(String userId, String action, String target, LocalDateTime timestamp) {
        this.userId = userId;
        this.action = action;
        this.target = target;
        this.timestamp = timestamp;
    }

    public String getUserId() { return userId; }
    public String getAction() { return action; }
    public String getTarget() { return target; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getFormattedTimestamp() { return timestamp.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); }
}
