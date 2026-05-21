# ЗВІТ З НАВЧАЛЬНОЇ ПРАКТИКИ
## Лабораторна робота №13
### Тема: Розширення доменної моделі та вдосконалення користувацького інтерфейсу адміністративної частини

---

### 1. Вступ
**Напрям дослідження:** Розробка та вдосконалення систем управління базами даних, проектування людино-машинного інтерфейсу та реалізація складної бізнес-логіки в корпоративних Java-додатках.

**Мета роботи:** Розширити функціональні можливості адміністративного модуля системи "Приймальна комісія", впровадити механізм предметних вимог для факультетів та покращити зручність користування (UX/UI) веб-інтерфейсом.

**Завдання:**
1. Розробити та інтегрувати нові доменні сутності: `Admin` та `SubjectRequirement`.
2. Модернізувати існуючу сутність `Faculty` для підтримки списку вимог до вступних балів.
3. Реалізувати збереження нових структур даних у MySQL за допомогою оновленого `MySqlFacultyRepository`.
4. Переробити шаблони FreeMarker (`.ftl`) для чіткого розділення ролей "Адміністратор" та "Абітурієнт".
5. Забезпечити покриття нового функціоналу юніт-тестами.

---

### 2. Лаконічний опис результатів виконання
В ході лабораторної роботи було виконано комплексне оновлення системи.

**Ключові результати:**
- **Доменна модель:** Додано клас `Admin` для ідентифікації адміністраторів та `SubjectRequirement` для формалізації вимог до мінімальних балів з окремих предметів.
- **Персистентність:** Оновлено `MySqlFacultyRepository`, який тепер підтримує транзакційний запис факультетів разом із їхніми вимогами (використання `batch updates` та `manual commit`).
- **База даних:** Створено таблицю `subject_requirements` та налаштовано зв'язки з таблицею `faculties`.
- **Веб-інтерфейс:** 
    - Повністю змінено `layout.ftl`: навігаційна панель тепер динамічно змінюється залежно від ролі авторизованого користувача.
    - Оновлено `index.ftl`: додано інформативні картки дій для швидкого доступу до функцій.
    - Створено інтерфейс керування вимогами в `admin/faculties.ftl`, що дозволяє адміністратору встановлювати поріг балів для кожного предмета.

---

### 3. Текст програми з коментарями

#### 3.1. Сутність «Адміністратор» (Admin.java)
```java
public class Admin {
    private final String username; // Унікальний логін
    private final String firstName;
    private final String lastName;
    private final String email;

    public Admin(String username, String firstName, String lastName, String email) {
        // Валідація вхідних даних для забезпечення цілісності об'єкта
        Objects.requireNonNull(username, "Логін не може бути null");
        if (!email.contains("@")) throw new IllegalArgumentException("Некоректний формат email");

        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    /** Повертає повне ім'я для відображення в інтерфейсі */
    public String getFullName() {
        return lastName + " " + firstName;
    }
}
```

#### 3.2. Логіка збереження вимог (MySqlFacultyRepository.java)
```java
@Override
public void save(Faculty faculty) {
    String insertFaculty = "INSERT INTO faculties (name, max_students) VALUES (?, ?) " +
                           "ON DUPLICATE KEY UPDATE max_students = ?";
    String deleteRequirements = "DELETE FROM subject_requirements WHERE faculty_name = ?";
    String insertRequirement = "INSERT INTO subject_requirements (faculty_name, subject, minimum_score) VALUES (?, ?, ?)";

    try (Connection conn = dataSource.getConnection()) {
        conn.setAutoCommit(false); // Початок транзакції
        try {
            // 1. Оновлення основних даних факультету
            try (PreparedStatement stmt = conn.prepareStatement(insertFaculty)) {
                stmt.setString(1, faculty.getName());
                stmt.setInt(2, faculty.getMaxStudents());
                stmt.setInt(3, faculty.getMaxStudents());
                stmt.executeUpdate();
            }

            // 2. Очищення старих вимог перед записом нових
            try (PreparedStatement stmt = conn.prepareStatement(deleteRequirements)) {
                stmt.setString(1, faculty.getName());
                stmt.executeUpdate();
            }

            // 3. Пакетне вставлення нових вимог
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
            conn.commit(); // Підтвердження транзакції
        } catch (SQLException e) {
            conn.rollback(); // Відкат у разі помилки
            throw e;
        }
    } catch (SQLException e) {
        throw new RuntimeException("Помилка БД", e);
    }
}
```

---

### 4. Вхідні та вихідні дані програми

**Вхідні дані:**
- Параметри профілю адміністратора (ПІБ, email, логін).
- Конфігурація факультетів: назва, ліміт місць, перелік обов'язкових предметів та мінімальні бали (від 100 до 200).
- Ролі користувачів у сесії (для розмежування прав доступу в UI).

**Вихідні дані:**
- Візуалізація списків факультетів з детальним описом вимог.
- Динамічне меню навігації (наприклад, кнопка "Адмін-панель" видима лише для ADMIN).
- Записи в таблицях `faculties` та `subject_requirements` у базі даних MySQL.
- Результати виконання юніт-тестів у консолі (повідомлення про успішне проходження).

---

### 5. Змістовний аналіз та висновки

**Аналіз результатів:**
Проведене проектування та програмування дозволило значно підвищити інтелектуальність системи. Впровадження `SubjectRequirement` дає можливість автоматично відсіювати абітурієнтів, чиї бали нижчі за встановлений поріг, ще на етапі подачі заявки. Рефакторинг шаблонів FreeMarker покращив UX: користувачі більше не бачать зайвих функцій, які не відповідають їхній ролі. Тестування за допомогою `DomainModelTest` підтвердило коректність роботи валідацій (наприклад, перевірка email або діапазону балів).

**Висновки:**
В ході виконання лабораторної роботи №13 було успішно розширено доменну модель та вдосконалено користувацький інтерфейс. Використання транзакцій при роботі з БД забезпечило надійність збереження пов'язаних даних. Результати тестування (20 успішних тестів) свідчать про стабільність системи після внесених змін. Розроблений функціонал створює фундамент для реалізації повної автоматизації процесу вступу.
