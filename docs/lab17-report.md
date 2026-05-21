# Звіт з лабораторної роботи №17
## Тема: Написання контролерів адміністративної частини додатку


---

### Вступ
**Мета:** Розробити структуру контролерів для адміністративної частини інформаційної системи, забезпечити модульність коду та відокремлення моделей відображення від доменних моделей.
**Завдання:**
1.  Розділити монолітний `AdminController` на спеціалізовані контролери.
2.  Впровадити об'єкти моделей відображення (View Models) для передачі даних у Freemarker.
3.  Забезпечити коректну взаємодію між веб-шаром та сервісним шаром.
4.  Протестувати нові контролери.

---

### Опис результатів виконання
В ході лабораторної роботи було проведено глибокий рефакторинг веб-шару адміністративної частини:
*   **Рефакторинг контролерів:** Один великий `AdminController` був розділений на чотири:
    1.  `AdminController` — загальний дашборд та логіка зарахування.
    2.  `AdminApplicantController` — управління даними абітурієнтів.
    3.  `AdminApplicationController` — фільтрація та оновлення статусів заяв.
    4.  `AdminFacultyController` — управління факультетами та вимогами до балів.
*   **Впровадження View Models:** Створено класи `ApplicantViewModel` та `FacultyViewModel`, які містять лише необхідні для відображення дані. Це дозволило зробити шаблони Freemarker «тупими» та незалежними від внутрішньої структури доменних моделей.
*   **Маппінг:** Логіку перетворення доменних моделей у ViewModel винесено в `ResponseMapper`, що забезпечує чистоту коду контролерів.

---

### Текст програми з коментарями

#### AdminApplicantController.java
```java
@Controller
@RequestMapping("/admin/applicants")
public class AdminApplicantController {
    // Делегування роботи сервісам та репозиторіям
    private final ApplicantService applicantService;
    private final GradeRepository gradeRepository;
    private final ApplicationRepository applicationRepository;

    // Відображення списку абітурієнтів
    @GetMapping
    public String listApplicants(Model model) {
        model.addAttribute("applicants", applicantService.getAllApplicants());
        return "admin/applicants";
    }

    // Детальна інформація з використанням ViewModel
    @GetMapping("/{id}")
    public String showApplicantDetails(@PathVariable String id, Model model) {
        applicantService.getApplicant(id).ifPresent(applicant -> {
            List<Grade> grades = gradeRepository.findByApplicantId(id);
            List<Application> applications = applicationRepository.findByApplicantId(id);
            // Перетворення доменних моделей у ViewModel через маппер
            model.addAttribute("applicant", ResponseMapper.toApplicantViewModel(applicant, grades, applications));
        });
        return "admin/applicant-details";
    }
}
```

---

### Вхідні та вихідні дані
*   **Вхідні дані:** HTTP-запити (GET/POST), параметри форми (ID абітурієнта, назва факультету, статус заяви).
*   **Вихідні дані:** Рендеринг HTML-сторінок на основі Freemarker шаблонів з переданими даними (об'єкти `ViewModel`, списки `DTO`).

---

### Аналіз результатів та висновки
В результаті виконання роботи було досягнуто:
1.  **Покращення структури:** Код став більш модульним та легким для підтримки (SRP — Single Responsibility Principle).
2.  **Безпека та гнучкість:** Використання View Models запобігає випадковому витоку зайвих даних з доменних моделей у веб-інтерфейс та спрощує зміну дизайну без зміни бізнес-логіки.
3.  **Тестованість:** Спеціалізовані контролери легше тестувати ізольовано.

**Висновок:** Рефакторинг контролерів та впровадження View Models є критично важливими кроками для створення професійної веб-системи, що відповідає сучасним архітектурним стандартам Spring MVC.
