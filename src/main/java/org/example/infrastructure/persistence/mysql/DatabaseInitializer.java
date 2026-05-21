package org.example.infrastructure.persistence.mysql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Ініціалізатор бази даних.
 * Створює таблиці та seed-дані при першому запуску додатку.
 * Використовує CREATE TABLE IF NOT EXISTS та INSERT IGNORE для ідемпотентності.
 */
public class DatabaseInitializer {

    /** SQL створення таблиці факультетів. */
    private static final String CREATE_FACULTIES = """
            CREATE TABLE IF NOT EXISTS faculties (
                name VARCHAR(255) PRIMARY KEY,
                max_students INT NOT NULL
            )""";

    /** SQL створення таблиці абітурієнтів. */
    private static final String CREATE_APPLICANTS = """
            CREATE TABLE IF NOT EXISTS applicants (
                id VARCHAR(255) PRIMARY KEY,
                first_name VARCHAR(255) NOT NULL,
                last_name VARCHAR(255) NOT NULL,
                documents_path VARCHAR(512)
            )""";

    /** SQL створення таблиці оцінок (FK → applicants, каскадне видалення). */
    private static final String CREATE_GRADES = """
            CREATE TABLE IF NOT EXISTS grades (
                applicant_id VARCHAR(255) NOT NULL,
                subject VARCHAR(50) NOT NULL,
                score INT NOT NULL,
                PRIMARY KEY (applicant_id, subject),
                FOREIGN KEY (applicant_id) REFERENCES applicants(id) ON DELETE CASCADE
            )""";

    /** SQL створення таблиці заявок (FK → applicants, faculties, каскадне видалення). */
    private static final String CREATE_APPLICATIONS = """
            CREATE TABLE IF NOT EXISTS applications (
                applicant_id VARCHAR(255) NOT NULL,
                faculty_name VARCHAR(255) NOT NULL,
                status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                PRIMARY KEY (applicant_id, faculty_name),
                FOREIGN KEY (applicant_id) REFERENCES applicants(id) ON DELETE CASCADE,
                FOREIGN KEY (faculty_name) REFERENCES faculties(name) ON DELETE CASCADE
            )""";

    /** SQL створення таблиці користувачів для автентифікації. */
    private static final String CREATE_USERS = """
            CREATE TABLE IF NOT EXISTS users (
                username VARCHAR(255) PRIMARY KEY,
                password_hash VARCHAR(255) NOT NULL,
                role VARCHAR(20) NOT NULL
            )""";

    /** SQL створення таблиці вимог до предметів (FK → faculties). */
    private static final String CREATE_SUBJECT_REQUIREMENTS = """
            CREATE TABLE IF NOT EXISTS subject_requirements (
                faculty_name VARCHAR(255) NOT NULL,
                subject VARCHAR(50) NOT NULL,
                minimum_score INT NOT NULL,
                PRIMARY KEY (faculty_name, subject),
                FOREIGN KEY (faculty_name) REFERENCES faculties(name) ON DELETE CASCADE
            )""";

    /** SQL створення таблиці аудит-логів. */
    private static final String CREATE_AUDIT_LOGS = """
            CREATE TABLE IF NOT EXISTS audit_logs (
                id INT AUTO_INCREMENT PRIMARY KEY,
                user_id VARCHAR(255) NOT NULL,
                action VARCHAR(255) NOT NULL,
                target VARCHAR(255) NOT NULL,
                timestamp DATETIME NOT NULL
            )""";

    /** SQL створення таблиці історії статусів. */
    private static final String CREATE_APPLICATION_STATUS_HISTORY = """
            CREATE TABLE IF NOT EXISTS application_status_history (
                id INT AUTO_INCREMENT PRIMARY KEY,
                applicant_id VARCHAR(255) NOT NULL,
                faculty_name VARCHAR(255) NOT NULL,
                status VARCHAR(20) NOT NULL,
                changed_at DATETIME NOT NULL,
                FOREIGN KEY (applicant_id) REFERENCES applicants(id) ON DELETE CASCADE,
                FOREIGN KEY (faculty_name) REFERENCES faculties(name) ON DELETE CASCADE
            )""";

    private DatabaseInitializer() {
    }

    /** Seed-дані: 5 факультетів за замовчуванням. */
    private static final String SEED_FACULTIES = """
            INSERT IGNORE INTO faculties (name, max_students) VALUES
            ('Комп''ютерних наук', 30),
            ('Математичний', 25),
            ('Фізичний', 20),
            ('Хімічний', 15),
            ('Біологічний', 20)""";

    /**
     * Ініціалізує БД: створює таблиці та вставляє seed-дані.
     *
     * @param connection з'єднання з БД
     * @throws SQLException у разі помилки SQL
     */
    public static void initialize(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(CREATE_FACULTIES);
            stmt.execute(CREATE_APPLICANTS);
            stmt.execute(CREATE_GRADES);
            stmt.execute(CREATE_APPLICATIONS);
            stmt.execute(CREATE_USERS);
            stmt.execute(CREATE_SUBJECT_REQUIREMENTS);
            stmt.execute(CREATE_AUDIT_LOGS);
            stmt.execute(CREATE_APPLICATION_STATUS_HISTORY);
            stmt.execute(SEED_FACULTIES);
        }
        ensureApplicantDocumentsColumn(connection);
    }

    /** Додає колонку для документів у старі бази, створені до цієї функції. */
    private static void ensureApplicantDocumentsColumn(Connection connection) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet columns = metaData.getColumns(null, null, null, null)) {
            while (columns.next()) {
                String tableName = columns.getString("TABLE_NAME");
                String columnName = columns.getString("COLUMN_NAME");
                if ("applicants".equalsIgnoreCase(tableName)
                        && "documents_path".equalsIgnoreCase(columnName)) {
                    return;
                }
            }
        }

        try (Statement stmt = connection.createStatement()) {
            stmt.execute("ALTER TABLE applicants ADD COLUMN documents_path VARCHAR(512)");
        }
    }
}
