# Система лотереи MEPHI Practicum

Система для проведения лотерейных тиражей с возможностью покупки билетов, определения выигрышей и интеграции с платежными системами.

## Возможности

- Аутентификация пользователей с помощью JWT
- Разграничение прав доступа (USER, ADMIN)
- Управление лотерейными тиражами
- Покупка и валидация билетов
- Обработка платежей
- Определение результатов тиражей
- Экспорт данных (CSV/JSON)
- Статистика и история пользователей

## Технологический стек

- Java 23
- Spring Boot 3.x
- PostgreSQL 17
- Spring Security с JWT
- Spring Data JPA
- Flyway для миграций базы данных
- Docker для контейнеризации

## Начало работы

### Требования

- Java 23 JDK
- Docker и Docker Compose
- Gradle

### Запуск приложения

1. Клонируйте репозиторий:
```
git clone https://github.com/yourusername/MEPHIPracticumLottery.git
cd MEPHIPracticumLottery
```

2. Запустите PostgreSQL с помощью Docker Compose:
```
docker-compose up -d
```

3. Соберите и запустите приложение:
```
./gradlew clean build
./gradlew bootRun
```

Приложение будет доступно по адресу http://localhost:8088

### Документация API

Swagger UI доступен по адресу http://localhost:8088/swagger-ui.html

#### Как использовать Swagger UI

1. Откройте Swagger UI в браузере: http://localhost:8088/swagger-ui.html
2. Для авторизации:
   - Выполните запрос POST `/api/auth/login` с вашими учетными данными
   - Скопируйте полученный token
   - Нажмите кнопку "Authorize" в правом верхнем углу
   - Введите токен в формате `Bearer {полученный_токен}` и нажмите "Authorize"
3. После авторизации все API методы станут доступны для тестирования

## Подробная документация API

> **Важно:** Все API-запросы, кроме авторизации, требуют JWT-токен в заголовке Authorization: `Bearer {token}`

### Аутентификация

#### Регистрация нового пользователя
- **Метод**: POST 
- **URL**: `/api/auth/register`
- **Тело запроса**: 
  ```json
  {
    "username": "user@example.com",
    "password": "password123"
  }
  ```
- **Пример выполнения**:
  ```bash
  curl -X POST 'http://localhost:8088/api/auth/register' \
    -H 'Content-Type: application/json' \
    -d '{"username":"user@example.com","password":"password123"}'
  ```
- **Ответ**: 
  ```json
  {
    "message": "User registered successfully"
  }
  ```
- **Описание**: Регистрирует нового пользователя. Первый зарегистрированный пользователь автоматически получает роль ADMIN.

#### Аутентификация пользователя
- **Метод**: POST 
- **URL**: `/api/auth/login`
- **Тело запроса**: 
  ```json
  {
    "username": "user@example.com",
    "password": "password123"
  }
  ```
- **Пример выполнения**:
  ```bash
  curl -X POST 'http://localhost:8088/api/auth/login' \
    -H 'Content-Type: application/json' \
    -d '{"username":"user@example.com","password":"password123"}'
  ```
- **Ответ**: 
  ```json
  {
    "message": "Login successful",
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  }
  ```
- **Описание**: Аутентифицирует пользователя и возвращает JWT токен для дальнейших запросов.

### Тиражи

#### Получение всех тиражей
- **Метод**: GET 
- **URL**: `/api/draws`
- **Пример выполнения**:
  ```bash
  curl -X GET 'http://localhost:8088/api/draws' \
    -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...'
  ```
- **Ответ**: Список всех тиражей в системе
- **Описание**: Возвращает все тиражи, доступные в системе.

#### Получение активных тиражей
- **Метод**: GET 
- **URL**: `/api/draws/active`
- **Пример выполнения**:
  ```bash
  curl -X GET 'http://localhost:8088/api/draws/active' \
    -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...'
  ```
- **Ответ**: Список активных тиражей
- **Описание**: Возвращает только активные тиражи, на которые можно покупать билеты.

#### Получение тиража по ID
- **Метод**: GET 
- **URL**: `/api/draws/{id}`
- **Параметры пути**: 
  - `id`: UUID идентификатор тиража
- **Пример выполнения**:
  ```bash
  curl -X GET 'http://localhost:8088/api/draws/550e8400-e29b-41d4-a716-446655440000' \
    -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...'
  ```
- **Ответ**: Информация о запрошенном тираже
- **Описание**: Возвращает детальную информацию о тираже по его ID.

#### Создание нового тиража (только для ADMIN)
- **Метод**: POST 
- **URL**: `/api/admin/draws`
- **Тело запроса**: 
  ```json
  {
    "lotteryType": "FIVE_OUT_OF_36",
    "startTime": "2024-05-20T12:00:00"
  }
  ```
- **Пример выполнения**:
  ```bash
  curl -X POST 'http://localhost:8088/api/admin/draws' \
    -H 'Content-Type: application/json' \
    -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...' \
    -d '{"lotteryType":"FIVE_OUT_OF_36","startTime":"2024-05-20T12:00:00"}'
  ```
