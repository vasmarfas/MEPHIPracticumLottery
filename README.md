# Система лотереи MEPHI Practicum

Система для проведения лотерейных тиражей с возможностью покупки билетов, определения выигрышей и интеграции с платежными системами.

## Возможности

- Аутентификация пользователей с помощью JWT
- Разграничение прав доступа (USER, ADMIN)
- Управление лотерейными тиражами (создание, активация, завершение, отмена)
- Покупка и валидация билетов (с выбором чисел пользователем)
- Обработка платежей и интеграция с платежной системой
- Определение результатов тиражей и начисление выигрышей
- Экспорт данных и статистики в CSV/JSON
- История и отчетность для пользователей и администраторов

## Технологический стек

- Java 17
- Spring Boot 3.4
- PostgreSQL 17
- Spring Security с JWT
- Spring Data JPA
- Flyway для миграций базы данных
- Docker и Docker Compose для контейнеризации

## Основные компоненты системы

### Тиражная служба (Draw Service)

- Управление лотерейными тиражами и их статусами
- Автоматическое определение результатов
- Поддержка разных типов лотерей ("5 из 36", "6 из 45")

### Служба билетов (Ticket Service)

- Создание и валидация билетов
- Проверка статуса билетов
- История билетов пользователя

### Шлюз оплаты (Payment Service)

- Обработка платежей за билеты
- Интеграция с платежной системой
- Поддержка двухэтапной (инвойс → оплата) и одноэтапной оплаты

### Служба результатов (Draw Result Service)

- Генерация выигрышных комбинаций
- Определение выигрышных билетов
- Уведомления о результатах

### Служба экспорта (Export Service)

- Формирование отчетов по тиражам
- Экспорт статистики в CSV/JSON
- Отчетность для пользователей и администраторов

## Начало работы

### Требования

- Java 17 JDK
- Docker и Docker Compose
- Gradle 8.5+

### Запуск приложения

1. Клонируйте репозиторий:
```bash
git clone https://github.com/yourusername/MEPHIPracticumLottery.git
cd MEPHIPracticumLottery
```

2. Запустите приложение с Docker Compose:
```bash
docker-compose up -d
```

Приложение будет доступно по адресу http://127.0.0.1:8088

### API документация

Swagger UI доступен по адресу http://127.0.0.1:8088/swagger-ui.html

## Основные API эндпоинты

### Аутентификация

- **POST /api/auth/register** - Регистрация нового пользователя
- **POST /api/auth/login** - Вход в систему, получение JWT токена

### Тиражи

- **GET /api/draws** - Получение списка всех тиражей
- **GET /api/draws/active** - Получение списка активных тиражей
- **GET /api/draws/{id}** - Получение информации о тираже
- **POST /api/admin/draws** - Создание нового тиража (требуется роль ADMIN)
- **PUT /api/admin/draws/{id}/cancel** - Отмена тиража (требуется роль ADMIN)

### Билеты

- **POST /api/tickets/purchase** - Покупка билета в один шаг (выбор тиража, чисел и оплата)
- **GET /api/tickets** - Получение списка билетов пользователя
- **GET /api/tickets/{id}** - Получение информации о билете
- **GET /api/tickets/{id}/check-result** - Проверка результата билета

### Оплата (2-х этапный процесс)

- **POST /api/invoice** - Создание инвойса для билета
- **POST /api/payments** - Оплата инвойса

### Результаты

- **GET /api/draws/{id}/results** - Получение результатов тиража
- **POST /api/admin/draws/{id}/generate-result** - Генерация результатов тиража (требуется роль ADMIN)

### Экспорт

- **GET /api/draws/{id}/export/csv** - Экспорт результатов тиража в CSV
- **GET /api/draws/{id}/export/json** - Экспорт результатов тиража в JSON
- **GET /api/users/{userId}/history/csv** - Экспорт истории пользователя в CSV
- **GET /api/users/{userId}/history/json** - Экспорт истории пользователя в JSON

## Примеры API запросов

### Аутентификация

#### Регистрация нового пользователя
```
POST /api/auth/register
Content-Type: application/json

{
  "username": "user@example.com",
  "password": "password123"
}
```

Ответ (200 OK):
```json
{
  "message": "Пользователь успешно зарегистрирован"
}
```

#### Авторизация и получение токена
```
POST /api/auth/login
Content-Type: application/json

{
  "username": "user@example.com",
  "password": "password123"
}
```

Ответ (200 OK):
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "user@example.com"
}
```

### Тиражи

#### Создание нового тиража (ADMIN)
```
POST /api/admin/draws
Content-Type: application/json
Authorization: Bearer {token}

