# Звіт з лабораторної роботи № 3

## Тема: Проектування моделі бази даних в середовищі MySQLWorkbench

**Дисципліна:** Веб-технології  
**Проект:** Інформаційна система «Приймальна комісія» (AdmissionSystem)  
**Технології:** MySQL 8.x, MySQLWorkbench, Spring Boot JDBC

---

### 1. Вступ

**Мета:** спроектувати реляційну модель бази даних для системи «Приймальна комісія», сформувати SQL-код створення БД та налаштувати MySQL-сервер.

**Завдання:**
1. Налаштувати MySQL-сервер та створити базу даних.
2. Спроектувати модель БД (таблиці, зв'язки, обмеження).
3. Сформувати SQL-код для побудови БД.

---

### 2. Налаштування MySQL-сервера

Параметри підключення (`application.properties`):

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/admission_db
spring.datasource.username=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

Пул з'єднань — HikariCP (Spring Boot JDBC starter).

---

### 3. Модель бази даних

#### ER-діаграма (текстовий опис):

```
[faculties] 1 ──< [applications] >── 1 [applicants] 1 ──< [grades]
                                                          
[users] (окрема таблиця автентифікації)
```

#### Таблиці та зв'язки:

| Таблиця | PK | Зв'язки |
|---|---|---|
| `faculties` | `name` | 1:N → applications |
| `applicants` | `id` | 1:N → grades, 1:N → applications |
| `grades` | (`applicant_id`, `subject`) | FK → applicants (CASCADE) |
| `applications` | (`applicant_id`, `faculty_name`) | FK → applicants (CASCADE), FK → faculties (CASCADE) |
| `users` | `username` | — |

#### Структура таблиць:

**faculties** — факультети:

| Поле | Тип | Обмеження |
|---|---|---|
| name | VARCHAR(255) | PRIMARY KEY |
| max_students | INT | NOT NULL |

**applicants** — абітурієнти:

| Поле | Тип | Обмеження |
|---|---|---|
| id | VARCHAR(255) | PRIMARY KEY |
| first_name | VARCHAR(255) | NOT NULL |
| last_name | VARCHAR(255) | NOT NULL |

**grades** — оцінки ЗНО:

| Поле | Тип | Обмеження |
|---|---|---|
| applicant_id | VARCHAR(255) | PK, FK → applicants(id) ON DELETE CASCADE |
| subject | VARCHAR(50) | PK |
| score | INT | NOT NULL |

**applications** — заявки на вступ:

| Поле | Тип | Обмеження |
|---|---|---|
| applicant_id | VARCHAR(255) | PK, FK → applicants(id) ON DELETE CASCADE |
| faculty_name | VARCHAR(255) | PK, FK → faculties(name) ON DELETE CASCADE |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'PENDING' |

**users** — користувачі системи:

| Поле | Тип | Обмеження |
|---|---|---|
| username | VARCHAR(255) | PRIMARY KEY |
| password_hash | VARCHAR(255) | NOT NULL |
| role | VARCHAR(20) | NOT NULL |

---

### 4. SQL-код для побудови БД

```sql
-- Створення бази даних
CREATE DATABASE IF NOT EXISTS admission_db
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE admission_db;

-- Таблиця факультетів
CREATE TABLE IF NOT EXISTS faculties (
    name VARCHAR(255) PRIMARY KEY,
    max_students INT NOT NULL
);

-- Таблиця абітурієнтів
CREATE TABLE IF NOT EXISTS applicants (
    id VARCHAR(255) PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL
);

-- Таблиця оцінок (FK → applicants, каскадне видалення)
CREATE TABLE IF NOT EXISTS grades (
    applicant_id VARCHAR(255) NOT NULL,
    subject VARCHAR(50) NOT NULL,
    score INT NOT NULL,
    PRIMARY KEY (applicant_id, subject),
    FOREIGN KEY (applicant_id) REFERENCES applicants(id) ON DELETE CASCADE
);

-- Таблиця заявок (FK → applicants, faculties, каскадне видалення)
CREATE TABLE IF NOT EXISTS applications (
    applicant_id VARCHAR(255) NOT NULL,
    faculty_name VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    PRIMARY KEY (applicant_id, faculty_name),
    FOREIGN KEY (applicant_id) REFERENCES applicants(id) ON DELETE CASCADE,
    FOREIGN KEY (faculty_name) REFERENCES faculties(name) ON DELETE CASCADE
);

-- Таблиця користувачів
CREATE TABLE IF NOT EXISTS users (
    username VARCHAR(255) PRIMARY KEY,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL
);

-- Seed-дані: факультети
INSERT IGNORE INTO faculties (name, max_students) VALUES
    ('Комп''ютерних наук', 30),
    ('Математичний', 25),
    ('Фізичний', 20),
    ('Хімічний', 15),
    ('Біологічний', 20);
```

---

### 5. Висновки

1. Налаштовано MySQL-сервер та створено базу даних `admission_db`.
2. Спроектовано реляційну модель з 5 таблиць та зв'язками через зовнішні ключі з каскадним видаленням.
3. Використано складені первинні ключі для таблиць `grades` та `applications`, що забезпечує унікальність записів.
4. SQL-код є ідемпотентним (`IF NOT EXISTS`, `INSERT IGNORE`) — безпечне повторне виконання.
5. Модель БД повністю відповідає доменним сутностям проекту (Applicant, Faculty, Grade, Application, User).
