# Звіт з лабораторної роботи № 2

## Налаштування проекту Spring Boot

---

### Титульний аркуш

**Лабораторна робота № 2**

**Тема:** Налаштування проекту Spring Boot

**Дисципліна:** ООП Практика

**Проект:** Інформаційна система «Приймальна комісія» (AdmissionSystem)

**Технології:** Java 17, Spring Boot 3.2.5, Maven, MySQL, Spring Security, FreeMarker, Caffeine Cache, Log4j2

---

### Вступ

**Напрям дослідження:** розробка серверного веб-додатку на основі фреймворку Spring Boot з використанням архітектурного патерну MVC та багатошарової архітектури (Domain-Driven Design).

**Мета роботи:** налаштувати проект Spring Boot для інформаційної системи «Приймальна комісія», організувати структуру директорій відповідно до патерну MVC, підключити Maven та основні бібліотеки, реалізувати базову функціональність системи.

**Завдання:**
1. Створити проект Spring Boot з підключенням Maven.
2. Організувати структуру пакетів: domain/model (entity), domain/repository, application/service (DAO/бізнес-логіка), infrastructure/web (controller), infrastructure/web/validation.
3. Підключити основні бібліотеки: Spring Web, Spring Security, Spring JDBC, FreeMarker, Caffeine, Log4j2, MySQL Connector.
4. Реалізувати доменні моделі, репозиторії, сервіси та контролери.
5. Налаштувати конфігурацію безпеки, бази даних та кешування.

---

### 1. Структура проекту (патерн MVC)

Проект організовано за принципами багатошарової архітектури:

```
src/main/java/org/example/
├── Main.java                          — точка входу
├── config/                            — конфігурація Spring
│   ├── CacheConfig.java               — налаштування Caffeine кешу
│   ├── DatabaseConfig.java            — налаштування DataSource та ініціалізація БД
│   └── SecurityConfig.java            — Spring Security (ролі, BCrypt)
├── domain/                            — доменний шар
│   ├── model/                         — entity (сутності)
│   │   ├── Applicant.java             — абітурієнт
│   │   ├── Application.java           — заявка на вступ
│   │   ├── ApplicationStatus.java     — статус заявки (enum)
│   │   ├── AdmissionSheet.java        — відомість зарахування
│   │   ├── Faculty.java               — факультет
│   │   ├── Grade.java                 — оцінка
│   │   ├── Role.java                  — роль користувача (enum)
│   │   ├── Subject.java               — предмет (enum)
│   │   └── User.java                  — користувач системи
│   └── repository/                    — інтерфейси репозиторіїв
│       ├── ApplicantRepository.java
│       ├── ApplicationRepository.java
│       ├── FacultyRepository.java
│       ├── GradeRepository.java
│       └── UserRepository.java
├── application/                       — шар бізнес-логіки
│   ├── dto/
│   │   └── ApplicantDto.java          — DTO абітурієнта
│   └── service/
│       ├── AdmissionService.java      — сервіс зарахування
│       ├── ApplicantService.java      — сервіс абітурієнтів
│       ├── ApplicationService.java    — сервіс заявок
│       └── AuthService.java           — сервіс автентифікації
└── infrastructure/                    — інфраструктурний шар
    ├── persistence/mysql/             — реалізація репозиторіїв (DAO)
    │   ├── DatabaseInitializer.java   — ініціалізація таблиць БД
    │   ├── MySqlApplicantRepository.java
    │   ├── MySqlApplicationRepository.java
    │   ├── MySqlFacultyRepository.java
    │   ├── MySqlGradeRepository.java
    │   └── MySqlUserRepository.java
    └── web/                           — контролери (Controller)
        ├── AdminController.java       — адміністрування
        ├── ApplicantController.java   — реєстрація абітурієнтів
        ├── ApplicationController.java — подача заявок
        ├── AuthController.java        — автентифікація
        ├── HomeController.java        — головна сторінка
        ├── GlobalExceptionHandler.java— обробка помилок
        ├── dto/                       — DTO відповідей
        │   ├── ApplicationResponse.java
        │   ├── AdmissionResultResponse.java
        │   ├── ApplyRequest.java
        │   ├── RegisterApplicantRequest.java
        │   └── ResponseMapper.java
        └── validation/
            └── InputValidator.java    — валідація вхідних даних
```

---

### 2. Підключення Maven та бібліотек

