# Pioneer Banking API

Сервис управления пользователями, их контактными данными и банковскими счетами.

## Содержание

1. [Технологии и инструменты](#технологии-и-инструменты)  
2. [Сборка и запуск](#сборка-и-запуск)  
3. [Структура проекта](#структура-проекта)  
4. [Конфигурация](#конфигурация)  
5. [Описание основных фич](#описание-основных-фич)  
6. [Логика нестандартных решений](#логика-нестандартных-решений)  
7. [Тестирование](#тестирование)  
8. [API эндпоинты](#api-эндпоинты)  

---

## Технологии и инструменты

- Java 11+  
- Spring Boot 3.x  
  - Spring Web, Spring Data JPA, Spring Security, Spring Cache, Spring Scheduler  
- База данных: PostgreSQL  
- Кэширование: Caffeine (in-memory)  
- JWT-аутентификация (jjwt)  
- Swagger / OpenAPI (springdoc-openapi)  
- Maven  
- Lombok  
- Тестирование:
  - Unit: JUnit 5, Mockito  
  - Интеграция: Testcontainers, MockMvc  

---

## Сборка и запуск

1. Клонировать репозиторий  
   ```bash
   git clone https://github.com/deniman23/pioneer.git
   cd pioneer

2. Запустите контейнеры:

  ```bash
  Copy
  # если у вас установлен Compose V2 (плагин Docker):
  docker compose up -d

  # или, если установлен standalone docker-compose:
  docker-compose up -d
```
b) Вручную (без Docker)
Установите PostgreSQL и создайте базу user_service_db и пользователя admin/admin

3. Настроить application.properties
В src/main/resources/application.properties указано:

```
spring.application.name=pioneer

spring.datasource.url=jdbc:postgresql://localhost:5432/user_service_db
spring.datasource.username=admin
spring.datasource.password=admin
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.default_schema=public
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.task.scheduling.enabled=true

spring.flyway.enabled=true
spring.flyway.schemas=public
spring.flyway.default-schema=public
spring.flyway.locations=classpath:db/migration

security.jwt.secret=LDTuBWLlpQXzd0hlAxoNvA8AvLNJDawRe02KREMy5T4=
security.jwt.expire-ms=3600000
```

4. Сборка и запуск приложения
  ```bash
  Copy
  mvn clean package
  java -jar target/pioneer-0.0.1-SNAPSHOT.jar
```

Приложение будет доступно по адресу:
http://localhost:8080

5. Swagger UI
Документация REST API в Swagger UI:
Copy
http://localhost:8080/swagger-ui/index.html

API эндпоинты
Метод	Путь	Описание	Auth
POST	/api/auth/login	Получение JWT по email/phone + password	—
GET	/api/account	Баланс и детали счёта	Bearer
POST	/api/transfer	Перевод средств	Bearer
GET	/api/users	Поиск пользователей (фильтры + пагинация)	Bearer
GET	/api/users/{id}	Детали пользователя	Bearer
GET	/api/users/{id}/emails	Список e-mail пользователя	Bearer
POST	/api/users/{id}/emails	Добавить e-mail	Bearer
PUT	/api/users/{id}/emails/{eid}	Обновить e-mail	Bearer
DELETE	/api/users/{id}/emails/{eid}	Удалить e-mail	Bearer
GET	/api/users/{id}/phones	Список телефонов	Bearer
POST	/api/users/{id}/phones	Добавить телефон	Bearer
PUT	/api/users/{id}/phones/{pid}	Обновить телефон	Bearer
DELETE	/api/users/{id}/phones/{pid}	Удалить телефон	Bearer
