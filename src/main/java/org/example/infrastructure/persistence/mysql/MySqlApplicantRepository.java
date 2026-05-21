package org.example.infrastructure.persistence.mysql;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.domain.model.Applicant;
import org.example.domain.model.Grade;
import org.example.domain.model.Subject;
import org.example.domain.repository.ApplicantRepository;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * MySQL-реалізація репозиторію абітурієнтів.
 */
@Repository
public class MySqlApplicantRepository implements ApplicantRepository {

    private static final Logger logger = LogManager.getLogger(MySqlApplicantRepository.class);
    private final DataSource dataSource;

    public MySqlApplicantRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(Applicant applicant) {
        String sql = "INSERT INTO applicants (id, first_name, last_name, documents_path) VALUES (?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE first_name = ?, last_name = ?, documents_path = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, applicant.getId());
            stmt.setString(2, applicant.getFirstName());
            stmt.setString(3, applicant.getLastName());
            stmt.setString(4, applicant.getDocumentsPath());
            stmt.setString(5, applicant.getFirstName());
            stmt.setString(6, applicant.getLastName());
            stmt.setString(7, applicant.getDocumentsPath());
            stmt.executeUpdate();
            logger.debug("Збережено абітурієнта: {}", applicant.getId());
        } catch (SQLException e) {
            logger.error("Помилка збереження абітурієнта: {}", applicant.getId(), e);
            throw new RuntimeException("Помилка збереження абітурієнта", e);
        }
    }

    @Override
    public Optional<Applicant> findById(String id) {
        String sql = "SELECT a.id, a.first_name, a.last_name, a.documents_path, g.subject, g.score " +
                     "FROM applicants a LEFT JOIN grades g ON a.id = g.applicant_id WHERE a.id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                Applicant applicant = null;
                while (rs.next()) {
                    if (applicant == null) {
                        applicant = new Applicant(
                            rs.getString("id"),
                            rs.getString("first_name"),
                            rs.getString("last_name")
                        );
                        applicant.setDocumentsPath(rs.getString("documents_path"));
                    }
                    String subject = rs.getString("subject");
                    if (subject != null) {
                        applicant.addGrade(new Grade(Subject.valueOf(subject), rs.getInt("score")));
                    }
                }
                return Optional.ofNullable(applicant);
            }
        } catch (SQLException e) {
            logger.error("Помилка пошуку абітурієнта за ID: {}", id, e);
            throw new RuntimeException("Помилка пошуку абітурієнта", e);
        }
    }

    @Override
    public List<Applicant> findAll() {
        List<Applicant> result = new ArrayList<>();
        String sql = "SELECT a.id, a.first_name, a.last_name, a.documents_path, g.subject, g.score " +
                     "FROM applicants a LEFT JOIN grades g ON a.id = g.applicant_id ORDER BY a.id";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            Applicant current = null;
            while (rs.next()) {
                String id = rs.getString("id");
                if (current == null || !current.getId().equals(id)) {
                    current = new Applicant(
                        id,
                        rs.getString("first_name"),
                        rs.getString("last_name")
                    );
                    current.setDocumentsPath(rs.getString("documents_path"));
                    result.add(current);
                }
                String subject = rs.getString("subject");
                if (subject != null) {
                    current.addGrade(new Grade(Subject.valueOf(subject), rs.getInt("score")));
                }
            }
        } catch (SQLException e) {
            logger.error("Помилка отримання всіх абітурієнтів", e);
            throw new RuntimeException("Помилка отримання всіх абітурієнтів", e);
        }
        return result;
    }

    @Override
    public List<Applicant> findByFacultyName(String facultyName) {
        List<Applicant> result = new ArrayList<>();
        String sql = "SELECT a.id, a.first_name, a.last_name, a.documents_path, g.subject, g.score " +
                     "FROM applicants a " +
                     "INNER JOIN applications app ON a.id = app.applicant_id " +
                     "LEFT JOIN grades g ON a.id = g.applicant_id " +
                     "WHERE app.faculty_name = ? ORDER BY a.id";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, facultyName);
            try (ResultSet rs = stmt.executeQuery()) {
                Applicant current = null;
                while (rs.next()) {
                    String id = rs.getString("id");
                    if (current == null || !current.getId().equals(id)) {
                        current = new Applicant(
                            id,
                            rs.getString("first_name"),
                            rs.getString("last_name")
                        );
                        current.setDocumentsPath(rs.getString("documents_path"));
                        result.add(current);
                    }
                    String subject = rs.getString("subject");
                    if (subject != null) {
                        current.addGrade(new Grade(Subject.valueOf(subject), rs.getInt("score")));
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Помилка пошуку абітурієнтів за факультетом: {}", facultyName, e);
            throw new RuntimeException("Помилка пошуку абітурієнтів", e);
        }
        return result;
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM applicants WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
            logger.debug("Видалено абітурієнта: {}", id);
        } catch (SQLException e) {
            logger.error("Помилка видалення абітурієнта: {}", id, e);
            throw new RuntimeException("Помилка видалення абітурієнта", e);
        }
    }
}
