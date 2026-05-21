# Звіт з лабораторної роботи № 10

**Тема:** Профілювання та тестування програми

---

## 1. Вступ

**Напрям дослідження:** профілювання та тестування інформаційної системи «Приймальна комісія».

**Мета:** набути практичних навичок написання модульних та інтеграційних тестів, виявлення та виправлення дефектів у програмному коді, а також оптимізації роботи проекту.

**Завдання:**
1. Опрацювати теоретичний матеріал з профілювання та тестування.
2. Провести тестування проекту за допомогою JUnit 5.
3. Виявити та виправити дефекти коду.
4. Оптимізувати роботу проекту.
5. Оформити звіт та підготуватися до захисту.

---

## 2. Опис тестової інфраструктури

### 2.1. Тестова база даних

Для тестування використовується вбудована база H2 у режимі сумісності з MySQL. Це дозволяє запускати тести без зовнішнього сервера БД.

```java
// Підключення до in-memory бази H2
sharedConnection = DriverManager.getConnection(
        "jdbc:h2:mem:test;MODE=MYSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
dataSource = new TestDataSource(sharedConnection);
DatabaseInitializer.initialize(sharedConnection);
```

### 2.2. TestDataSource — обгортка з'єднання

Клас `TestDataSource` реалізує інтерфейс `DataSource` та повертає проксі-обгортку над спільним з'єднанням, яка ігнорує виклик `close()`, щоб з'єднання залишалось відкритим між тестами.

```java
// Проксі, що ігнорує close() — з'єднання залишається відкритим між тестами
private static Connection nonClosingProxy(Connection target) {
    return (Connection) Proxy.newProxyInstance(
            Connection.class.getClassLoader(),
            new Class<?>[]{Connection.class},
            (proxy, method, args) -> {
                if ("close".equals(method.getName())) {
                    return null; // ігноруємо close()
                }
                return method.invoke(target, args);
            }
    );
}
```

---

## 3. Результати тестування

Проект містить **4 тестових класи** із загальною кількістю **35 тестів**.

### 3.1. DomainModelTest (16 тестів)

Модульні тести доменної моделі, що перевіряють бізнес-логіку без звернення до бази даних.

| Тест                                                  | Опис                                                    | Результат |
| ----------------------------------------------------- | ------------------------------------------------------- | --------- |
| `gradeAcceptsMinAndMaxScore`                          | Оцінка приймає мінімальний (1) та максимальний (12) бал | ✅         |
| `gradeRejectsTooLowScore`                             | Оцінка відхиляє бал ≤ 0                                 | ✅         |
| `gradeRejectsTooHighScore`                            | Оцінка відхиляє бал > 12                                | ✅         |
| `gradeDoesNotAllowNullSubject`                        | Оцінка не дозволяє null-предмет                         | ✅         |
| `applicantComputesTotalScoreFromMultipleGrades`       | Підрахунок загальної суми балів                         | ✅         |
| `applicantForbidsDuplicateSubjectGrade`               | Заборона дублювання предмету                            | ✅         |
| `applicantRejectsEmptyId`                             | Відхилення порожнього ID                                | ✅         |
| `applicantRejectsEmptyFirstName`                      | Відхилення порожнього імені                             | ✅         |
| `applicantRejectsEmptyLastName`                       | Відхилення порожнього прізвища                          | ✅         |
| `facultyRejectsEmptyName`                             | Відхилення порожньої назви факультету                   | ✅         |
| `facultyRejectsZeroCapacity`                          | Відхилення нульової кількості місць                     | ✅         |
| `facultyRejectsNegativeCapacity`                      | Відхилення від'ємної кількості місць                    | ✅         |
| `applicationHasPendingStatusByDefault`                | Заявка має статус PENDING за замовчуванням              | ✅         |
| `admissionSheetSelectsTopApplicants`                  | Відомість зараховує абітурієнтів з найвищими балами     | ✅         |
| `admissionSheetRejectsSameApplicationTwice`           | Заборона повторної реєстрації заявки                    | ✅         |
| `admissionSheetRejectsApplicationForDifferentFaculty` | Заборона заявки на інший факультет                      | ✅         |

