package org.example.infrastructure.persistence.mysql;

import org.example.domain.model.ApplicationStatus;
import org.example.domain.model.ApplicationStatusHistory;
import org.example.domain.repository.ApplicationStatusHistoryRepository;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MySqlApplicationStatusHistoryRepository implements ApplicationStatusHistoryRepository {

    private final DataSource dataSource;

    public MySqlApplicationStatusHistoryRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(ApplicationStatusHistory history) {
        String sql = "INSERT INTO application_status_history (applicant_id, faculty_name, status, changed_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, history.getApplicantId());
            stmt.setString(2, history.getFacultyName());
            stmt.setString(3, history.getStatus().name());
            stmt.setTimestamp(4, Timestamp.valueOf(history.getChangedAt()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Помилка збереження історії статусу", e);
        }
    }

    @Override
    public List<ApplicationStatusHistory> findByApplication(String applicantId, String facultyName) {
        String sql = "SELECT status, changed_at FROM application_status_history WHERE applicant_id = ? AND faculty_name = ? ORDER BY changed_at DESC";
        List<ApplicationStatusHistory> history = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, applicantId);
            stmt.setString(2, facultyName);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    history.add(new ApplicationStatusHistory(
                            applicantId,
                            facultyName,
                            ApplicationStatus.valueOf(rs.getString("status")),
                            rs.getTimestamp("changed_at").toLocalDateTime()
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка отримання історії статусів", e);
        }
        return history;
    }
}
