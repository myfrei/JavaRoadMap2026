# Лекция: DispatcherServlet: путь HTTP-запроса

> Java Spring · Модуль 3 — MVC и контроллеры · [⬅ К содержанию трека](../README.md)

## Введение

В прошлой лекции официант (контроллер) принимал заказ. Но представьте большой ресторан: десятки
столиков, и было бы хаосом, если бы каждый гость сам искал «своего» официанта. Поэтому на входе стоит
**хостес** — один человек, через которого проходят все гости. Он встречает, смотрит бронь и
направляет к нужному столику.

В Spring MVC роль хостес играет `DispatcherServlet` — единая входная дверь для всех HTTP-запросов.
Вы наверняка писали `@GetMapping` и видели, что «оно как-то работает». Давайте разберём, что
происходит между сетевым запросом и вашим методом контроллера — это снимает 90% магии Spring.

## Паттерн Front Controller

`DispatcherServlet` реализует классический паттерн **Front Controller**: вместо того чтобы вешать
отдельный сервлет на каждый URL, мы ставим **один** сервлет на все пути (`/*`) и поручаем ему
диспетчеризацию.

```
Без Front Controller            С Front Controller (Spring)
/users   → UserServlet          /*  → DispatcherServlet ──┬─→ UserController
/orders  → OrderServlet                                   ├─→ OrderController
/products→ ProductServlet                                 └─→ ProductController
   ❌ дублирование                    ✅ единая точка входа
```

Зачем один вход? Чтобы централизовать общие задачи: логирование, безопасность, разбор параметров,
обработку ошибок. Их пишут один раз в инфраструктуре, а не копируют в каждый сервлет. В Spring Boot
`DispatcherServlet` регистрируется автоматически — вы его не видите, но он есть.

## Сердце Spring MVC

`DispatcherServlet` сам не содержит вашей логики — он **оркестратор**. Его задача: получив запрос,
найти, кто его обработает, передать управление, получить результат и превратить его в HTTP-ответ. Для
этого он опирается на несколько помощников:

| Компонент | Роль | Аналогия (хостес) |
|-----------|------|-------------------|
| `HandlerMapping` | по URL находит метод-обработчик | «какой столик у этой брони?» |
| `HandlerAdapter` | умеет вызвать найденный обработчик | официант, обслуживающий столик |
| `HttpMessageConverter` | тело ↔ объект (JSON для REST) | переводчик меню |
| `ViewResolver` | по имени view находит шаблон | подбор тарелки для блюда |
| `HandlerExceptionResolver` | превращает исключение в ответ | менеджер при жалобе |

Все они — бины в контексте. Вы можете их подменять и настраивать, но в 99% случаев хватает
умолчаний Spring Boot.

## Полный путь запроса

Теперь главное — соберём поток целиком. Проследим, что происходит с запросом `GET /users/42` от
момента, когда он коснулся `DispatcherServlet`, до ответа клиенту.

```
                         HTTP: GET /users/42
                                 │
                                 ▼
                    ┌─────────────────────────┐
                    │    DispatcherServlet     │  ← единая дверь (Front Controller)
                    └────────────┬────────────┘
            1. кто обработает?   │
                                 ▼
                    ┌─────────────────────────┐
                    │      HandlerMapping      │  → нашёл UserController#getById
                    └────────────┬────────────┘
            2. вызови обработчик │
                                 ▼
                    ┌─────────────────────────┐
                    │      HandlerAdapter      │  → связывает аргументы и зовёт метод
                    └────────────┬────────────┘
                                 ▼
                    ┌─────────────────────────┐
                    │     ВАШ контроллер       │  → return user / return "view"
                    └────────────┬────────────┘
                                 │
            3. что вернул метод? ▼
              ┌──────────────────┴──────────────────┐
              │                                      │
       объект (REST)                          имя view (MVC)
              │                                      │
              ▼                                      ▼
    ┌───────────────────┐                ┌───────────────────┐
    │ HttpMessageConverter│              │    ViewResolver    │
    │  (Jackson → JSON)  │               │  → users.html      │
    └─────────┬─────────┘                └─────────┬─────────┘
              │                                     │ render
              └──────────────────┬──────────────────┘
                                 ▼
                         HTTP-ответ клиенту
```

