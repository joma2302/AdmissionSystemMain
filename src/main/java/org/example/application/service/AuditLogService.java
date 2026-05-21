package org.example.application.service;

import org.example.domain.model.AuditLog;
import org.example.domain.repository.AuditLogRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(String userId, String action, String target) {
        auditLogRepository.save(new AuditLog(userId, action, target, LocalDateTime.now()));
    }

    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAll();
    }
}
