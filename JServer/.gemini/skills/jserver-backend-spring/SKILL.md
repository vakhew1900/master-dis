---
name: jserver-backend-spring
description: Руководство по разработке Spring Boot бэкенда для JServer. Включает стандарты API, работы с БД и обработки файлов.
---

# JServer Backend Spring Skill

Этот skill предоставляет стандарты и правила для разработки серверной части JServer на Spring Boot.

## Архитектурные стандарты
- **Пакеты:** Все классы должны находиться в `org.master.diploma.backend`.
    - `entity`: JPA сущности.
    - `dao` (или `repository`): Spring Data интерфейсы.
    - `service`: Бизнес-логика.
    - `controller` (или `routes`): REST контроллеры.
    - `dto`: Объекты передачи данных.
    - `config`: Конфигурация (Security, MinIO, Swagger).

## Стандарты Entity
- Каждая сущность должна содержать константу `TABLE_NAME`.
- Внутри сущности должен быть статический класс `COLUMN_NAMES` для хранения названий всех колонок БД.
- Использовать эти константы в аннотациях `@Table`, `@Column`, `@JoinColumn`.
- Пример:
  ```java
  public static final String TABLE_NAME = "users";
  public static class COLUMN_NAMES {
      public static final String ID = "id";
  }
  @Column(name = COLUMN_NAMES.ID)
  ```

## Стандарты DTO
- Использовать статический класс `FIELDS` для хранения имен полей.
- Использовать эти константы в аннотации `@SerializedName` (для Gson) для обеспечения консистентности имен при сериализации в JSON.
- Пример:
  ```java
  public static class FIELDS {
      public static final String USER_NAME = "user_name";
  }
  @SerializedName(FIELDS.USER_NAME)
  private String userName;
  ```

## Стандарты API
- Использовать `ResponseEntity` для возврата ответов.
- По умолчанию Spring Boot (при наличии Gson в classpath и соответствующих настроек) сериализует объекты в JSON.
- Ошибки должны возвращаться в едином формате JSON (статус, сообщение, таймштамп).
- Пути:
    - `/api/auth/**` - публичные или для авторизации.
    - `/api/admin/**` - только для роли `ADMIN`.
    - `/api/student/**` - только для роли `STUDENT`.

## Сборка и Инструменты
- **Maven:** Для сборки проекта на данной машине использовать:
  `C:\Program Files\JetBrains\IntelliJ IDEA 2025.3.3\plugins\maven\lib\maven3\bin\mvn.cmd`

## OpenApi
- Для генерации документации использовать `springdoc-openapi-ui`.
- Контроллеры и методы должны быть аннотированы `@Tag` и `@Operation` для понятного описания в Swagger UI.
- Генерация JSON схемы: `mvn clean verify`. Файл будет доступен в `target/openapi.json`.

## Безопасность и Валидация
- Пароли хранить ОБЯЗАТЕЛЬНО в хешированном виде (`BCryptPasswordEncoder`).
- Публичная регистрация пользователей ОТСУТСТВУЕТ. Создание новых пользователей (студентов) выполняется только администратором через `AdminStudentController`.
- Персональные данные пользователей включают `firstName` (имя), `lastName` (фамилия) и `middleName` (отчество).
- Валидация входных данных через `@Valid` и `jakarta.validation`.
- Ограничение на загрузку ZIP:
    - Max size: 10MB (настраиваемо).
    - Max unzipped: 100MB.
    - Проверка на zip-bomb через подсчет коэффициента сжатия.

## Работа с файлами
- Для работы с файлами (MinIO, локальное хранилище) использовать абстракцию `FileService`.
- `MinioService` должен наследоваться от `FileService`.
- Все методы загрузки новых файлов должны проверять наличие старых файлов и удалять их для предотвращения накопления мусора.
- Метод `downloadRepository` должен возвращать `java.io.File`, указывающий на временную директорию с распакованным содержимым.

## Работа с Git репозиториями
- Использовать `JGit` для работы с загруженными архивами.
- Перед анализом архив должен быть распакован во временную директорию (`java.io.tmpdir`).
- После завершения анализа временные файлы должны быть удалены.

## Конфигурация
- Все настройки приложения должны дублироваться в файле `src/main/resources/application.properties.example`.
- Файл `.example` не должен содержать реальных секретов (паролей, ключей), только примеры или значения для разработки.

### Основные файлы конфигурации:
- `src/main/resources/application.properties`: Основной файл настроек (БД, MinIO, пути OpenAPI).
- `src/main/java/org/master/diploma/backend/config/`:
    - `SecurityConfig.java`: Настройка прав доступа (Spring Security) и `PasswordEncoder`.
    - `MinioConfig.java`: Инициализация `MinioClient` для работы с S3.
    - `GraphMethodConfig.java`: Регистрация доступных алгоритмов сравнения и строителей отчетов (Builders).
    - `Constants.java`: Константы путей API и названий бакетов MinIO.
    - `DataInitializer.java`: Начальная инициализация данных (создание первого админа).

### Настройка безопасности:
- Начальный пароль администратора задается через свойство `admin.initial-password`.

## Хранение данных
- Основные данные — Hibernate/JPA.
- Файлы (ZIP, репозитории) — MinIO/S3.