Здесь развилка на шаге 3 — самая важная мысль всей лекции. От **типа возвращаемого значения** и
аннотаций зависит, какой ветвью пойдёт обработка:

- Метод вернул `String` (имя view) → работает `ViewResolver`, рендерится HTML-шаблон.
- Метод вернул объект, а класс помечен `@RestController` (или метод — `@ResponseBody`) → работает
  `HttpMessageConverter`, объект сериализуется в JSON.

Сравним обе ветви на коде.

```java
@Controller                                   // ← ветвь VIEW
public class PageController {
    @GetMapping("/users/{id}")
    public String page(@PathVariable long id, Model model) {
        model.addAttribute("user", service.findById(id));
        return "users";                       // ← имя шаблона → ViewResolver → users.html
    }
}

@RestController                               // ← ветвь REST (= @Controller + @ResponseBody)
public class UserApi {
    @GetMapping("/users/{id}")
    public User getById(@PathVariable long id) {
        return service.findById(id);          // ← объект → HttpMessageConverter → JSON
    }
}
```

Разбор: оба метода поймал один `DispatcherServlet`, оба прошли через `HandlerMapping` и
`HandlerAdapter`. Различие — лишь в финале: `@RestController` говорит «не ищи view, сериализуй
объект». Именно поэтому для API мы используем `@RestController`, а для серверного рендеринга страниц —
`@Controller`. Подробнее виды контроллеров разберём в следующей лекции.

## HandlerInterceptor: контроль на входе и выходе

Раз все запросы идут через одну дверь, удобно повесить на неё «вахтёра», который сработает до и после
контроллера для всех (или части) URL. Это `HandlerInterceptor` — лёгкая альтернатива сервлетным
фильтрам, работающая уже внутри Spring MVC (знает про найденный handler).

```java
@Component
public class TimingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) {
        req.setAttribute("start", System.nanoTime());  // ← до контроллера
        return true;                                    // ← false прервал бы цепочку
    }

    @Override
    public void afterCompletion(HttpServletRequest req, HttpServletResponse res,
                                Object handler, Exception ex) {
        long ms = (System.nanoTime() - (long) req.getAttribute("start")) / 1_000_000;
        // ← после полного завершения запроса: логируем длительность, метрики
    }
}
```

Где это применяют на практике:

- ✅ аутентификация/авторизация до входа в контроллер;
- ✅ логирование и метрики времени обработки;
- ✅ проброс `traceId` для распределённой трассировки;
- ⚠️ не кладите в интерсептор бизнес-логику — он про инфраструктуру (cross-cutting concerns), а не про домен.

Чтобы интерсептор заработал, его регистрируют через `WebMvcConfigurer#addInterceptors` — это вернёт нас
к теме конфигурации в одной из следующих лекций.

## Заключение

**Что изучено:**
- `DispatcherServlet` — реализация паттерна Front Controller: единая дверь для всех HTTP-запросов.
- Поток запроса: `HandlerMapping` (найти обработчик) → `HandlerAdapter` (вызвать) → контроллер → развилка.
- Развилка по результату: `ViewResolver` + рендер HTML **или** `HttpMessageConverter` (Jackson) → JSON.
- `HandlerInterceptor` как точка `preHandle`/`afterCompletion` для сквозной логики.

**Как применять на практике:**
- Помните развилку: возвращаете `String` под `@Controller` → ищется view; возвращаете объект под `@RestController` → отдаётся JSON.
- Для сквозных задач (auth, логи, трассировка) используйте `HandlerInterceptor`, а не дублируйте код в контроллерах.
- При отладке «почему вернулся не тот ответ» мысленно прогоните запрос по схеме — обычно ошибка в выборе ветви (забыли `@RestController`).

**Что дальше:** разберём все виды контроллеров Spring — `@Controller` против `@RestController`,
функциональные эндпоинты и глобальную обработку ошибок через `@ControllerAdvice`.

[⬅ Паттерн MVC](01-mvc-pattern.md) · [📑 Оглавление модуля](README.md) · [Виды контроллеров в Spring ➡](03-controller-types.md)
