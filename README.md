# Pioneer Banking API

Сервис управления пользователями, их контактными данными и банковскими счетами.
  

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


## Запуск проекта

Запуск через idea 

Приложение будет доступно по адресу:
http://localhost:8080

## Swagger UI
Документация REST API в Swagger UI:
Copy
http://localhost:8080/swagger-ui/index.html

##API эндпоинты
| Метод  | Путь                              | Описание                                           | Auth   |
| ------ | --------------------------------- | -------------------------------------------------- | ------ |
| POST   | `/api/auth/login`                 | Получение JWT по email/phone + password            | —      |
| GET    | `/api/account`                    | Баланс и детали счёта                              | Bearer |
| POST   | `/api/transfer`                   | Перевод средств                                    | Bearer |
| GET    | `/api/users`                      | Поиск пользователей (фильтры + пагинация)          | Bearer |
| GET    | `/api/users/{id}`                 | Детали пользователя                                | Bearer |
| GET    | `/api/users/{id}/emails`          | Список e-mail пользователя                         | Bearer |
| POST   | `/api/users/{id}/emails`          | Добавить e-mail                                    | Bearer |
| PUT    | `/api/users/{id}/emails/{eid}`    | Обновить e-mail                                    | Bearer |
| DELETE | `/api/users/{id}/emails/{eid}`    | Удалить e-mail                                     | Bearer |
| GET    | `/api/users/{id}/phones`          | Список телефонов                                   | Bearer |
| POST   | `/api/users/{id}/phones`          | Добавить телефон                                   | Bearer |
| PUT    | `/api/users/{id}/phones/{pid}`    | Обновить телефон                                   | Bearer |
| DELETE | `/api/users/{id}/phones/{pid}`    | Удалить телефон                                    | Bearer |
