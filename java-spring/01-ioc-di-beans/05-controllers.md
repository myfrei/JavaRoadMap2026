# Лекция: Контроллеры — @Controller и @RestController

> Java Spring · Модуль 1 — IoC, DI и бины · [⬅ К содержанию трека](../README.md)

## Введение

Представьте администратора на ресепшене. К нему приходят посетители с разными запросами: «хочу заселиться»,
«где конференц-зал», «дайте счёт». Администратор не убирает номера и не готовит еду — он *принимает*
обращение, понимает, что человеку нужно, передаёт задачу нужной службе и возвращает ответ. Он — точка
входа между внешним миром и внутренней кухней отеля.

В веб-приложении эту роль играет **контроллер (controller)**: он принимает HTTP-запрос, достаёт из него
данные, зовёт сервис и возвращает ответ. Вы наверняка уже сталкивались с двумя его разновидностями —
`@Controller` и `@RestController` — и не всегда понятно, чем они отличаются. Давайте разберём роль
контроллера и эту пару аннотаций раз и навсегда.

## Роль контроллера

Контроллер — это веб-слой, тонкая прослойка между HTTP и вашей бизнес-логикой. Его задачи:

- принять запрос по определённому URL и методу (GET/POST/…);
- извлечь данные: путь, параметры, тело;
- вызвать соответствующий метод сервиса;
- сформировать HTTP-ответ (тело + статус).

⚠️ Чего контроллер делать **не должен** — содержать бизнес-логику (см. анти-паттерн «жирный контроллер» в
предыдущей лекции). Его дело — маршрутизация и преобразование данных, а не правила предметной области.

## @Controller vs @RestController

Это два стереотипа веб-слоя, и разница между ними — в том, *что возвращает* метод.

**`@Controller`** — классический MVC. Метод возвращает **имя view** (шаблона), который Spring отрендерит в
HTML. Используется для серверного рендеринга страниц (Thymeleaf, JSP).

```java
@Controller   // ← возвращаемое значение трактуется как ИМЯ ШАБЛОНА
public class PageController {

    @GetMapping("/profile")
    public String profile(Model model) {
        model.addAttribute("name", "Анна");   // ← кладём данные для шаблона
        return "profile";   // ← имя view → отрендерит templates/profile.html
    }
}
```

**`@RestController`** — для REST API. Метод возвращает **тело ответа** (объект сериализуется в JSON).
Это `@Controller` + `@ResponseBody` в одной аннотации.

```java
@RestController   // ← = @Controller + @ResponseBody: возвращаемое значение идёт ПРЯМО в тело ответа
public class UserApiController {

    @GetMapping("/api/users/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.findById(id);   // ← объект User сериализуется в JSON автоматически
    }
}
```

| Критерий | `@Controller` | `@RestController` |
|---|---|---|
| Что возвращает метод | имя view (шаблона) | тело ответа (объект → JSON) |
| Эквивалент | — | `@Controller` + `@ResponseBody` |
| Типичное применение | серверный рендеринг HTML | REST API / SPA-бэкенд |
| Нужен ли шаблонизатор | ✅ да (Thymeleaf/JSP) | ❌ нет |
| Формат ответа | HTML-страница | JSON (по умолчанию) |

Простое правило: строите **API** — берите `@RestController`. Рендерите **HTML-страницы** на сервере —
`@Controller`. В современных бэкендах под фронтенд-фреймворки чаще нужен именно `@RestController`.

## Маппинг запросов: @RequestMapping и его сокращения

Чтобы связать метод с URL и HTTP-методом, используют аннотации маппинга. `@RequestMapping` — общая, а
`@GetMapping`/`@PostMapping`/… — её удобные сокращения под конкретный метод.

```java
@RestController
@RequestMapping("/api/orders")   // ← общий префикс пути для всех методов класса
public class OrderApiController {

    @GetMapping              // ← GET /api/orders        — список
    public List<Order> all() { /* ... */ }

    @GetMapping("/{id}")     // ← GET /api/orders/{id}    — один элемент
    public Order one(@PathVariable Long id) { /* ... */ }

    @PostMapping             // ← POST /api/orders        — создать
    public Order create(@RequestBody OrderRequest req) { /* ... */ }
}
```

Есть пары для каждого глагола: `@GetMapping`, `@PostMapping`, `@PutMapping`, `@PatchMapping`,
`@DeleteMapping`. Использовать их предпочтительнее, чем громоздкий `@RequestMapping(method = ...)` — короче
и читаемее.

## Извлечение данных из запроса

Данные приходят тремя путями, и для каждого своя аннотация.

