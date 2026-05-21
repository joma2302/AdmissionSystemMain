# Звіт з лабораторної роботи № 5

## Тема: Підключення БД до проекту та розробка CRUD-операцій

**Проект:** AdmissionSystem  
**Технології:** Java 17, Spring Boot 3.2.5, MySQL 8.x, JDBC, HikariCP

---

### 1. Вступ

**Мета:** підключити MySQL до проекту та реалізувати CRUD-операції через JpaRepository-подібні інтерфейси.

**Завдання:**
1. Налаштувати підключення до БД.
2. Реалізувати репозиторії з CRUD-методами для кожної сутності.

---

### 2. Підключення БД

**application.properties:**

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/admission_db
spring.datasource.username=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

**DatabaseConfig.java** — конфігурація DataSource та ініціалізація таблиць:

```java
// Конфігурація підключення до MySQL через HikariCP
@Configuration
public class DatabaseConfig {
    @Bean
    public DataSource dataSource() { ... } // HikariDataSource з параметрами

    @Bean
    public DatabaseInitializer databaseInitializer(DataSource ds) {
        DatabaseInitializer.initialize(ds.getConnection()); // CREATE TABLE IF NOT EXISTS
    }
}
```

---

### 3. Інтерфейси репозиторіїв

| Репозиторій | Методи |
|---|---|
| `ApplicantRepository` | save, findById, findAll, findByFacultyName, deleteById |
| `ApplicationRepository` | save, findByFacultyName, findByApplicantId, findByFacultyNameOrderByScoreDesc, updateStatus |
| `FacultyRepository` | save, findByName, findAll, deleteByName |
| `GradeRepository` | save, findByApplicantId, deleteByApplicantId |
| `UserRepository` | save, findByUsername, existsByUsername |

---

### 4. Реалізація CRUD (MySQL)

#### MySqlApplicantRepository (приклад):

```java
// CRUD-реалізація для абітурієнтів через JDBC
@Repository
public class MySqlApplicantRepository implements ApplicantRepository {
    private final DataSource dataSource;

    // CREATE / UPDATE
    public void save(Applicant applicant) {
        // INSERT INTO applicants (id, first_name, last_name) VALUES (?,?,?)
        // ON DUPLICATE KEY UPDATE ...
    }

    // READ
    public Optional<Applicant> findById(String id) {
        // SELECT * FROM applicants WHERE id = ?
        // + завантаження оцінок з таблиці grades
    }

    // READ ALL
    public List<Applicant> findAll() { /* SELECT * FROM applicants */ }

    // DELETE
    public void deleteById(String id) { /* DELETE FROM applicants WHERE id = ? */ }
}
```

Аналогічно реалізовано: `MySqlFacultyRepository` (з кешуванням Caffeine), `MySqlGradeRepository`, `MySqlApplicationRepository`, `MySqlUserRepository`.

---

### 5. Висновки

1. Підключено MySQL через Spring Boot JDBC + HikariCP.
2. Реалізовано 5 репозиторіїв з повним набором CRUD-операцій.
3. Використано PreparedStatement для захисту від SQL-ін'єкцій.
4. Ініціалізація БД — ідемпотентна (CREATE IF NOT EXISTS, INSERT IGNORE).
5. Кешування факультетів через Caffeine зменшує навантаження на БД.