- **Ответ**: Созданный тираж
- **Описание**: Создает новый тираж. Доступно только администраторам. Типы лотерей: FIVE_OUT_OF_36, SIX_OUT_OF_45, SEVEN_OUT_OF_49.

#### Отмена тиража (только для ADMIN)
- **Метод**: PUT 
- **URL**: `/api/admin/draws/{id}/cancel`
- **Параметры пути**: 
  - `id`: UUID идентификатор тиража
- **Пример выполнения**:
  ```bash
  curl -X PUT 'http://localhost:8088/api/admin/draws/550e8400-e29b-41d4-a716-446655440000/cancel' \
    -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...'
  ```
- **Ответ**: Обновленный статус тиража
- **Описание**: Отменяет тираж. Доступно только администраторам.

### Билеты

#### Получение билетов текущего пользователя
- **Метод**: GET 
- **URL**: `/api/tickets`
- **Пример выполнения**:
  ```bash
  curl -X GET 'http://localhost:8088/api/tickets' \
    -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...'
  ```
- **Ответ**: Список билетов пользователя
- **Описание**: Возвращает все билеты, принадлежащие текущему пользователю.

#### Получение билета по ID
- **Метод**: GET 
- **URL**: `/api/tickets/{id}`
- **Параметры пути**: 
  - `id`: UUID идентификатор билета
- **Пример выполнения**:
  ```bash
  curl -X GET 'http://localhost:8088/api/tickets/550e8400-e29b-41d4-a716-446655440000' \
    -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...'
  ```
- **Ответ**: Информация о билете
- **Описание**: Возвращает детальную информацию о билете по его ID.

#### Получение билетов пользователя для тиража
- **Метод**: GET 
- **URL**: `/api/draws/{drawId}/tickets`
- **Параметры пути**: 
  - `drawId`: UUID идентификатор тиража
- **Пример выполнения**:
  ```bash
  curl -X GET 'http://localhost:8088/api/draws/550e8400-e29b-41d4-a716-446655440000/tickets' \
    -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...'
  ```
- **Ответ**: Список билетов пользователя для указанного тиража
- **Описание**: Возвращает все билеты текущего пользователя для конкретного тиража.

#### Получение истории билетов текущего пользователя
- **Метод**: GET 
- **URL**: `/api/users/me/history`
- **Пример выполнения**:
  ```bash
  curl -X GET 'http://localhost:8088/api/users/me/history' \
    -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...'
  ```
- **Ответ**: История покупок билетов пользователя
- **Описание**: Возвращает историю покупок билетов текущего пользователя.

### Результаты тиражей

#### Получение результатов тиража
- **Метод**: GET 
- **URL**: `/api/draws/{drawId}/results`
- **Параметры пути**: 
  - `drawId`: UUID идентификатор тиража
- **Пример выполнения**:
  ```bash
  curl -X GET 'http://localhost:8088/api/draws/550e8400-e29b-41d4-a716-446655440000/results' \
    -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...'
  ```
- **Ответ**: Результаты розыгрыша
- **Описание**: Возвращает результаты проведенного тиража.

#### Проверка результата билета
- **Метод**: GET 
- **URL**: `/api/tickets/{ticketId}/check-result`
- **Параметры пути**: 
  - `ticketId`: UUID идентификатор билета
- **Пример выполнения**:
  ```bash
  curl -X GET 'http://localhost:8088/api/tickets/550e8400-e29b-41d4-a716-446655440000/check-result' \
    -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...'
  ```
- **Ответ**: Результат проверки билета
- **Описание**: Проверяет результат конкретного билета в тираже.

#### Генерация результата тиража (только для ADMIN)
- **Метод**: POST 
- **URL**: `/api/admin/draws/{drawId}/generate-result`
- **Параметры пути**: 
  - `drawId`: UUID идентификатор тиража
- **Пример выполнения**:
  ```bash
  curl -X POST 'http://localhost:8088/api/admin/draws/550e8400-e29b-41d4-a716-446655440000/generate-result' \
    -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...'
  ```
- **Ответ**: Сгенерированные результаты тиража
- **Описание**: Генерирует результаты тиража. Доступно только администраторам.

### Экспорт данных

#### Экспорт результатов тиража в CSV
- **Метод**: GET 
- **URL**: `/api/draws/{drawId}/export/csv`
- **Параметры пути**: 
  - `drawId`: UUID идентификатор тиража
- **Пример выполнения**:
  ```bash
  curl -X GET 'http://localhost:8088/api/draws/550e8400-e29b-41d4-a716-446655440000/export/csv' \
    -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...' \
    -o results.csv
  ```
- **Ответ**: Файл CSV с результатами тиража
- **Описание**: Экспортирует результаты тиража в формате CSV.

#### Экспорт результатов тиража в JSON
- **Метод**: GET 
- **URL**: `/api/draws/{drawId}/export/json`
- **Параметры пути**: 
  - `drawId`: UUID идентификатор тиража