```java
@RestController
@RequestMapping("/api/products")
public class ProductApiController {
    private final ProductService productService;

    public ProductApiController(ProductService productService) {
        this.productService = productService;
    }

    // /api/products/42 — часть ПУТИ
    @GetMapping("/{id}")
    public Product byId(@PathVariable Long id) {   // ← @PathVariable: переменная из URL-шаблона
        return productService.findById(id);
    }

    // /api/products?category=books&limit=10 — параметры ЗАПРОСА
    @GetMapping
    public List<Product> search(
            @RequestParam String category,                          // ← обязательный query-параметр
            @RequestParam(defaultValue = "20") int limit) {         // ← необязательный, с дефолтом
        return productService.search(category, limit);
    }

    // тело POST-запроса (JSON) → объект
    @PostMapping
    public Product create(@RequestBody ProductRequest req) {   // ← @RequestBody: JSON десериализуется в объект
        return productService.create(req);
    }
}
```

Коротко:

- `@PathVariable` — значение из пути (`/users/{id}`);
- `@RequestParam` — параметр строки запроса (`?key=value`), поддерживает `defaultValue` и `required`;
- `@RequestBody` — тело запроса (обычно JSON) превращается в Java-объект.

## ResponseEntity и HTTP-статусы

Иногда вернуть объект мало — нужно управлять *статусом* ответа и заголовками. Для этого есть
`ResponseEntity<T>`: обёртка вокруг тела, статуса и заголовков.

```java
@RestController
@RequestMapping("/api/users")
public class UserApiController {
    private final UserService userService;

    public UserApiController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)                          // ← нашли → 200 OK + тело
                .orElse(ResponseEntity.notFound().build());      // ← нет → 404 Not Found без тела
    }

    @PostMapping
    public ResponseEntity<User> create(@RequestBody UserRequest req) {
        User saved = userService.create(req);
        return ResponseEntity
                .status(HttpStatus.CREATED)   // ← 201 CREATED — правильный статус для создания ресурса
                .body(saved);
    }
}
```

Почему это важно: правильные статусы — часть контракта API. `200` для успеха, `201` для создания, `404`
если ресурс не найден, `400` при невалидных данных. Клиенты (фронтенд, другие сервисы) полагаются на эти
коды. Если статус не критичен, можно возвращать объект напрямую — Spring отдаст `200 OK`; `ResponseEntity`
берите, когда нужен контроль над статусом или заголовками.

## Складываем вместе: REST-контроллер

Соберём аккуратный CRUD-контроллер, который делегирует всю работу сервису и грамотно расставляет статусы.

```java
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;   // ← зависимость через конструктор, final

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOne(@PathVariable Long id) {
        return orderService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());   // ← 404, если заказа нет
    }

    @PostMapping
    public ResponseEntity<Order> create(@RequestBody OrderRequest req) {
        Order created = orderService.placeOrder(req.customerId(), req.items());   // ← логика — в сервисе
        return ResponseEntity.status(HttpStatus.CREATED).body(created);           // ← 201 на создание
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.cancel(id);
        return ResponseEntity.noContent().build();   // ← 204 No Content — удалили, тела нет
    }
}
```

Контроллер остаётся тонким: ни одного бизнес-правила, только маршрутизация, извлечение данных и
формирование HTTP-ответа. Вся суть — в `OrderService`.

## Заключение

**Что изучено:**

- Контроллер — тонкий веб-слой: принимает запрос, извлекает данные, зовёт сервис, формирует ответ.
- `@Controller` возвращает имя view (HTML-рендеринг); `@RestController` = `@Controller` + `@ResponseBody` (тело → JSON).
- Маппинг — через `@GetMapping`/`@PostMapping`/… (сокращения `@RequestMapping`) и общий префикс на классе.
- Данные достаём через `@PathVariable` (путь), `@RequestParam` (query), `@RequestBody` (тело).
- `ResponseEntity` даёт контроль над HTTP-статусом и заголовками (200, 201, 404, 204).

**Как применять на практике:**

- Для REST API используйте `@RestController`; для серверных HTML-страниц — `@Controller`.
- Группируйте эндпоинты общим `@RequestMapping` на классе, методы помечайте `@GetMapping`/`@PostMapping`/….
- Возвращайте осмысленные статусы (`201` на создание, `404` если не найдено) через `ResponseEntity`.
- Не кладите логику в контроллер — делегируйте сервису, держите веб-слой тонким.

**Что дальше:** разберём scope бинов и тонкости выбора реализации — `@Primary`, `@Qualifier`, условные бины.

[⬅ Сервисный слой и аннотация @Service](04-service-layer.md) · [📑 Оглавление модуля](README.md) · [Scope бинов и выбор реализации ➡](06-bean-scopes-and-wiring.md)
