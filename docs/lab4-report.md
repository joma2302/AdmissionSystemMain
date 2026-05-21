# Звіт з лабораторної роботи № 4

## Тема: Розробка класів сутностей

**Проект:** Інформаційна система «Приймальна комісія» (AdmissionSystem)  
**Технології:** Java 17, Spring Boot 3.2.5

---

### 1. Вступ

**Мета:** розробити класи сутностей доменної моделі системи «Приймальна комісія».

**Завдання:**
1. Визначити сутності предметної області.
2. Реалізувати класи з інкапсуляцією, валідацією та зв'язками.

---

### 2. Перелік сутностей

| Клас | Пакет | Опис |
|---|---|---|
| `Applicant` | domain.model | Абітурієнт (id, ПІБ, оцінки) |
| `Faculty` | domain.model | Факультет (назва, макс. кількість студентів) |
| `Grade` | domain.model | Оцінка ЗНО (предмет, бал) |
| `Application` | domain.model | Заявка на вступ (абітурієнт, факультет, статус) |
| `User` | domain.model | Користувач системи (логін, хеш пароля, роль) |
| `AdmissionSheet` | domain.model | Відомість зарахування (факультет, список заявок) |
| `Subject` | domain.model | Enum предметів ЗНО (MATH, UKRAINIAN, HISTORY) |
| `Role` | domain.model | Enum ролей (ADMIN, APPLICANT) |
| `ApplicationStatus` | domain.model | Enum статусів заявки (PENDING, APPROVED, REJECTED) |

---

### 3. Лістинг класів сутностей

#### Applicant.java

```java
// Доменна сутність «Абітурієнт»
// Інкапсулює дані абітурієнта та його оцінки
public class Applicant {
    private final String id;          // унікальний ідентифікатор
    private final String firstName;   // ім'я
    private final String lastName;    // прізвище
    private final List<Grade> grades; // список оцінок ЗНО

    // Конструктор з валідацією: id, firstName, lastName — не null/порожні
    public Applicant(String id, String firstName, String lastName) { ... }

    // Додає оцінку; кидає IllegalArgumentException при дублікаті предмета
    public void addGrade(Grade grade) { ... }

    // Обчислює загальний бал (сума всіх оцінок)
    public int getTotalScore() { ... }

    // equals/hashCode за полем id
}
```

#### Faculty.java

```java
// Доменна сутність «Факультет»
public class Faculty {
    private final String name;       // назва (PK)
    private final int maxStudents;   // максимальна кількість місць

    // Конструктор з валідацією: name не порожній, maxStudents > 0
    public Faculty(String name, int maxStudents) { ... }
}
```

#### Grade.java

```java
// Доменна сутність «Оцінка ЗНО»
public class Grade {
    private final Subject subject; // предмет (enum)
    private final int score;       // бал (100–200)

    // Конструктор з валідацією діапазону балів
    public Grade(Subject subject, int score) { ... }
}
```

#### Application.java

```java
// Доменна сутність «Заявка на вступ»
public class Application {
    private final Applicant applicant;     // абітурієнт
    private final Faculty faculty;         // факультет
    private ApplicationStatus status;      // статус (PENDING → APPROVED/REJECTED)

    // Методи зміни статусу
    public void approve() { ... }
    public void reject() { ... }

    // Загальний бал абітурієнта
    public int getTotalScore() { return applicant.getTotalScore(); }
}
```

#### User.java

```java
// Доменна сутність «Користувач»
public class User {
    private final String username;     // логін (PK)
    private final String passwordHash; // BCrypt-хеш пароля
    private final Role role;           // роль (ADMIN/APPLICANT)
}
```

#### AdmissionSheet.java

```java
// Відомість зарахування на факультет
public class AdmissionSheet {
    private final Faculty faculty;
    private final List<Application> applications;

    // Ранжує заявки за балами, зараховує top-N (maxStudents)
    public void process() { ... }

    // Повертає списки зарахованих/відхилених
    public List<Application> getApproved() { ... }
    public List<Application> getRejected() { ... }
}
```

#### Enum-класи

```java
// Предмети ЗНО
public enum Subject { MATH, UKRAINIAN, HISTORY }

// Ролі користувачів
public enum Role { ADMIN, APPLICANT }

// Статуси заявки
public enum ApplicationStatus { PENDING, APPROVED, REJECTED }
```

---

### 4. Діаграма зв'язків

```
Applicant 1──* Grade
Applicant 1──* Application *──1 Faculty
AdmissionSheet ──1 Faculty, ──* Application
User (незалежна сутність)
```

---

### 5. Висновки

1. Розроблено 9 класів сутностей (6 класів + 3 enum) у пакеті `domain.model`.
2. Застосовано інкапсуляцію: поля `final`, валідація в конструкторах, незмінні колекції.
3. Бізнес-логіка розміщена в доменних об'єктах (підрахунок балів, ранжування, зміна статусу).
4. Класи не залежать від фреймворків — чиста доменна модель.
5. Зв'язки між сутностями відповідають реляційній моделі БД.