Проект використовує **Apache Maven** як систему збірки. Файл `pom.xml` налаштовано з батьківським POM Spring Boot:

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.5</version>
</parent>
```

**Підключені бібліотеки:**

| Бібліотека | Призначення |
|---|---|
| `spring-boot-starter-web` | Spring MVC, вбудований Tomcat |
| `spring-boot-starter-freemarker` | Шаблонізатор FreeMarker |
| `spring-boot-starter-security` | Автентифікація, авторизація, ролі |
| `spring-boot-starter-jdbc` | JDBC + HikariCP пул з'єднань |
| `spring-boot-starter-cache` | Підтримка кешування |
| `caffeine` | Caffeine — in-memory кеш |
| `spring-boot-starter-log4j2` | Журналювання Log4j2 |
| `mysql-connector-j 8.3.0` | MySQL JDBC драйвер |
| `spring-boot-starter-test` | Тестування (JUnit 5, Mockito) |
| `spring-security-test` | Тестування Spring Security |
| `h2` | In-memory БД для тестів |

---

### 3. Лістинг програми з коментарями

#### 3.1. Точка входу — Main.java

```java
package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Точка входу в додаток «Приймальна комісія».
 * Використовує Spring Boot для автоконфігурації всіх компонентів.
 * {@code @EnableCaching} активує підтримку кешування (Caffeine).
 */
@SpringBootApplication
@EnableCaching
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        System.out.println("Сайт запущен: http://localhost:8080");
    }
}
```

#### 3.2. Доменні моделі (entity)

**Applicant.java** — сутність «Абітурієнт»:
```java
package org.example.domain.model;
import java.util.*;

/**
 * Доменна сутність «Абітурієнт».
 * Інкапсулює дані абітурієнта та його оцінки.
 * Забезпечує інваріанти: непорожні поля, унікальність оцінок за предметами.
 */
public class Applicant {
    private final String id;          // Унікальний ідентифікатор
    private final String firstName;   // Ім'я
    private final String lastName;    // Прізвище
    private final List<Grade> grades; // Список оцінок

    // Конструктор з валідацією
    public Applicant(String id, String firstName, String lastName) { ... }

    // Додавання оцінки з перевіркою дублікатів
    public void addGrade(Grade grade) { ... }

    // Обчислення загального балу
    public int getTotalScore() { ... }
}
```

**Faculty.java** — сутність «Факультет»:
```java
/**
 * Доменна сутність «Факультет».
 * Визначає назву та максимальну кількість студентів.
 */
public class Faculty {
    private final String name;       // Назва факультету
    private final int maxStudents;   // Максимальна кількість місць
}
```

**Application.java** — сутність «Заявка»:
```java
/**
 * Доменна сутність «Заявка на вступ».
 * Пов'язує абітурієнта з факультетом та зберігає статус.
 */
public class Application {
    private final Applicant applicant;     // Абітурієнт
    private final Faculty faculty;         // Факультет
    private ApplicationStatus status;      // Статус заявки
}
```

**ApplicationStatus.java** — перелік статусів:
```java
/** Статуси заявки: очікує, зарахований, відхилений. */
public enum ApplicationStatus {
    PENDING, ADMITTED, REJECTED
}
```

**AdmissionSheet.java** — відомість зарахування:
```java
/**
 * Доменна сутність «Відомість зарахування».
 * Ранжує заявки за балами та визначає зарахованих/відхилених.
 * Реалізує Strategy pattern — алгоритм зарахування інкапсульований
 * у методі determineAdmitted().
 */
public class AdmissionSheet {
    private final Faculty faculty;
    private final List<Application> applications;

    public void determineAdmitted() { ... } // Алгоритм зарахування
    public List<Application> getAdmitted() { ... }
    public List<Application> getRejected() { ... }
}
```

#### 3.3. Репозиторії (repository)

Інтерфейси репозиторіїв визначають контракт доступу до даних:

```java
/** Інтерфейс репозиторію абітурієнтів (абстракція доступу до даних). */
public interface ApplicantRepository {
    void save(Applicant applicant);
    Optional<Applicant> findById(String id);
    List<Applicant> findAll();
    List<Applicant> findByFacultyName(String facultyName);
    void deleteById(String id);
}

/** Інтерфейс репозиторію заявок. */
public interface ApplicationRepository {
    void save(Application application);
    List<Application> findByFacultyName(String facultyName);
    List<Application> findByApplicantId(String applicantId);
    List<Application> findByFacultyNameOrderByScoreDesc(String facultyName);
    void updateStatus(Application application);
}

