**1. Заведение заявки в сервисе:**

Запрос: POST http://localhost:7777/api/v1/opportunity/register (в теле запроса передается json с параметрами: opportunity - Id заявки, data - массив параметров заявки в виде ключ : значение)

Пример запроса:
```
{
  "opportunity": "1-5YE2LKW",
  "data": {
    "name": "2-10012345",
    "fio": "Тестов Тест Тестович",
    "status": "Проверка заявки",
    "loanamount": "1 000 000,00",
    "type": "Ипотека"
  }
}
```

**2. Получить заявку в работу для пользователя из доступной очереди:**

Запрос: GET http://localhost:7777/api/v1/opportunity?employeeId= (employeeId - Id пользователя)

**3. Изменение статуса заявки:**

Запрос: POST http://localhost:7777/api/v1/opportunity/release (в теле запроса передается json с параметрами: opportunity - Id заявки, delete - флаг необходимости удаления заявки из сервиса, status - статус заявки)

Пример запроса:
```
{
  "opportunity": "1-5YE2LKW",
  "delete": false,
  "status": "Верификация"
}
```

**4. Заблокировать заявку за пользователем:**

Запрос: POST http://localhost:7777/api/v1/opportunity/lock (в теле запроса передается json с параметрами: opportunity - Id заявки, employee - Id пользователя)

Пример запроса:
```
{
  "opportunity": "1-5YE2LKW",
  "employee": "1-2CFCKVQ"
}
```
