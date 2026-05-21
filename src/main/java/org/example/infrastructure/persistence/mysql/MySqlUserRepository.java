package org.example.infrastructure.persistence.mysql;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.domain.model.Role;
import org.example.domain.model.User;
import org.example.domain.repository.UserRepository;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;

/**
 * MySQL-реалізація репозиторію користувачів.
 * Зберігає облікові записи для автентифікації через Spring Security.
 */
@Repository
public class MySqlUserRepository implements UserRepository {

    private static final Logger logger = LogManager.getLogger(MySqlUserRepository.class);
    private final DataSource dataSource;

    public MySqlUserRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /** Зберігає або оновлює користувача. */
    @Override
    public void save(User user) {
        String sql = "INSERT INTO users (username, password_hash, role) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE password_hash = ?, role = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getRole().name());
            stmt.setString(4, user.getPasswordHash());
            stmt.setString(5, user.getRole().name());
            stmt.executeUpdate();
            logger.debug("Збережено користувача: {}", user.getUsername());
        } catch (SQLException e) {
            logger.error("Помилка збереження користувача: {}", user.getUsername(), e);
            throw new RuntimeException("Помилка збереження користувача", e);
        }
    }

    /** Знаходить користувача за ім'ям. */
    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT username, password_hash, role FROM users WHERE username = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new User(
                            rs.getString("username"),
                            rs.getString("password_hash"),
                            Role.valueOf(rs.getString("role"))));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            logger.error("Помилка пошуку користувача: {}", username, e);
            throw new RuntimeException("Помилка пошуку користувача", e);
        }
    }

    /** Перевіряє, чи існує користувач з таким ім'ям. */
    @Override
    public boolean existsByUsername(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.error("Помилка перевірки існування користувача: {}", username, e);
            throw new RuntimeException("Помилка перевірки існування користувача", e);
        }
    }

    @Override
    public java.util.List<User> findAll() {
        java.util.List<User> users = new java.util.ArrayList<>();
        String sql = "SELECT username, password_hash, role FROM users";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(new User(
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        Role.valueOf(rs.getString("role"))));
            }
        } catch (SQLException e) {
            logger.error("Помилка отримання списку користувачів", e);
            throw new RuntimeException("Помилка отримання списку користувачів", e);
        }
        return users;
    }
}
