# Домашка модуля 3 — MVC и контроллеры

> Статьи модуля: [оглавление](../README.md) · Примеры кода: [`src/main/java/...`](../src/main/java/com/javaroadmap/spring/s03/)

Тема — мини-API «заметки». Сервис ([`NotesService`](src/main/java/com/javaroadmap/spring/s03/homework/NotesService.java))
готов; твоя зона — **веб-слой**: контроллер, валидация, advice и WebSocket-хендлер.
Сделай «красные» тесты ([`Mod03HomeworkTest`](src/test/java/com/javaroadmap/spring/s03/homework/Mod03HomeworkTest.java),
[`Mod03HomeworkWsTest`](src/test/java/com/javaroadmap/spring/s03/homework/Mod03HomeworkWsTest.java)) зелёными.
**Файлы с тестами трогать не нужно.**

## Как работать

1. Открой заготовку задания, прочитай TODO и формулировку ниже.
2. Реализуй задания по порядку.
3. После каждого задания запускай проверку:
   ```bash
   ./gradlew :java-spring:03-mvc-controllers:homeworkTest
   ```
4. Цель — **зелёный** `homeworkTest`. Общий `./gradlew build` остаётся зелёным независимо от домашки.

## Задания (по порядку)

### 1. REST-контроллер: чтение (статьи 01–04)

`NotesController`

**Задача.** Преврати класс в REST-контроллер на `/api/notes`. Реализуй
`GET /api/notes` (+ необязательный query-параметр `contains` — фильтр по подстроке без учёта
регистра, сервис уже умеет) и `GET /api/notes/{id}`.

**Ожидаемый результат.** Список отдаётся как JSON-массив; фильтр сужает выборку;
заметка по id возвращается с полями `id` и `text`.

### 2. POST с валидацией (статья 04)

`NewNoteRequest`, `NotesController`

**Задача.** Добавь в `NewNoteRequest` validation-аннотации (`text` не пустой и ≤ 140 символов —
тексты сообщений в Javadoc заготовки). Реализуй `POST /api/notes`: тело валидируется, ответ —
**201 Created** с созданной заметкой.

**Ожидаемый результат.** Валидное тело → 201 и JSON заметки; пустой `text` → 400
(тело ошибки соберёт advice из задания 4).

### 3. DELETE (статья 04)

`NotesController`

**Задача.** Реализуй `DELETE /api/notes/{id}` с ответом **204 No Content**.

**Ожидаемый результат.** Удаление существующей заметки → 204; повторное → 404.

### 4. Обработка ошибок: @RestControllerAdvice (статья 04)

`NotesExceptionHandler`

**Задача.** Преврати класс в advice с двумя хендлерами:
`NoteNotFoundException` → **404** с телом `{"error": "<сообщение>"}`;
`MethodArgumentNotValidException` → **400** с телом `{"<поле>": "<сообщение>"}`.

**Ожидаемый результат.** `GET /api/notes/777` → 404 `{"error":"Заметка 777 не найдена"}`;
невалидный POST → 400 `{"text":"text не должен быть пустым"}`. Контроллер при этом
ничего не знает про HTTP-статусы ошибок.

### 5. WebSocket-хендлер (статья 06)

`UppercaseWebSocketHandler`

**Задача.** Переопредели нужный метод `TextWebSocketHandler` и на каждое сообщение ответь
тем же текстом в верхнем регистре. Регистрация на `/ws/upper` уже сделана (`HomeworkWsConfig`).

**Ожидаемый результат.** Клиент шлёт `hello spring` — получает `HELLO SPRING` по живому
ws-соединению (тест поднимает настоящий сервер на случайном порту).

## 🎓 Навыки после модуля

- Строишь **REST-API**: маппинги, path/query-параметры, статусы ответов (200/201/204/400/404).
- Ставишь **валидацию на границе** приложения и возвращаешь клиенту ошибки по полям.
- Выносишь перевод исключений в HTTP в **@RestControllerAdvice** — контроллеры остаются тонкими.
- Понимаешь путь запроса через **DispatcherServlet** (см. `handler()`-проверки в тестах примеров).
- Пишешь **WebSocket-хендлеры** и понимаешь, чем длинное соединение отличается от запрос-ответ.

## Готово, когда

- [ ] `./gradlew :java-spring:03-mvc-controllers:homeworkTest` зелёный (все задания).
- [ ] Решение закоммичено в свой репозиторий/ветку.

---

[📚 К модулю](../README.md) · [← Домашка 2](../../02-spring-data-jpa/homework/README.md) · [К треку Java Spring](../../README.md)
