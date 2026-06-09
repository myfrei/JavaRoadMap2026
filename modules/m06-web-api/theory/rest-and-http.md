# REST и HTTP: контроллеры, методы, статусы

> Модуль 6 — Web и API · [к модулю](../../../course/06-web-api.md)

## Зачем это нужно

REST — соглашение о том, как строить HTTP-API: ресурсы, методы, коды статусов. Следуя ему, ты
делаешь API предсказуемым — клиенты понимают его без долгой документации, а инструменты (кэши,
прокси) работают правильно. Это язык общения сервисов.

**Где применяется в проектах:** публичные и внутренние API; мобильные бэкенды; интеграции между
микросервисами.

## Ресурсы и HTTP-методы

REST оперирует ресурсами (существительные), а действие задаёт метод:

| Метод | Смысл | Идемпотентность |
|-------|-------|-----------------|
| `GET /users/42` | получить | да (не меняет состояние) |
| `POST /users` | создать | нет |
| `PUT /users/42` | заменить целиком | да |
| `PATCH /users/42` | частично изменить | нет |
| `DELETE /users/42` | удалить | да |

Идемпотентность важна: повтор `PUT`/`DELETE` безопасен, а `POST` может создать дубликат — это влияет
на ретраи.

## Контроллер в Spring

```java
@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping("/{id}")                       // GET /users/42
    public User getById(@PathVariable long id) {
        return service.findById(id)
                .orElseThrow(() -> new NotFoundException(id));
    }

    @GetMapping                                 // GET /users?city=NYC
    public List<User> list(@RequestParam(required = false) String city) {
        return service.findByCity(city);
    }

    @PostMapping                                // POST /users
    public ResponseEntity<User> create(@RequestBody UserDto dto) {
        User created = service.create(dto);
        return ResponseEntity.status(201).body(created);   // 201 Created
    }
}
```

- `@PathVariable` — часть пути (`/{id}`); `@RequestParam` — query (`?city=`); `@RequestBody` — JSON-тело.
- `ResponseEntity` даёт контроль над кодом статуса и заголовками.

## Коды статусов — это контракт

- `2xx` — успех (`200 OK`, `201 Created`, `204 No Content`);
- `4xx` — ошибка клиента (`400 Bad Request`, `404 Not Found`, `409 Conflict`);
- `5xx` — ошибка сервера (`500`, `503`).

Возвращать `200` на ошибку — антипаттерн: клиент не отличит успех от провала.

## Версионирование и пагинация

API развивается — закладывай версию (`/v1/users`) и пагинацию для списков (`?page=&size=` или
keyset), чтобы не отдавать миллион строк за раз.

## Связь с кодом модуля

В [homework](../homework/src/main/java/com/javaroadmap/m06/homework/Mod06Homework.java) ты строишь
«кирпичики» веб-слоя без Spring: `matchRoute("/users/{id}", …)` — то, что делает роутер Spring;
`statusText` — коды статусов; `parseQuery`/`buildUrl` — query-строка.

## Итог

**Что изучено:**
- REST: ресурсы + HTTP-методы; идемпотентность и почему она важна для ретраев.
- Spring-контроллер: `@GetMapping`/`@PathVariable`/`@RequestParam`/`@RequestBody`, `ResponseEntity`.
- Коды статусов как контракт; версионирование и пагинация.

**Как применять на практике:**
- Проектировать URL вокруг ресурсов, действие выражать методом, а не глаголом в пути.
- Возвращать корректные коды статусов (не «200 на всё»).
- Сразу закладывать версию API и пагинацию списков.
