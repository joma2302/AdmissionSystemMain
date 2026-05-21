package org.example.domain.repository;

import org.example.domain.model.AuditLog;
import java.util.List;

public interface AuditLogRepository {
    void save(AuditLog log);
    List<AuditLog> findAll();
}
