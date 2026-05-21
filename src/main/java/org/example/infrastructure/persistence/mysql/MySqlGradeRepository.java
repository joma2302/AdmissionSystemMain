package org.example.infrastructure.persistence.mysql;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.domain.model.Grade;
import org.example.domain.model.Subject;
import org.example.domain.repository.GradeRepository;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * MySQL-реалізація репозиторію оцінок.
 */
@Repository
public class MySqlGradeRepository implements GradeRepository {

    private static final Logger logger = LogManager.getLogger(MySqlGradeRepository.class);
    private final DataSource dataSource;

    public MySqlGradeRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(String applicantId, Grade grade) {
        String sql = "INSERT INTO grades (applicant_id, subject, score) VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE score = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, applicantId);
            stmt.setString(2, grade.getSubject().name());
            stmt.setInt(3, grade.getScore());
            stmt.setInt(4, grade.getScore());
            stmt.executeUpdate();
            logger.debug("Збережено оцінку: {} -> {} = {}", applicantId, grade.getSubject(), grade.getScore());
        } catch (SQLException e) {
            logger.error("Помилка збереження оцінки для абітурієнта: {}", applicantId, e);
            throw new RuntimeException("Помилка збереження оцінки", e);
        }
    }

    @Override
    public List<Grade> findByApplicantId(String applicantId) {
        List<Grade> grades = new ArrayList<>();
        String sql = "SELECT subject, score FROM grades WHERE applicant_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, applicantId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    grades.add(new Grade(
                        Subject.valueOf(rs.getString("subject")),
                        rs.getInt("score")
                    ));
                }
            }
        } catch (SQLException e) {
            logger.error("Помилка отримання оцінок для абітурієнта: {}", applicantId, e);
            throw new RuntimeException("Помилка отримання оцінок", e);
        }
        return grades;
    }

    @Override
    public void deleteByApplicantId(String applicantId) {
        String sql = "DELETE FROM grades WHERE applicant_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, applicantId);
            stmt.executeUpdate();
            logger.debug("Видалено всі оцінки для абітурієнта: {}", applicantId);
        } catch (SQLException e) {
            logger.error("Помилка видалення оцінок для абітурієнта: {}", applicantId, e);
            throw new RuntimeException("Помилка видалення оцінок", e);
        }
    }
}
