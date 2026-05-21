# ЗВІТ З НАВЧАЛЬНОЇ ПРАКТИКИ
## Лабораторна робота №14
### Тема: Підключення додатку до бази даних та реалізація репозиторіїв для CRUD-операцій

---

### 1. Вступ
**Напрям дослідження:** Інтеграція Java-додатків з реляційними базами даних, конфігурація пулів з'єднань та реалізація паттерну Repository для забезпечення персистентності даних.

**Мета роботи:** Налаштувати стабільне підключення додатку "Приймальна комісія" до СУБД MySQL, реалізувати механізми автоматичної ініціалізації схеми БД та розробити класи репозиторіїв для виконання повного спектру CRUD-операцій (Create, Read, Update, Delete).

**Завдання:**
1. Налаштувати параметри підключення до MySQL у конфігураційному файлі `application.properties`.
2. Впровадити пул з'єднань HikariCP для оптимізації роботи з ресурсами БД.
3. Реалізувати клас `DatabaseInitializer` для автоматичного створення таблиць та наповнення їх початковими даними (seeding).
4. Розробити MySQL-реалізації інтерфейсів репозиторіїв для основних доменних сутностей: `Applicant`, `Faculty`, `Application`.
5. Перевірити коректність роботи шару доступу до даних за допомогою інтеграційних тестів.

---

### 2. Лаконічний опис результатів виконання
В ході лабораторної роботи було завершено побудову шару інфраструктури (Infrastructure Layer), відповідального за зберігання даних.

**Ключові результати:**
- **Конфігурація:** У файлі `application.properties` прописано параметри JDBC-драйвера, URL бази даних, облікові дані та налаштування HikariCP (розмір пулу, таймаути).
- **Ініціалізація БД:** Створено компонент `DatabaseConfig`, який при старті Spring-контексту викликає `DatabaseInitializer.initialize()`. Це гарантує наявність усіх необхідних таблиць (`applicants`, `faculties`, `applications`, `grades`, `users`, `subject_requirements`) без ручного втручання.
- **Репозиторії:** 
    - `MySqlApplicantRepository`: реалізує збереження абітурієнтів разом із обробкою їхніх оцінок (через JOIN).
    - `MySqlApplicationRepository`: забезпечує складну логіку вибірки заявок, включаючи ранжування за сумою балів (`findByFacultyNameOrderByScoreDesc`).
    - `MySqlFacultyRepository`: підтримує транзакційне оновлення лімітів місць та предметних вимог.
- **Тестування:** Проведено успішне виконання набору тестів `MySqlRepositoryTest` (9 з 9 тестів пройдено), що підтверджує коректність виконання операцій вставки, пошуку за складними критеріями, оновлення статусів та каскадного видалення.

---

### 3. Текст програми з коментарями

#### 3.1. Налаштування підключення (application.properties)
```properties
# Налаштування MySQL (HikariCP пул)
spring.datasource.url=jdbc:mysql://localhost:3306/admission_system?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC&createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Параметри пулу з'єднань для забезпечення високої продуктивності
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=2
spring.datasource.hikari.connection-timeout=30000
```

#### 3.2. Реалізація CRUD-операцій в MySqlApplicantRepository.java
```java
@Repository
public class MySqlApplicantRepository implements ApplicantRepository {
    private final DataSource dataSource; // Використання ін'єкції пулу з'єднань

    public MySqlApplicantRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(Applicant applicant) {
        // Використання ON DUPLICATE KEY UPDATE для ідемпотентності операції
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
        } catch (SQLException e) {
            throw new RuntimeException("Помилка збереження абітурієнта", e);
        }
    }

    @Override
    public void deleteById(String id) {
        // Видалення за первинним ключем (каскадне видалення оцінок та заявок налаштоване в БД)
        String sql = "DELETE FROM applicants WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Помилка видалення абітурієнта", e);
        }
    }
}
```

---

### 4. Вхідні та вихідні дані програми

**Вхідні дані:**
- SQL-скрипти ініціалізації схеми БД у класі `DatabaseInitializer`.
- Об'єкти доменних моделей (`Applicant`, `Faculty`, `Application`), що передаються методам репозиторіїв.
- Конфігураційні параметри середовища (host, port, credentials).

**Вихідні дані:**
- Сформована структура таблиць у базі даних MySQL.
- Записи в таблицях, що відповідають збереженим сутностям.
- Логи додатку, що підтверджують успішність підключення ("База даних успішно ініціалізована").
- Результати JUnit-тестів (зелені індикатори проходження всіх 9 сценаріїв CRUD).

---

### 5. Змістовний аналіз та висновки

**Аналіз результатів:**
Реалізація шару доступу до даних через JDBC та паттерн Repository дозволила абстрагувати бізнес-логіку від особливостей роботи з конкретною СУБД. Використання HikariCP забезпечує стабільність підключення під навантаженням. Завдяки `DatabaseInitializer` додаток став більш автономним, оскільки він самостійно розгортає необхідну інфраструктуру при першому запуску. Використання SQL-конструкції `ON DUPLICATE KEY UPDATE` зробило операції збереження безпечними (upsert logic), що запобігає дублюванню даних.

**Висновки:**
В ході виконання лабораторної роботи №14 було успішно налаштовано підключення додатку до бази даних MySQL та реалізовано повноцінний CRUD-інтерфейс через репозиторії. Система готова до збереження великих обсягів даних про абітурієнтів та їхні заявки. Виконані тести підтвердили надійність реалізації та коректність роботи всіх механізмів персистентності.
