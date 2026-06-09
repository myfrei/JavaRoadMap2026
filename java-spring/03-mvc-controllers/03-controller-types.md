# Лекция: Виды контроллеров в Spring

> Java Spring · Модуль 3 — MVC и контроллеры · [⬅ К содержанию трека](../README.md)

## Введение

В прошлой лекции мы видели развилку: один и тот же запрос можно отдать как HTML-страницу или как JSON.
За эти сценарии отвечают **разные виды контроллеров**. Представьте набор отвёрток: крестовая, плоская,
звёздочка. Все «крутят винты», но под разные шлицы — и если взять не ту, дело не пойдёт.

Так и тут: `@Controller` для страниц, `@RestController` для API, функциональные эндпоинты для лёгких
сервисов, `@ControllerAdvice` для ошибок. Давайте разберём, какой инструмент под какую задачу, чтобы
вы выбирали осознанно, а не копировали аннотации наугад.

## @Controller: страницы с серверным рендерингом

`@Controller` — классика MVC. Метод возвращает **имя view**, Spring находит шаблон (Thymeleaf/JSP) и
рендерит HTML на сервере. Браузер получает готовую страницу.

```java
@Controller                                   // ← вернёт HTML
public class DashboardController {

    private final ReportService reports;

    public DashboardController(ReportService reports) {   // конструкторная инъекция
        this.reports = reports;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("stats", reports.daily());     // данные во view
        return "dashboard";                   // ← имя шаблона: dashboard.html
    }
}
```

Когда брать: серверный рендеринг страниц, классические web-приложения, формы, админки. Если у вас
SPA на React/Vue или мобильное приложение — этот вид не нужен, им подавай JSON.

## @RestController: API, отдающее данные

`@RestController` — это `@Controller` + `@ResponseBody` на уровне класса. Метод возвращает **объект**,
а Spring через Jackson сериализует его в JSON (или XML). Никаких view — только данные.

```java
@RestController                               // ← @Controller + @ResponseBody
@RequestMapping("/api/users")
public class UserApi {

    private final UserService service;

    public UserApi(UserService service) { this.service = service; }

    @GetMapping("/{id}")
    public User get(@PathVariable long id) {
        return service.findById(id);          // ← объект → JSON, view не ищется
    }
}
```

Это рабочая лошадка современного бэкенда. Глубоко REST разберём в следующей лекции — пока запомните:
`@RestController` = «верни данные», `@Controller` = «верни страницу».

## Функциональные эндпоинты: роутинг без аннотаций

Кроме аннотаций, Spring умеет описывать маршруты **функционально** — через `RouterFunction`. Вместо
методов с `@GetMapping` вы строите таблицу маршрутов как обычный Java-объект. Это краеугольный стиль
в WebFlux (реактивный стек), но доступен и в обычном MVC.

```java
@Configuration
public class UserRoutes {

    @Bean
    public RouterFunction<ServerResponse> routes(UserHandler handler) {
        return RouterFunctions.route()
                .GET("/fn/users/{id}", handler::getById)   // ← маршрут → метод-обработчик
                .POST("/fn/users", handler::create)
                .build();
    }
}
```

- ✅ маршруты собраны в одном месте, их легко компоновать и тестировать как обычный код;
- ✅ органично ложится на реактивный стиль (вернёмся к этому в модуле про WebFlux);
- ⚠️ для большинства команд аннотации привычнее и читаемее — функциональный стиль берут осознанно.

## Глобальная обработка ошибок: @ControllerAdvice

Что будет, если сервис кинет исключение? По умолчанию клиент получит унылый `500` со стектрейсом.
Это и неинформативно, и небезопасно. Решение — **централизованная** обработка ошибок через
`@ControllerAdvice`: один класс ловит исключения **со всех контроллеров** и превращает их в
аккуратные ответы.

Сначала пометим доменное исключение статусом через `@ResponseStatus`:

