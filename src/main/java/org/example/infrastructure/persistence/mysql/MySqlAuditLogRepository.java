package org.example.infrastructure.persistence.mysql;

import org.example.domain.model.AuditLog;
import org.example.domain.repository.AuditLogRepository;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MySqlAuditLogRepository implements AuditLogRepository {

    private final DataSource dataSource;

    public MySqlAuditLogRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(AuditLog log) {
        String sql = "INSERT INTO audit_logs (user_id, action, target, timestamp) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, log.getUserId());
            stmt.setString(2, log.getAction());
            stmt.setString(3, log.getTarget());
            stmt.setTimestamp(4, Timestamp.valueOf(log.getTimestamp()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Помилка збереження логу", e);
        }
    }

    @Override
    public List<AuditLog> findAll() {
        String sql = "SELECT user_id, action, target, timestamp FROM audit_logs ORDER BY timestamp DESC";
        List<AuditLog> logs = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                logs.add(new AuditLog(
                        rs.getString("user_id"),
                        rs.getString("action"),
                        rs.getString("target"),
                        rs.getTimestamp("timestamp").toLocalDateTime()
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка отримання логів", e);
        }
        return logs;
    }
}