- **Пример выполнения**:
  ```bash
  curl -X GET 'http://localhost:8088/api/draws/550e8400-e29b-41d4-a716-446655440000/export/json' \
    -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...' \
    -o results.json
  ```
- **Ответ**: Файл JSON с результатами тиража
- **Описание**: Экспортирует результаты тиража в формате JSON.

#### Экспорт месячной статистики в CSV (только для ADMIN)
- **Метод**: GET 
- **URL**: `/api/statistics/{year}/{month}/csv`
- **Параметры пути**: 
  - `year`: Год (например, 2024)
  - `month`: Месяц (1-12)
- **Пример выполнения**:
  ```bash
  curl -X GET 'http://localhost:8088/api/statistics/2024/5/csv' \
    -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...' \
    -o statistics.csv
  ```
- **Ответ**: Файл CSV с месячной статистикой
- **Описание**: Экспортирует месячную статистику в формате CSV. Доступно только администраторам.

#### Экспорт месячной статистики в JSON (только для ADMIN)
- **Метод**: GET 
- **URL**: `/api/statistics/{year}/{month}/json`
- **Параметры пути**: 
  - `year`: Год (например, 2024)
  - `month`: Месяц (1-12)
- **Пример выполнения**:
  ```bash
  curl -X GET 'http://localhost:8088/api/statistics/2024/5/json' \
    -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...' \
    -o statistics.json
  ```
- **Ответ**: Файл JSON с месячной статистикой
- **Описание**: Экспортирует месячную статистику в формате JSON. Доступно только администраторам.

#### Экспорт истории пользователя в CSV
- **Метод**: GET 
- **URL**: `/api/users/{userId}/history/csv`
- **Параметры пути**: 
  - `userId`: UUID идентификатор пользователя
- **Пример выполнения**:
  ```bash
  curl -X GET 'http://localhost:8088/api/users/550e8400-e29b-41d4-a716-446655440000/history/csv' \
    -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...' \
    -o user_history.csv
  ```
- **Ответ**: Файл CSV с историей пользователя
- **Описание**: Экспортирует историю пользователя в формате CSV. Пользователи могут экспортировать только свою историю, администраторы - историю любого пользователя.

#### Экспорт истории пользователя в JSON
- **Метод**: GET 
- **URL**: `/api/users/{userId}/history/json`
- **Параметры пути**: 
  - `userId`: UUID идентификатор пользователя
- **Пример выполнения**:
  ```bash
  curl -X GET 'http://localhost:8088/api/users/550e8400-e29b-41d4-a716-446655440000/history/json' \
    -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...' \
    -o user_history.json
  ```
- **Ответ**: Файл JSON с историей пользователя
- **Описание**: Экспортирует историю пользователя в формате JSON. Пользователи могут экспортировать только свою историю, администраторы - историю любого пользователя.

### Платежи

#### Создание нового счета
- **Метод**: POST 
- **URL**: `/api/invoice`
- **Тело запроса**: 
  ```json
  {
    "amount": 500.00,
    "description": "Покупка билетов на тираж 5/36"
  }
  ```
- **Пример выполнения**:
  ```bash
  curl -X POST 'http://localhost:8088/api/invoice' \
    -H 'Content-Type: application/json' \
    -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...' \
    -d '{"amount":500.00,"description":"Покупка билетов на тираж 5/36"}'
  ```
- **Ответ**: Информация о созданном счете
- **Описание**: Создает новый счет для оплаты.

#### Получение счета по ID
- **Метод**: GET 
- **URL**: `/api/invoice/{id}`
- **Параметры пути**: 
  - `id`: UUID идентификатор счета
- **Пример выполнения**:
  ```bash
  curl -X GET 'http://localhost:8088/api/invoice/550e8400-e29b-41d4-a716-446655440000' \
    -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...'
  ```
- **Ответ**: Информация о счете
- **Описание**: Возвращает информацию о счете по его ID.

## Новые функции

### Система уведомлений

В проект добавлены уведомления через Email и Telegram:

- Email-уведомления для победителей лотереи
- Telegram-уведомления о результатах тиражей
- Уведомления администраторов о критических ошибках

### Возврат средств

Пользователи могут запросить возврат средств за билеты, если тираж еще не начался:

```
POST /api/tickets/{ticketId}/refund
```

### Реферальная система

Добавлена реферальная система с возможностью получения бонусов за приглашение новых пользователей:

- Генерация реферальных кодов: `GET /api/users/me/referral-code`
- Регистрация по реферальному коду: `POST /api/referrals/register?code={code}&userId={userId}`
- Просмотр списка рефералов: `GET /api/users/me/referrals`

## Запуск с Docker

Для запуска проекта с помощью Docker:

```
docker-compose up -d
```

## API документация

Swagger UI доступен по адресу: http://localhost:8088/swagger-ui.html

## Лицензия

Этот проект распространяется под лицензией MIT - подробности см. в файле LICENSE.