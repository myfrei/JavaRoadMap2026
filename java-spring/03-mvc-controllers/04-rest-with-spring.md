# Лекция: REST и Spring

> Java Spring · Модуль 3 — MVC и контроллеры · [⬅ К содержанию трека](../README.md)

## Введение

Представьте библиотеку с миллионом книг, но без системы расстановки: книги лежат как попало, и каждый
посетитель должен спрашивать у разных сотрудников, где что. Кошмар. А теперь — библиотека с единым
каталогом: у каждой книги адрес (полка, ряд), действия стандартны (взять, вернуть, продлить). Зайдя
впервые, вы сразу понимаете правила.

**REST** — это и есть такая система для веб-API. Он задаёт соглашения: данные — это **ресурсы** с
адресами (URL), а действия над ними — стандартные **HTTP-методы**. Следуя REST, вы делаете API
предсказуемым: клиент понимает его почти без документации. Давайте разберём принципы и соберём
рабочий CRUD-контроллер на Spring.

## Принципы REST

REST (Representational State Transfer) стоит на нескольких столпах. Разберём ключевые.

1. **Ресурсы** — всё есть ресурс с уникальным URL: `/users/42`, `/orders/7`. Ресурсы — это
   существительные (объекты), а не действия.
2. **HTTP-методы** задают операцию: `GET` — прочитать, `POST` — создать, `PUT`/`PATCH` — изменить,
   `DELETE` — удалить. Глагол — в методе, не в URL.
3. **Статус-коды** сообщают результат: `2xx` успех, `4xx` ошибка клиента, `5xx` ошибка сервера.
4. **Stateless** — сервер не хранит сессию между запросами; каждый запрос самодостаточен (несёт токен,
   контекст). Это позволяет масштабировать сервер горизонтально.
5. **Идемпотентность** — повтор `GET`/`PUT`/`DELETE` даёт тот же эффект, что и один вызов; `POST` —
   нет. Это критично для ретраев: повторить `PUT` безопасно, повторить `POST` — рискуешь дублем.

Сведём методы в таблицу — это контракт, который должен знать каждый бэкендер.

| HTTP-метод | Операция (CRUD) | Пример | Идемпотентен | Типичный успех |
|------------|-----------------|--------|--------------|----------------|
| `GET` | Read | `GET /users/42` | да | `200 OK` |
| `POST` | Create | `POST /users` | нет | `201 Created` |
| `PUT` | Update (целиком) | `PUT /users/42` | да | `200 OK` |
| `PATCH` | Update (частично) | `PATCH /users/42` | нет | `200 OK` |
| `DELETE` | Delete | `DELETE /users/42` | да | `204 No Content` |

## Проектирование URL

Хороший URL читается как адрес в каталоге. Несколько правил, отличающих профессиональный API от
любительского.

```
✅ ХОРОШО                          ❌ ПЛОХО
GET    /api/v1/users               GET  /api/getAllUsers      ← глагол в пути
GET    /api/v1/users/42            GET  /api/user?id=42       ← действие вместо ресурса
POST   /api/v1/users               POST /api/createUser       ← дублирует смысл метода
GET    /api/v1/users/42/orders     GET  /api/getUserOrders    ← не отражает иерархию
DELETE /api/v1/users/42            POST /api/deleteUser?id=42  ← неверный метод
```

Правила в двух словах:

- ✅ **Существительные во множественном числе**: `/users`, не `/getUser`.
- ✅ **Иерархия через вложенность**: заказы пользователя — `/users/42/orders`.
- ✅ **Версия в пути**: `/api/v1/...` — чтобы развивать API, не ломая старых клиентов.
- ❌ Никаких глаголов (`/create`, `/delete`) — действие выражает HTTP-метод.

## CRUD-контроллер на Spring

Теперь соберём всё в полноценный REST-контроллер. Это эталон, на который можно опираться в проектах.

