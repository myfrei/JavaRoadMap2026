# Валидация и обработка ошибок

> Модуль 6 — Web и API · [к модулю](../../../course/06-web-api.md)

## Зачем это нужно

Любой внешний ввод — потенциально невалидный или враждебный. Валидация на границе (в контроллере)
не пускает «мусор» в бизнес-логику и БД. А единый формат ошибок делает API предсказуемым: клиент
всегда знает, как выглядит ответ при сбое.

**Где применяется в проектах:** все API, принимающие данные; формы; интеграции; защита от некорректных
и вредоносных запросов (вместе с модулем 16 «Безопасность»).

## Bean Validation — декларативно

Правила вешаются аннотациями на поля DTO:

```java
public record CreateUser(
        @NotBlank String name,                       // не пустая строка
        @Email String email,                         // формат email
        @Min(0) @Max(150) int age) {}                // диапазон
```

```java
@PostMapping("/users")
public ResponseEntity<User> create(@Valid @RequestBody CreateUser dto) {
    //                              ^^^^^^ @Valid запускает проверку ДО входа в метод
    return ResponseEntity.status(201).body(service.create(dto));
}
```

Если данные невалидны, Spring бросит `MethodArgumentNotValidException` — метод даже не выполнится.

## Единый обработчик ошибок

Чтобы не дублировать try/catch в каждом контроллере, ошибки ловят централизованно:

```java
@RestControllerAdvice
public class ApiErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> onValidation(MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .toList();
        return ResponseEntity.badRequest().body(new ApiError(400, errors));  // 400
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> onNotFound(NotFoundException e) {
        return ResponseEntity.status(404).body(new ApiError(404, List.of(e.getMessage())));
    }
}

public record ApiError(int status, List<String> messages) {}
```

Теперь любой контроллер отдаёт ошибки в одном формате `{status, messages}`. Это резко упрощает жизнь
клиентам и фронтенду.

## Не показывай внутренности

В ответе об ошибке не должно быть стектрейсов и SQL — это утечка информации (помощь атакующему) и
плохой UX. Логируй детали на сервере, клиенту отдавай безопасное сообщение и код.

## Связь с кодом модуля

`validate(name, age)` в
[homework](../homework/src/main/java/com/javaroadmap/m06/homework/Mod06Homework.java) — это ручная
версия Bean Validation: собрать список ошибок («name is required», «age must be >= 0»). `statusText`
показывает, какой код какому смыслу соответствует.

## Итог

**Что изучено:**
- Bean Validation (`@NotBlank`/`@Email`/`@Min`) + `@Valid` — проверка на границе до логики.
- `@RestControllerAdvice` + `@ExceptionHandler` — единый формат ошибок без дублирования.
- Не отдавать клиенту стектрейсы и внутренние детали.

**Как применять на практике:**
- Валидировать все входящие DTO аннотациями; не пускать невалидные данные дальше контроллера.
- Завести один `@RestControllerAdvice` с предсказуемым телом ошибки (`{status, messages}`).
- Логировать детали на сервере, наружу — безопасные сообщения и корректные коды.