{
  "lotteryType": "FIVE_OUT_OF_36",
  "startTime": "2024-06-01T12:00:00",
  "endTime": "2024-06-01T18:00:00",
  "ticketPrice": 100.00
}
```

Ответ (201 Created):
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "lotteryType": "FIVE_OUT_OF_36",
  "startTime": "2024-06-01T12:00:00",
  "endTime": "2024-06-01T18:00:00",
  "status": "ACTIVE",
  "ticketPrice": 100.00
}
```

#### Получение активных тиражей
```
GET /api/draws/active
Authorization: Bearer {token}
```

Ответ (200 OK):
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "lotteryType": "FIVE_OUT_OF_36",
    "startTime": "2024-06-01T12:00:00",
    "endTime": "2024-06-01T18:00:00",
    "status": "ACTIVE",
    "ticketPrice": 100.00
  }
]
```

### Покупка билетов

#### Способ 1: Покупка билета в один шаг
```
POST /api/tickets/purchase
Content-Type: application/json
Authorization: Bearer {token}

{
  "drawId": "550e8400-e29b-41d4-a716-446655440000",
  "numbers": "5,12,18,24,36",
  "cardNumber": "4242424242424242",
  "cvc": "123"
}
```

Ответ (201 Created):
```json
{
  "id": "661f9511-f38c-52e5-b817-557766551111",
  "userId": "772fa622-g49d-63f6-c928-668877662222",
  "drawId": "550e8400-e29b-41d4-a716-446655440000",
  "numbers": "5,12,18,24,36",
  "status": "PENDING",
  "createdAt": "2024-05-20T10:15:30"
}
```

#### Способ 2: Двухэтапная покупка

##### Шаг 1: Создание билета
```
POST /api/tickets
Content-Type: application/json
Authorization: Bearer {token}

{
  "drawId": "550e8400-e29b-41d4-a716-446655440000",
  "numbers": "7,14,21,28,35"
}
```

Ответ (201 Created):
```json
{
  "id": "661f9511-f38c-52e5-b817-557766551112",
  "userId": "772fa622-g49d-63f6-c928-668877662222",
  "drawId": "550e8400-e29b-41d4-a716-446655440000",
  "numbers": "7,14,21,28,35",
  "status": "CREATED",
  "createdAt": "2024-05-20T10:16:30"
}
```

##### Шаг 2: Создание инвойса
```
POST /api/invoice
Content-Type: application/json
Authorization: Bearer {token}

{
  "ticketData": "{\"drawId\":\"550e8400-e29b-41d4-a716-446655440000\",\"numbers\":\"7,14,21,28,35\"}",
  "amount": 100.0
}
```

Ответ (201 Created):
```json
{
  "id": "883g0622-h50e-63f6-c928-779988773333",
  "ticketData": "{\"drawId\":\"550e8400-e29b-41d4-a716-446655440000\",\"numbers\":\"7,14,21,28,35\"}",
  "registerTime": "2024-05-20T10:17:30",
  "status": "PENDING",
  "amount": 100.0
}
```

##### Шаг 3: Оплата инвойса
```
POST /api/payments
Content-Type: application/json
Authorization: Bearer {token}

{
  "invoiceId": "883g0622-h50e-63f6-c928-779988773333",
  "cardNumber": "4242424242424242",
  "cvc": "123"
}
```

Ответ (201 Created):
```json
{
  "id": "994h1733-i61f-74g7-d039-880099884444",
  "invoiceId": "883g0622-h50e-63f6-c928-779988773333",
  "amount": 100.0,
  "status": "SUCCESS",
  "paymentTime": "2024-05-20T10:18:30"
}
```

### Проверка билета

```
GET /api/tickets/661f9511-f38c-52e5-b817-557766551111/check-result
Authorization: Bearer {token}
```

Ответ (200 OK):
```json
{
  "ticketId": "661f9511-f38c-52e5-b817-557766551111",
  "drawId": "550e8400-e29b-41d4-a716-446655440000",
  "ticketNumbers": "5,12,18,24,36",
  "winningNumbers": "5,11,18,24,35",
  "matchedCount": 4,
  "prizeAmount": 1000.0,
  "status": "WON"
}
```

### Экспорт данных

```
GET /api/users/772fa622-g49d-63f6-c928-668877662222/history/json
Authorization: Bearer {token}
```

Ответ (200 OK) - файл JSON с историей билетов пользователя.

## Безопасность

- Все API эндпоинты, кроме аутентификации, требуют JWT токен
- Токены действительны в течение 1 часа
- Данные платежных карт не хранятся в системе

## Логирование

- JSON-форматированные логи
- Логи хранятся в директории `logs/`
- Ежедневная ротация логов

## Тестирование

В репозитории доступна коллекция Postman для тестирования API.

## Лицензия

MIT