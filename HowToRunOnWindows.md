# Инструкция по запуску проекта на Windows

Этот проект представляет собой стандартное Java-приложение на базе Spring Boot. Для его работы вам необходимо подготовить среду окружения.

## 1. Подготовка инструментов

Перед запуском убедитесь, что у вас установлены следующие компоненты:

### А. Установка Java (JDK 17+)
Проекту требуется Java 17.
- **Скачать:** [Eclipse Adoptium (Temurin)](https://adoptium.net/temurin/releases/?version=17) или [Oracle JDK](https://www.oracle.com/java/technologies/downloads/).
- **Проверка:** Откройте командную строку (CMD) и введите:
  ```cmd
  java -version
  ```

### Б. Установка Apache Maven
Maven нужен для сборки и управления зависимостями проекта.
- **Скачать:** [Официальный сайт Apache Maven](https://maven.apache.org/download.cgi).
- **Настройка:** Распакуйте архив в удобную папку (например, `C:\maven`). Добавьте путь к папке `bin` (например, `C:\maven\bin`) в системную переменную `PATH` в настройках Windows.
- **Проверка:** Введите в CMD:
  ```cmd
  mvn -version
  ```

### В. Установка MySQL Server
Для работы приложения необходим сервер MySQL.
- **Скачать:** [MySQL Installer for Windows](https://dev.mysql.com/downloads/installer/).
- **Настройка:** В процессе установки выберите "Developer Default", установите пароль для пользователя `root` (запомните его).
- **Клиент:** Удобно также установить **MySQL Workbench** (обычно идет в комплекте с установщиком) для управления базой данных.

---

## 2. Настройка базы данных

1. Откройте **MySQL Workbench** (или используйте терминал MySQL).
2. Создайте пустую схему (базу данных) с именем `admission_system`:
   ```sql
   CREATE DATABASE admission_system;
   ```
3. Откройте файл проекта `src/main/resources/application.properties` (если файла нет, создайте его) и укажите ваши данные для подключения:
   ```properties
   # Настройки MySQL
   spring.datasource.url=jdbc:mysql://localhost:3306/admission_system?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC&createDatabaseIfNotExist=true
   spring.datasource.username=root
   spring.datasource.password=root123
   spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
   ```

---

## 3. Запуск проекта

Перейдите в папку с проектом через командную строку (CMD или PowerShell):

```cmd
cd путь\к\папке\с\проектом
```

### Вариант 1: Запуск через Maven (рекомендуется для разработки)
Эта команда соберет проект и сразу запустит его:
```cmd
mvn spring-boot:run
```

### Вариант 2: Сборка и запуск через JAR-файл
Если вы хотите сначала собрать проект, а затем запустить его как готовое приложение:

1. **Сборка:**
   ```cmd
   mvn clean install
   ```
   *После завершения в папке `target` появится файл `AdmissionSystem-1.0-SNAPSHOT.jar`.*

2. **Запуск:**
   ```cmd
   java -jar target/AdmissionSystem-1.0-SNAPSHOT.jar
   ```

---

## 4. Проверка работы

После успешного запуска Spring Boot выведет логи в консоль. Когда увидите сообщение `Started ... in ... seconds`, приложение готово.

- **URL для доступа:** [http://localhost:8090](http://localhost:8090)
- **Приложение:** Откройте браузер и перейдите по этой ссылке.

> **Примечание:** Если при запуске возникают ошибки подключения к БД, проверьте, запущен ли сервер MySQL (это можно сделать через "Службы" (services.msc) в Windows, сервис `MySQL80` должен быть в статусе "Работает").