### 3.2. MySqlRepositoryTest (9 тестів)

Інтеграційні тести репозиторіїв, що перевіряють коректність збереження та зчитування даних з бази.

| Тест                                        | Опис                                              | Результат |
| ------------------------------------------- | ------------------------------------------------- | --------- |
| `savesAndFindsFacultyByName`                | Збереження та пошук факультету за назвою          | ✅         |
| `findsAllSavedFaculties`                    | Отримання списку всіх факультетів                 | ✅         |
| `savesApplicantAndLoadsWithGrades`          | Збереження абітурієнта з оцінками та завантаження | ✅         |
| `findsGradesForApplicant`                   | Пошук оцінок за ID абітурієнта                    | ✅         |
| `findsApplicantsByFacultyName`              | Пошук абітурієнтів за назвою факультету           | ✅         |
| `findsApplicationsByApplicant`              | Пошук заявок за ID абітурієнта                    | ✅         |
| `findsApplicationsOrderedByScoreDescending` | Ранжування заявок за балами (спадання)            | ✅         |
| `updatesApplicationStatusCorrectly`         | Оновлення статусу заявки                          | ✅         |
| `deletesApplicantAndCascadesGrades`         | Каскадне видалення абітурієнта з оцінками         | ✅         |

### 3.3. ServiceLayerTest (9 тестів)

Тести сервісного шару, що перевіряють бізнес-операції через сервіси.

| Тест                                     | Опис                                       | Результат |
| ---------------------------------------- | ------------------------------------------ | --------- |
| `registersApplicantWithMultipleGrades`   | Реєстрація абітурієнта з кількома оцінками | ✅         |
| `registersApplicantWithEmptyGrades`      | Реєстрація абітурієнта без оцінок          | ✅         |
| `forbidsApplyingToSecondFaculty`         | Заборона подачі заявки на другий факультет | ✅         |
| `throwsWhenApplicantDoesNotExist`        | Помилка при неіснуючому абітурієнті        | ✅         |
| `throwsWhenFacultyDoesNotExist`          | Помилка при неіснуючому факультеті         | ✅         |
| `ranksApplicantsByScoreDescending`       | Ранжування абітурієнтів за балами          | ✅         |
| `processAdmissionBuildsCorrectLists`     | Формування списків зарахованих/відхилених  | ✅         |
| `getTotalScoreComputesCorrectly`         | Коректний підрахунок загального балу       | ✅         |
| `getTotalScoreThrowsForMissingApplicant` | Помилка при відсутньому абітурієнті        | ✅         |

### 3.4. ApplicationServiceIntegrationTest (1 тест)

Наскрізний інтеграційний тест повного циклу вступу.

| Тест                   | Опис                                                                                              | Результат |
| ---------------------- | ------------------------------------------------------------------------------------------------- | --------- |
| `fullAdmissionProcess` | Повний цикл: створення факультету → реєстрація → подача заявок → зарахування → перевірка статусів | ✅         |

### 3.5. AuthTest (1 тест)

Перевірка механізму реєстрації та автентифікації користувачів із використанням мокованого `UserRepository` та BCryptPasswordEncoder.

| Тест                    | Опис                                          | Результат |
| ----------------------- | --------------------------------------------- | --------- |
| `registerAndAuthenticate` | Реєстрація користувача та перевірка паролю   | ✅         |