/** Інтерфейс репозиторію факультетів. */
public interface FacultyRepository {
    void save(Faculty faculty);
    Optional<Faculty> findByName(String name);
    List<Faculty> findAll();
    void deleteByName(String name);
}
```

#### 3.4. DAO — реалізація репозиторіїв (MySQL)

```java
/**
 * MySQL-реалізація репозиторію абітурієнтів.
 * Використовує JdbcTemplate для виконання SQL-запитів.
 */
@Repository
public class MySqlApplicantRepository implements ApplicantRepository {
    private final JdbcTemplate jdbc;
    private final GradeRepository gradeRepo;

    @Override
    public void save(Applicant applicant) {
        // INSERT або UPDATE абітурієнта в БД
        jdbc.update("INSERT INTO applicants (id, first_name, last_name) VALUES (?,?,?) " +
                     "ON DUPLICATE KEY UPDATE first_name=?, last_name=?", ...);
        // Збереження оцінок
        for (Grade g : applicant.getGrades()) {
            gradeRepo.save(applicant.getId(), g);
        }
    }

    @Override
    public Optional<Applicant> findById(String id) {
        // SELECT абітурієнта + його оцінки
    }
}
```

#### 3.5. Сервіси (бізнес-логіка)

```java
/**
 * Сервіс зарахування — формує відомість та визначає зарахованих.
 * Використовує @CacheEvict для скидання кешу після зарахування.
 */
@Service
public class AdmissionService {
    @CacheEvict(value = "faculties", allEntries = true)
    public AdmissionSheet runAdmission(String facultyName) {
        // 1. Знайти факультет
        // 2. Завантажити заявки, відсортовані за балами
        // 3. Створити відомість та визначити зарахованих
        // 4. Оновити статуси в БД
    }
}

/**
 * Сервіс автентифікації — реєстрація та вхід користувачів.
 * Паролі хешуються через BCryptPasswordEncoder.
 */
@Service
public class AuthService implements UserDetailsService {
    public void register(String username, String rawPassword, Role role) { ... }
    public UserDetails loadUserByUsername(String username) { ... }
}
```

#### 3.6. Контролери (Controller)

```java
/**
 * Контролер адміністрування — запуск зарахування та перегляд результатів.
 * Доступний лише для ролі ADMIN.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {
    @PostMapping("/admission")
    public String runAdmission(@RequestParam String facultyName, Model model) {
        // Запуск зарахування та відображення результатів
    }

    @GetMapping("/ranking")
    public String ranking(@RequestParam String facultyName, Model model) {
        // Відображення рейтингу абітурієнтів
    }
}

/**
 * Контролер реєстрації абітурієнтів.
 * Валідує вхідні дані через InputValidator.
 */
@Controller
@RequestMapping("/applicants")
public class ApplicantController {
    @PostMapping("/register")
    public String register(@ModelAttribute RegisterApplicantRequest req, Model model) {
        // Валідація → створення DTO → збереження через сервіс
    }
}
```

#### 3.7. Валідація (validation)

```java
/**
 * Утилітний клас для валідації вхідних даних.
 * Перевіряє коректність імен, оцінок та інших полів.
 */
public final class InputValidator {
    public static void validateName(String name) { ... }
    public static void validateScore(int score) { ... }
    public static void validateUsername(String username) { ... }
}
```

#### 3.8. Конфігурація

**SecurityConfig.java:**
```java
/**
 * Конфігурація Spring Security.
 * Визначає правила доступу за ролями та BCrypt для паролів.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        // /admin/** — тільки ADMIN
        // /applicants/**, /applications/** — APPLICANT або ADMIN
        // /, /auth/** — доступно всім
    }
}
```

**DatabaseConfig.java:**
```java
/**
 * Конфігурація бази даних.
 * Ініціалізує таблиці при старті додатку.
 */
@Configuration
public class DatabaseConfig {
    @PostConstruct
    public void initDatabase() {
        DatabaseInitializer.initialize(dataSource.getConnection());
    }
}
```

**application.properties:**
```properties
# Підключення до MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/admission_db
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# FreeMarker шаблонізатор
spring.freemarker.template-loader-path=classpath:/templates/
spring.freemarker.suffix=.ftl

