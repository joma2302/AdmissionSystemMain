package org.example.infrastructure.persistence.mysql;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.domain.model.*;
import org.example.domain.repository.ApplicationRepository;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * MySQL-реалізація репозиторію заявок.
 * Використовує JDBC з пулом з'єднань HikariCP через DataSource.
 */
@Repository
public class MySqlApplicationRepository implements ApplicationRepository {

    private static final Logger logger = LogManager.getLogger(MySqlApplicationRepository.class);
    private final DataSource dataSource;

    public MySqlApplicationRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /** Зберігає або оновлює заявку у БД. */
    @Override
    public void save(Application application) {
        String sql = "INSERT INTO applications (applicant_id, faculty_name, status) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE status = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, application.getApplicant().getId());
            stmt.setString(2, application.getFaculty().getName());
            stmt.setString(3, application.getStatus().name());
            stmt.setString(4, application.getStatus().name());
            stmt.executeUpdate();
            logger.debug("Збережено заявку: {} -> {}", application.getApplicant().getId(),
                    application.getFaculty().getName());
        } catch (SQLException e) {
            logger.error("Помилка збереження заявки", e);
            throw new RuntimeException("Помилка збереження заявки", e);
        }
    }

    /** Знаходить заявки за назвою факультету. */
    @Override
    public List<Application> findByFacultyName(String facultyName) {
        return findApplications("WHERE app.faculty_name = ? ORDER BY a.id", facultyName);
    }

    /** Знаходить заявки за ID абітурієнта. */
    @Override
    public List<Application> findByApplicantId(String applicantId) {
        return findApplications("WHERE app.applicant_id = ? ORDER BY app.faculty_name", applicantId);
    }

    /** Повертає всі заявки для адміністративної панелі. */
    @Override
    public List<Application> findAll() {
        return findApplications("ORDER BY f.name, a.last_name, a.first_name");
    }

    /** Знаходить заявки за факультетом, відсортовані за сумою балів (для ранжування). */
    @Override
    public List<Application> findByFacultyNameOrderByScoreDesc(String facultyName) {
        String sql = "SELECT a.id, a.first_name, a.last_name, " +
                "f.name AS faculty_name, f.max_students, app.status, " +
                "g.subject, g.score " +
                "FROM applications app " +
                "INNER JOIN applicants a ON app.applicant_id = a.id " +
                "INNER JOIN faculties f ON app.faculty_name = f.name " +
                "LEFT JOIN grades g ON a.id = g.applicant_id " +
                "WHERE app.faculty_name = ? " +
                "ORDER BY (SELECT COALESCE(SUM(g2.score), 0) FROM grades g2 WHERE g2.applicant_id = a.id) DESC, a.id";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, facultyName);
            try (ResultSet rs = stmt.executeQuery()) {
                return mapApplications(rs);
            }
        } catch (SQLException e) {
            logger.error("Помилка отримання заявок для ранжування: {}", facultyName, e);
            throw new RuntimeException("Помилка отримання заявок для ранжування", e);
        }
    }

    /** Оновлює статус заявки (PENDING → ADMITTED / REJECTED). */
    @Override
    public void updateStatus(Application application) {
        updateStatus(application.getApplicant().getId(), application.getFaculty().getName(), application.getStatus());
    }

    /** Оновлює статус заявки за ключем. */
    @Override
    public void updateStatus(String applicantId, String facultyName, ApplicationStatus status) {
        String sql = "UPDATE applications SET status = ? WHERE applicant_id = ? AND faculty_name = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setString(2, applicantId);
            stmt.setString(3, facultyName);
            stmt.executeUpdate();
            logger.debug("Оновлено статус заявки: {} -> {} = {}",
                    applicantId, facultyName, status);
        } catch (SQLException e) {
            logger.error("Помилка оновлення статусу заявки", e);
            throw new RuntimeException("Помилка оновлення статусу заявки", e);
        }
    }

    /** Загальний метод пошуку заявок без параметрів. */
    private List<Application> findApplications(String orderClause) {
        String sql = "SELECT a.id, a.first_name, a.last_name, " +
                "f.name AS faculty_name, f.max_students, app.status, " +
                "g.subject, g.score " +
                "FROM applications app " +
                "INNER JOIN applicants a ON app.applicant_id = a.id " +
                "INNER JOIN faculties f ON app.faculty_name = f.name " +
                "LEFT JOIN grades g ON a.id = g.applicant_id " +
                orderClause;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return mapApplications(rs);
        } catch (SQLException e) {
            logger.error("Помилка отримання заявок", e);
            throw new RuntimeException("Помилка отримання заявок", e);
        }
    }

    /** Загальний метод пошуку заявок з JOIN-ами. */
    private List<Application> findApplications(String whereClause, String param) {
        String sql = "SELECT a.id, a.first_name, a.last_name, " +
                "f.name AS faculty_name, f.max_students, app.status, " +
                "g.subject, g.score " +
                "FROM applications app " +
                "INNER JOIN applicants a ON app.applicant_id = a.id " +
                "INNER JOIN faculties f ON app.faculty_name = f.name " +
                "LEFT JOIN grades g ON a.id = g.applicant_id " +
                whereClause;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, param);
            try (ResultSet rs = stmt.executeQuery()) {
                return mapApplications(rs);
            }
        } catch (SQLException e) {
            logger.error("Помилка отримання заявок", e);
            throw new RuntimeException("Помилка отримання заявок", e);
        }
    }

    /**
     * Маппінг ResultSet у список заявок (Builder pattern — поетапна побудова складного об'єкта).
     */
    private List<Application> mapApplications(ResultSet rs) throws SQLException {
        List<Application> result = new ArrayList<>();
        Application current = null;
        String currentKey = null;

        while (rs.next()) {
            String applicantId = rs.getString("id");
            String facultyName = rs.getString("faculty_name");
            String key = applicantId + "|" + facultyName;

            if (!key.equals(currentKey)) {
                Applicant applicant = new Applicant(
                        applicantId, rs.getString("first_name"), rs.getString("last_name"));
                Faculty faculty = new Faculty(facultyName, rs.getInt("max_students"));
                current = new Application(applicant, faculty);

                // Відновлення статусу з БД (State pattern)
                String status = rs.getString("status");
                if ("ADMITTED".equals(status)) {
                    current.admit();
                } else if ("REJECTED".equals(status)) {
                    current.reject();
                }

                result.add(current);
                currentKey = key;
            }

            String subjectName = rs.getString("subject");
            if (subjectName != null) {
                current.getApplicant().addGrade(
                        new Grade(Subject.valueOf(subjectName), rs.getInt("score")));
            }
        }
        return result;
    }
}
