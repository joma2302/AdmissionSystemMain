package org.example.infrastructure.persistence.mysql;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.domain.model.Faculty;
import org.example.domain.model.Subject;
import org.example.domain.model.SubjectRequirement;
import org.example.domain.repository.FacultyRepository;
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
 * MySQL-реалізація репозиторію факультетів.
 */
@Repository
public class MySqlFacultyRepository implements FacultyRepository {

    private static final Logger logger = LogManager.getLogger(MySqlFacultyRepository.class);
    private final DataSource dataSource;

    public MySqlFacultyRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(Faculty faculty) {
        String insertFaculty = "INSERT INTO faculties (name, max_students) VALUES (?, ?) " +
                               "ON DUPLICATE KEY UPDATE max_students = ?";
        String deleteRequirements = "DELETE FROM subject_requirements WHERE faculty_name = ?";
        String insertRequirement = "INSERT INTO subject_requirements (faculty_name, subject, minimum_score) VALUES (?, ?, ?)";

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement stmt = conn.prepareStatement(insertFaculty)) {
                    stmt.setString(1, faculty.getName());
                    stmt.setInt(2, faculty.getMaxStudents());
                    stmt.setInt(3, faculty.getMaxStudents());
                    stmt.executeUpdate();
                }

                try (PreparedStatement stmt = conn.prepareStatement(deleteRequirements)) {
                    stmt.setString(1, faculty.getName());
                    stmt.executeUpdate();
                }

                if (!faculty.getRequirements().isEmpty()) {
                    try (PreparedStatement stmt = conn.prepareStatement(insertRequirement)) {
                        for (SubjectRequirement req : faculty.getRequirements()) {
                            stmt.setString(1, faculty.getName());
                            stmt.setString(2, req.getSubject().name());
                            stmt.setInt(3, req.getMinimumScore());
                            stmt.addBatch();
                        }
                        stmt.executeBatch();
                    }
                }

                conn.commit();
                logger.debug("Збережено факультет та його вимоги: {}", faculty.getName());
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            logger.error("Помилка збереження факультету: {}", faculty.getName(), e);
            throw new RuntimeException("Помилка збереження факультету", e);
        }
    }

    @Override
    public Optional<Faculty> findByName(String name) {
        String facultySql = "SELECT name, max_students FROM faculties WHERE name = ?";
        String reqSql = "SELECT subject, minimum_score FROM subject_requirements WHERE faculty_name = ?";
        try (Connection conn = dataSource.getConnection()) {
            Faculty faculty = null;
            try (PreparedStatement stmt = conn.prepareStatement(facultySql)) {
                stmt.setString(1, name);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        faculty = new Faculty(rs.getString("name"), rs.getInt("max_students"));
                    }
                }
            }
            if (faculty != null) {
                try (PreparedStatement stmt = conn.prepareStatement(reqSql)) {
                    stmt.setString(1, name);
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            faculty.addRequirement(new SubjectRequirement(
                                    Subject.valueOf(rs.getString("subject")),
                                    rs.getInt("minimum_score")
                            ));
                        }
                    }
                }
                return Optional.of(faculty);
            }
        } catch (SQLException e) {
            logger.error("Помилка пошуку факультету за назвою: {}", name, e);
            throw new RuntimeException("Помилка пошуку факультету", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Faculty> findAll() {
        List<Faculty> result = new ArrayList<>();
        String facultySql = "SELECT name, max_students FROM faculties ORDER BY name";
        String reqSql = "SELECT faculty_name, subject, minimum_score FROM subject_requirements";
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(facultySql);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(new Faculty(rs.getString("name"), rs.getInt("max_students")));
                }
            }
            try (PreparedStatement stmt = conn.prepareStatement(reqSql);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String fName = rs.getString("faculty_name");
                    Subject subject = Subject.valueOf(rs.getString("subject"));
                    int score = rs.getInt("minimum_score");
                    result.stream()
                            .filter(f -> f.getName().equals(fName))
                            .findFirst()
                            .ifPresent(f -> f.addRequirement(new SubjectRequirement(subject, score)));
                }
            }
        } catch (SQLException e) {
            logger.error("Помилка отримання всіх факультетів", e);
            throw new RuntimeException("Помилка отримання всіх факультетів", e);
        }
        return result;
    }

    @Override
    public void deleteByName(String name) {
        String sql = "DELETE FROM faculties WHERE name = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.executeUpdate();
            logger.debug("Видалено факультет: {}", name);
        } catch (SQLException e) {
            logger.error("Помилка видалення факультету: {}", name, e);
            throw new RuntimeException("Помилка видалення факультету", e);
        }
    }
}