```java
@RestController                                // ← объекты сериализуются в JSON
@RequestMapping("/api/v1/users")               // ← общий префикс с версией
public class UserController {

    private final UserService service;

    public UserController(UserService service) {   // конструкторная инъекция
        this.service = service;
    }

    @GetMapping                                 // GET /api/v1/users?city=NYC
    public List<UserDto> list(@RequestParam(required = false) String city) {
        return service.findByCity(city);        // ← фильтр через query-параметр
    }

    @GetMapping("/{id}")                         // GET /api/v1/users/42
    public UserDto get(@PathVariable long id) {
        return service.findById(id);            // отсутствие → 404 через @ControllerAdvice
    }

    @PostMapping                                 // POST /api/v1/users
    public ResponseEntity<UserDto> create(@Valid @RequestBody CreateUserDto dto) {
        UserDto created = service.create(dto);
        URI location = URI.create("/api/v1/users/" + created.id());  // ← где лежит ресурс
        return ResponseEntity.created(location).body(created);       // ← 201 + заголовок Location
    }

    @PutMapping("/{id}")                         // PUT /api/v1/users/42
    public UserDto replace(@PathVariable long id, @Valid @RequestBody UpdateUserDto dto) {
        return service.replace(id, dto);        // целиком заменяем ресурс
    }

    @DeleteMapping("/{id}")                      // DELETE /api/v1/users/42
    public ResponseEntity<Void> delete(@PathVariable long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();   // ← 204 No Content, тела нет
    }
}
```

Разбор ключевых моментов:

- `@RequestBody` — тело запроса (JSON) превращается в объект через Jackson (`HttpMessageConverter` из лекции про `DispatcherServlet`).
- `ResponseEntity` даёт полный контроль над статусом и заголовками; `ResponseEntity.created(...)` ставит `201` и `Location`.
- `@PathVariable` — часть пути (`/{id}`), `@RequestParam` — query (`?city=`).
- На `create` отдаём `201 Created` и заголовок `Location` — это требование REST, многие забывают.

## Валидация входных данных

Нельзя доверять тому, что прислал клиент. `@Valid` запускает проверку DTO по аннотациям Bean
Validation (`jakarta.validation.*`) ещё до тела метода. Невалидный запрос → `400`, обработанный
вашим `@ControllerAdvice`.

```java
public record CreateUserDto(
        @NotBlank String name,                  // ← не пустое и не из пробелов
        @Email String email,                    // ← формат email
        @Min(0) @Max(150) int age               // ← диапазон
) {}
```

- ✅ Проверяйте данные на границе (в контроллере), а не размазывайте по сервисам.
- ✅ Ошибки валидации ловите централизованно (`MethodArgumentNotValidException` → `400`), как в лекции про виды контроллеров.
- ⚠️ `@Valid` без `@ControllerAdvice` всё равно вернёт `400`, но с сырым телом — добавьте обработчик для красивого ответа.

## Content negotiation и HATEOAS

Два штриха, которые отличают зрелый REST.

**Content negotiation** — клиент в заголовке `Accept` говорит, в каком формате хочет ответ
(`application/json`, реже XML), а в `Content-Type` — в каком формате прислал тело. Spring сам
выбирает нужный `HttpMessageConverter`. Обычно это работает «из коробки» для JSON, но знать механизм
полезно при поддержке нескольких форматов.

```bash
# клиент просит JSON ← заголовок Accept управляет форматом ответа
curl -H "Accept: application/json" http://localhost:8080/api/v1/users/42
```

**HATEOAS** (Hypermedia as the Engine of Application State) — высший «уровень зрелости» REST: ответ
несёт не только данные, но и ссылки на возможные действия. Клиент «ходит» по API как по сайту,
переходя по ссылкам.

```json
{
  "id": 42,
  "name": "Anna",
  "_links": {
    "self":   { "href": "/api/v1/users/42" },
    "orders": { "href": "/api/v1/users/42/orders" }
  }
}
```

На практике полный HATEOAS встречается редко (есть `spring-hateoas`), но идея «давать клиенту ссылки»
полезна и в облегчённом виде.

## Заключение

**Что изучено:**
- Принципы REST: ресурсы-существительные, HTTP-методы как операции, статус-коды, stateless, идемпотентность.
- Проектирование URL: множественное число, иерархия, версия в пути, никаких глаголов.
- CRUD-контроллер на `@RestController`: `@RequestBody`, `ResponseEntity`, статусы `201`/`204`, `Location`.
- Валидация через `@Valid` и `jakarta.validation`; content negotiation и идея HATEOAS.

**Как применять на практике:**
- Проектируйте URL вокруг ресурсов; операцию выражайте методом, а не глаголом в пути.
- Возвращайте корректные статусы (`201` + `Location` на create, `204` на delete), а не «200 на всё».
- Валидируйте вход через `@Valid` на границе и переводите ошибки в `400` централизованно.

**Что дальше:** REST — не единственный способ общения сервисов. Разберём gRPC — бинарный, быстрый и
контракт-first протокол поверх HTTP/2.

[⬅ Виды контроллеров в Spring](03-controller-types.md) · [📑 Оглавление модуля](README.md) · [gRPC и Spring ➡](05-grpc-with-spring.md)
