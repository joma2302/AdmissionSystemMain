# Звіт з лабораторної роботи № 9

**Тема:** Управління сесіями (HttpSession) в системі «Приймальна комісія»

---

## 1. Теоретичні відомості та налаштування

У Spring Boot робота з сесіями інтегрована з Spring Security. Сесія дозволяє серверу розпізнавати користувача між різними HTTP-запитами.

### Ключові налаштування у проекті

* **Механізм:** використовується cookie `JSESSIONID`, який створюється після успішної авторизації.
* **Конфігурація:** у файлі `SecurityConfig.java` визначено стратегію управління сесіями:

    * `maximumSessions(1)` — обмежує користувача однією активною сесією.
    * `invalidateHttpSession(true)` — повне знищення сесії під час виходу.
    * `deleteCookies("JSESSIONID")` — видалення ідентифікатора з браузера.

---

## 2. Реалізація у коді

### Конфігурація безпеки та сесії (`SecurityConfig.java`)

Цей код визначає поведінку системи після авторизації користувача.

```java
.logout(logout -> logout
    .logoutUrl("/logout")
    .logoutSuccessUrl("/login?logout=true")
    .invalidateHttpSession(true)
    .deleteCookies("JSESSIONID")
    .permitAll()
)
.sessionManagement(session -> session
    .maximumSessions(1)
);
```

---

### Використання даних сесії у шаблонах (`layout.ftl`)

У FreeMarker доступ до сесії здійснюється через об’єкт `SPRING_SECURITY_CONTEXT`, що дозволяє відобразити ім’я користувача та його роль.

```html
<div class="user-info">
    <#if Session?? && Session.SPRING_SECURITY_CONTEXT??>
        <#assign user = Session.SPRING_SECURITY_CONTEXT.authentication>
        ${user.name}
        <span class="role-badge">${user.authorities[0].authority}</span>
    </#if>
</div>
```

---

## 3. Опис виконання операцій із сесією

1. **Створення сесії:** після введення логіна і пароля на сторінці `/login` Spring Security створює `HttpSession` та передає браузеру cookie `JSESSIONID`.
2. **Підтримка стану:** при переході на сторінки `/apply` або `/admission` сервер зчитує ID сесії, отримує дані користувача та перевіряє його роль.
3. **Завершення сесії:** при виконанні запиту на `/logout` сесія анулюється на сервері, а cookie видаляється з браузера.

---

## 4. Висновки

Під час виконання лабораторної роботи було вивчено механізми управління станом користувача в системі Spring MVC.

* **HttpSession** забезпечує ідентифікацію користувача між запитами.
* Інтеграція з Spring Security автоматизує створення та знищення сесій.
* Обмеження кількості активних сесій підвищує рівень безпеки системи.