```java
package org.example.infrastructure.persistence.mysql.AuthService;

import org.example.application.service.AuthService;
import org.example.domain.model.Role;
import org.example.domain.model.User;
import org.example.domain.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

public class AuthTest {
    public static void main(String[] args) {
        UserRepository repo = new UserRepository() {
            User storedUser = null;

            @Override
            public void save(User user) { storedUser = user; }

            @Override
            public Optional<User> findByUsername(String username) {
                if (storedUser != null && storedUser.getUsername().equals(username)) {
                    return Optional.of(storedUser);
                }
                return Optional.empty();
            }

            @Override
            public boolean existsByUsername(String username) {
                return storedUser != null && storedUser.getUsername().equals(username);
            }
        };

        PasswordEncoder encoder = new BCryptPasswordEncoder();
        AuthService authService = new AuthService(repo, encoder);

        String testUsername = "testuser";
        String testPassword = "12345";
        authService.registerUser(testUsername, testPassword, Role.APPLICANT);
        System.out.println("Користувач зареєстрований: " + testUsername);

        try {
            var userDetails = authService.loadUserByUsername(testUsername);
            System.out.println("UserDetails завантажено: " + userDetails.getUsername());
            boolean matches = encoder.matches(testPassword, userDetails.getPassword());
            System.out.println("Пароль співпадає: " + matches);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

**Результати тестування AuthService:**

* Користувач успішно зареєстрований.
* UserDetails завантажено коректно.
* Пароль після кодування збігається з оригіналом.

---

## 4. Виявлені та виправлені дефекти

### 4.1. Помилка компіляції: відсутній клас `NonClosingConnection`

**Проблема:** клас `TestDataSource` посилався на неіснуючий клас `NonClosingConnection`, що призводило до помилки компіляції.

**Рішення:** замінено `new NonClosingConnection(connection)` на динамічний проксі (`java.lang.reflect.Proxy`), який обгортає `Connection` та ігнорує виклик `close()`.

```java
// До виправлення (не компілювалось):
return new NonClosingConnection(connection);

// Після виправлення:
return nonClosingProxy(connection);
```

### 4.2. Неправильний формат повного імені абітурієнта

**Проблема:** метод `Applicant.getFullName()` повертав `"Ім'я Прізвище"`, тоді як усі тести очікували формат `"Прізвище Ім'я"`. Це призвело до провалу 5 тестів.

**Рішення:** змінено порядок конкатенації у методі `getFullName()`.

```java
// До виправлення:
public String getFullName() {
    return firstName + " " + lastName;
}

// Після виправлення:
public String getFullName() {
    return lastName + " " + firstName;
}
```

---

## 5. Оптимізація роботи проекту

1. **Спільне з'єднання з БД у тестах:** використання одного з'єднання (`sharedConnection`) для всіх тестів у класі замість створення нового для кожного тесту, що значно прискорює виконання.
2. **Проксі замість наслідування:** використання `java.lang.reflect.Proxy` для обгортки `Connection` замість окремого класу-обгортки, що зменшує кількість коду та спрощує підтримку.
3. **Очищення даних між тестами:** використання `DELETE FROM` у `@BeforeEach` забезпечує ізоляцію тестів без перестворення схеми БД.

---

## 6. Вхідні та вихідні дані

### Вхідні дані (тестові):

* Абітурієнти: Марія Шевченко, Андрій Бондаренко, Тетяна Мельник
* Предмети: BIOLOGY, CHEMISTRY, ENGLISH, HISTORY, GEOGRAPHY
* Оцінки: від 1 до 12 балів
* Факультети: Медицина (різна кількість місць), Біологія, Право

### Вихідні дані:

* Результат виконання 36 тестів: **36 passed, 0 failed** (з урахуванням AuthTest)
* Статуси заявок: PENDING → ADMITTED / REJECTED
* Ранжовані списки абітурієнтів за сумою балів

---

## 7. Висновки

Під час виконання лабораторної роботи було проведено комплексне тестування системи «Приймальна комісія»:

1. **Модульне тестування** доменної моделі підтвердило коректність валідації вхідних даних, обчислення балів та алгоритму зарахування.
2. **Інтеграційне тестування** репозиторіїв перевірило коректність CRUD-операцій, каскадного видалення та ранжування за балами.
3. **Тестування сервісного шару** підтвердило правильність бізнес-логіки: реєстрації, подачі заявок, обмежень та формування відомостей зарахування.
4. **Наскрізний тест** продемонстрував коректну роботу повного циклу вступної кампанії.
5. **Тестування AuthService** підтвердило правильну роботу реєстрації та авторизації користувачів із хешуванням паролів.
6. Було виявлено та виправлено **2 дефекти**: помилку компіляції (`NonClosingConnection`) та некоректний формат повного імені абітурієнта.
7. Усі **36 тестів** успішно пройдені після виправлення дефектів.

Отримано практичні навички роботи з JUnit 5, H2 Database, патерном Proxy, Spring Security (BCrypt) та методологією тестування багатошарової архітектури.
