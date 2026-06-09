# Spring Boot и внедрение зависимостей (DI)

> Модуль 6 — Web и API · [к модулю](../../../course/06-web-api.md)

## Зачем это нужно

Внедрение зависимостей (Dependency Injection) — основа всего Spring: компоненты не создают свои
зависимости сами, а получают их извне. Это даёт слабую связанность и тестируемость (легко подменить
реализацию на мок). Spring Boot добавляет автоконфигурацию — рабочее приложение из минимума кода.

**Где применяется в проектах:** практически каждый корпоративный Java-бэкенд; структура «контроллер →
сервис → репозиторий»; конфигурация по профилям (dev/prod).

## Жёсткая связанность — плохо

```java
// ❌ сервис сам создаёт зависимость — не подменить, не протестировать
class OrderService {
    private final OrderRepository repo = new JdbcOrderRepository();  // жёстко зашито
}
```

## DI через конструктор — хорошо

```java
@Service
public class OrderService {
    private final OrderRepository repo;

    public OrderService(OrderRepository repo) {   // зависимость ПРИХОДИТ извне
        this.repo = repo;
    }
}
```

Spring видит `@Service`, находит подходящий бин `OrderRepository` и сам передаёт его в конструктор.
В тесте можно передать мок — `new OrderService(mockRepo)` — без поднятия Spring.

> Конструкторная инъекция — предпочтительный стиль: зависимости видны, поле можно сделать `final`,
> объект всегда в валидном состоянии.

## Стереотипы-аннотации

- `@Component` — общий бин; `@Service` — сервис (бизнес-логика); `@Repository` — слой данных;
- `@RestController` — веб-контроллер;
- `@Configuration` + `@Bean` — ручное создание бинов.

## Точка входа и автоконфигурация

```java
@SpringBootApplication        // = @Configuration + @ComponentScan + @EnableAutoConfiguration
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);   // поднимает контекст и встроенный сервер
    }
}
```

`@EnableAutoConfiguration` сама настраивает то, что найдено в classpath: есть драйвер БД → настроит
`DataSource`; есть веб-стартер → поднимет HTTP-сервер. Меньше шаблона — больше дела.

## Профили и конфиг

```yaml
# application.yml
spring:
  datasource:
    url: ${DB_URL:jdbc:postgresql://localhost:5432/appdb}   # значение из env с дефолтом
```

Профили (`dev`, `prod`) позволяют держать разные настройки и подменять бины под окружение.

## Итог

**Что изучено:**
- DI: зависимости приходят извне → слабая связанность и тестируемость.
- Конструкторная инъекция предпочтительна; стереотипы `@Service`/`@Repository`/`@RestController`.
- `@SpringBootApplication` + автоконфигурация поднимают приложение из минимума кода.

**Как применять на практике:**
- Внедрять зависимости через конструктор и хранить их в `final`-полях.
- Делить код по слоям (контроллер/сервис/репозиторий) — это упрощает тесты и развитие.
- Конфигурацию держать в `application.yml` + env, разделять по профилям.