# Кешування (Caffeine)
spring.cache.type=caffeine
spring.cache.caffeine.spec=maximumSize=100,expireAfterWrite=10m
```

---

### 4. Вхідні та вихідні дані програми

#### Вхідні дані:

| Операція | Вхідні дані | Приклад |
|---|---|---|
| Реєстрація користувача | username, password | admin / password123 |
| Реєстрація абітурієнта | id, firstName, lastName, оцінки (MATH, UKRAINIAN, HISTORY) | "001", "Іван", "Петренко", MATH=185, UKRAINIAN=190, HISTORY=175 |
| Подача заявки | applicantId, facultyName | "001", "Комп'ютерних наук" |
| Запуск зарахування | facultyName | "Комп'ютерних наук" |
| Перегляд рейтингу | facultyName | "Математичний" |

#### Вихідні дані:

| Операція | Вихідні дані |
|---|---|
| Реєстрація абітурієнта | Сторінка з підтвердженням або повідомлення про помилку валідації |
| Подача заявки | Перенаправлення на список заявок |
| Список заявок | Таблиця: ПІБ абітурієнта, факультет, статус, загальний бал |
| Зарахування | Відомість: списки зарахованих та відхилених абітурієнтів |
| Рейтинг | Відсортований список абітурієнтів за балами |

#### Приклад роботи системи:

1. Адміністратор входить у систему (`/auth/login`).
2. Абітурієнт реєструється (`/auth/signup`) та заповнює анкету (`/applicants/register`).
3. Абітурієнт подає заявку на факультет (`/applications/apply`).
4. Адміністратор запускає зарахування (`/admin/admission`).
5. Система ранжує абітурієнтів за балами та зараховує найкращих у межах квоти факультету.
6. Результати відображаються у відомості зарахування.

---

### 5. Аналіз результатів та висновки

#### Аналіз результатів проектування:

Проект побудовано на основі **багатошарової архітектури** з чітким розділенням відповідальностей:

- **Domain Layer** (domain/model, domain/repository) — містить бізнес-сутності та інтерфейси репозиторіїв. Не залежить від фреймворків.
- **Application Layer** (application/service, application/dto) — реалізує бізнес-логіку та оркеструє доменні об'єкти.
- **Infrastructure Layer** (infrastructure/persistence, infrastructure/web) — реалізує доступ до БД та HTTP-контролери.

Такий підхід забезпечує:
- **Тестованість** — доменний шар можна тестувати без БД та Spring.
- **Замінність** — реалізацію MySQL можна замінити на іншу СУБД без зміни бізнес-логіки.
- **Масштабованість** — нові функції додаються в окремих шарах.

#### Аналіз програмування:

- Використано **Spring Boot 3.2.5** з автоконфігурацією, що значно спрощує налаштування.
- **Maven** забезпечує управління залежностями та збірку проекту.
- **Spring Security** реалізує автентифікацію з BCrypt-хешуванням паролів та авторизацію за ролями (ADMIN, APPLICANT).
- **JdbcTemplate** використовується для доступу до MySQL замість ORM, що дає повний контроль над SQL-запитами.
- **Caffeine Cache** прискорює часті запити (наприклад, список факультетів).
- **FreeMarker** використовується як шаблонізатор для серверного рендерингу HTML-сторінок.
- **Log4j2** забезпечує структуроване журналювання.

#### Аналіз тестування:

Проект містить тести на кількох рівнях:
- **DomainModelTest** — модульні тести доменних сутностей.
- **ServiceLayerTest** — тести бізнес-логіки сервісного шару.
- **MySqlRepositoryTest** — інтеграційні тести репозиторіїв (H2 in-memory БД).
- **ApplicationServiceIntegrationTest** — наскрізні інтеграційні тести.

#### Висновки:

1. У ході лабораторної роботи було успішно налаштовано проект Spring Boot для інформаційної системи «Приймальна комісія».
2. Організовано структуру директорій відповідно до патерну MVC: entity (domain/model), repository (domain/repository), DAO (infrastructure/persistence), validation (infrastructure/web/validation), controller (infrastructure/web).
3. Підключено Maven як систему збірки з батьківським POM Spring Boot 3.2.5.
4. Підключено основні бібліотеки: Spring Web, Spring Security, Spring JDBC, FreeMarker, Caffeine, Log4j2, MySQL Connector.
5. Реалізовано повний цикл роботи системи: реєстрація → подача заявки → зарахування → перегляд результатів.
6. Застосовано принципи чистої архітектури з розділенням на доменний, прикладний та інфраструктурний шари.
7. Проект готовий до подальшого розвитку та масштабування.