```java
@ResponseStatus(HttpStatus.NOT_FOUND)         // ← брошено → автоматически 404
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(long id) {
        super("User not found: " + id);
    }
}
```

Теперь общий «менеджер по жалобам» для всего приложения:

```java
@RestControllerAdvice                          // ← @ControllerAdvice + @ResponseBody (ответ = JSON)
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)        // ← ловим конкретный тип
    public ProblemDetail handleNotFound(UserNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);  // RFC 7807
        pd.setDetail(ex.getMessage());          // ← единый формат ошибки для всех API
        return pd;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)   // ошибки валидации
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setDetail("Validation failed: " + ex.getFieldErrors().size() + " errors");
        return pd;
    }
}
```

Разбор:

- `@ControllerAdvice` действует **глобально** — не нужно ловить исключения в каждом контроллере.
- `@ExceptionHandler` связывает тип исключения с методом-обработчиком.
- `@ResponseStatus` на исключении задаёт HTTP-код «по умолчанию», когда отдельный обработчик не нужен.
- `ProblemDetail` (Spring 6 / RFC 7807) — стандартный формат тела ошибки; не изобретайте свой.

❌ Не глотайте исключения в контроллере `try/catch` и не возвращайте `200` с текстом ошибки — это
ломает контракт API. ✅ Бросьте доменное исключение, а `@ControllerAdvice` переведёт его в корректный
статус.

## Сравнение видов и когда что брать

Сведём всё в одну таблицу — это шпаргалка по выбору инструмента.

| Вид | Аннотация | Возвращает | Когда применять |
|-----|-----------|------------|-----------------|
| MVC-контроллер | `@Controller` | имя view (HTML) | серверный рендеринг, формы, админки |
| REST-контроллер | `@RestController` | объект → JSON | API для SPA, мобильных, интеграций |
| Функциональный | `RouterFunction` | `ServerResponse` | реактивный стиль, лёгкие сервисы |
| Обработчик ошибок | `@ControllerAdvice` | ответ при исключении | централизованная обработка ошибок |

Как принять решение:

1. Отдаёте HTML-страницы → `@Controller`.
2. Отдаёте данные клиенту-программе → `@RestController` (90% бэкенд-задач).
3. Идёте в реактивный стек или любите функциональный стиль → `RouterFunction`.
4. В любом случае — заведите `@ControllerAdvice`, чтобы ошибки были единообразными.

Отдельно вспомним `HandlerInterceptor` из прошлой лекции: это **не** вид контроллера, а перехватчик
вокруг любого из них (auth, логи, метрики). Контроллер решает «что вернуть», интерсептор — «что
сделать до/после» для всех сразу.

## Заключение

**Что изучено:**
- `@Controller` рендерит HTML-страницы; `@RestController` отдаёт JSON (= `@Controller` + `@ResponseBody`).
- Функциональные эндпоинты (`RouterFunction`) — альтернативный, безаннотационный способ описать маршруты.
- `@ControllerAdvice` + `@ExceptionHandler` + `@ResponseStatus` дают единую обработку ошибок; `ProblemDetail` — её стандартный формат.
- `HandlerInterceptor` — это обвязка вокруг контроллеров, а не их вид.

**Как применять на практике:**
- Выбирайте вид по тому, **что** возвращаете: страницу или данные. Не смешивайте в одном классе.
- Сразу заводите глобальный `@RestControllerAdvice` — без него API отдаёт сырые `500` со стектрейсом.
- Связывайте доменные исключения с HTTP-кодами через `@ResponseStatus`, а нестандартные случаи — через `@ExceptionHandler`.

**Что дальше:** погрузимся в REST — принципы, проектирование URL, статусы, валидацию и полноценный
CRUD-контроллер на Spring.

[⬅ DispatcherServlet: путь HTTP-запроса](02-dispatcher-servlet.md) · [📑 Оглавление модуля](README.md) · [REST и Spring ➡](04-rest-with-spring.md)
