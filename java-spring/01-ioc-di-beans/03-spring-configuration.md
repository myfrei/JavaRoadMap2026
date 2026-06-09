# Лекция: Конфигурация Spring-приложений

> Java Spring · Модуль 1 — IoC, DI и бины · [⬅ К содержанию трека](../README.md)

## Введение

Представьте пульт управления умным домом. На нём вы задаёте: какой свет включить, какую температуру
держать, какой сценарий запустить утром, а какой вечером. Сам дом (лампы, котёл, шторы) не меняется — вы
лишь *настраиваете* его поведение через пульт. И главное: дома днём один режим, на даче в выходные —
другой, а в отпуске — третий.

В Spring таким «пультом» выступает **конфигурация**: вы описываете, какие бины создать, откуда брать
настройки и как поведение меняется в зависимости от окружения (dev / test / prod). Вы наверняка уже видели
`@SpringBootApplication` в `main` — давайте разберём, что именно прячется за этой аннотацией и какими
инструментами настраивается приложение.

## @Configuration и @Bean

Класс, помеченный `@Configuration`, — это «фабрика бинов»: его методы с `@Bean` создают объекты, которыми
будет управлять контейнер.

```java
@Configuration   // ← класс-источник определений бинов
public class HttpConfig {

    @Bean   // ← метод-фабрика: его результат станет бином
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl("https://api.example.com")
                .build();   // ← полностью настроенный объект отдаём контейнеру
    }

    @Bean
    public RetryPolicy retryPolicy() {
        return new RetryPolicy(3);   // ← можно вызывать конструкторы с логикой
    }
}
```

Один бин может зависеть от другого — просто примите его как параметр метода:

```java
@Bean
public OrderClient orderClient(RestClient restClient) {   // ← Spring подставит бин restClient
    return new OrderClient(restClient);
}
```

⚠️ Тонкость: `@Configuration`-классы Spring оборачивает в прокси, поэтому вызов одного `@Bean`-метода из
другого (`restClient()` напрямую) возвращает *тот же* singleton, а не новый объект. Это поведение
отличает `@Configuration` от обычного класса.

## @ComponentScan: автоматический поиск бинов

Прописывать каждый бин руками утомительно. `@ComponentScan` говорит Spring: «пройдись по этим пакетам и
сам подбери все классы со стереотипами».

```java
@Configuration
@ComponentScan(basePackages = "com.javaroadmap.shop")   // ← искать @Component/@Service/... здесь и ниже
public class AppConfig {
}
```

Найдя класс с `@Service`, `@Repository`, `@Controller` или `@Component`, контейнер автоматически создаст из
него бин. Именно благодаря сканированию вы не объявляете свои сервисы и репозитории вручную.

## @SpringBootApplication: три аннотации в одной

В Spring Boot вы редко пишете эти аннотации по отдельности. Точка входа помечается одной
`@SpringBootApplication`, и вот что она в себя включает:

```java
@SpringBootApplication   // ← = @Configuration + @ComponentScan + @EnableAutoConfiguration
public class ShopApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShopApplication.class, args);   // ← поднимает контекст
    }
}
```

Разберём три «слагаемых»:

- `@Configuration` — сам класс может содержать `@Bean`-методы.
- `@ComponentScan` — сканирует пакет этого класса **и все вложенные** (поэтому главный класс кладут в
  корневой пакет проекта).
- `@EnableAutoConfiguration` — автоконфигурация: Spring смотрит, что есть в classpath, и сам настраивает
  разумные умолчания (нашёл драйвер БД → настроит `DataSource`; нашёл веб-стартер → поднимет HTTP-сервер).

## Java-config vs XML: немного истории

Раньше (Spring 2.x–3.x) бины описывались в XML-файлах — длинных и без проверки типов компилятором.
Сегодня это легаси; знать о нём стоит лишь чтобы не пугаться в старых проектах.

```xml
<!-- ❌ так настраивали бины раньше — многословно, опечатки ловятся только в рантайме -->
<bean id="orderService" class="com.shop.OrderService">
    <constructor-arg ref="orderRepository"/>
</bean>
```

```java
// ✅ современный java-config — типобезопасно, рефакторинг и автодополнение работают
@Bean
public OrderService orderService(OrderRepository repo) {
    return new OrderService(repo);
}
```

Вывод однозначный: **в новых проектах — java-config и аннотации**, XML не используем.

## Свойства: application.yml и application.properties

Настройки (URL базы, порт, таймауты) не зашивают в код — их выносят в файл свойств. Spring Boot читает
`application.yml` или `application.properties` из `src/main/resources`.

```yaml
# application.yml — формат с отступами, удобен для вложенности
server:
  port: 8080
app:
  greeting: "Привет"
  retry-attempts: 3
  feature:
    dark-mode: true
```

То же самое в `.properties` — плоские ключи:

```properties
# application.properties
server.port=8080
app.greeting=Привет
app.retry-attempts=3
app.feature.dark-mode=true
```

| Критерий | `application.yml` | `application.properties` |
|---|---|---|
| Вложенность | ✅ наглядная, через отступы | ⚠️ через точки в ключе |
| Списки и сложные структуры | ✅ удобно | ⚠️ громоздко |
| Чувствительность к отступам | ⚠️ да (легко ошибиться) | ✅ нет |
| Поддержка из коробки | ✅ да | ✅ да |

Оба формата равнозначны по возможностям; в современных проектах чаще выбирают YAML за читаемость вложенных
структур.

## @Value vs @ConfigurationProperties

Прочитать свойство в код можно двумя способами.

**`@Value` — точечно, одно свойство.**

```java
@Service
public class GreetingService {
    private final String greeting;

    public GreetingService(@Value("${app.greeting}") String greeting) {   // ← одно свойство по ключу
        this.greeting = greeting;
    }
}
```

**`@ConfigurationProperties` — type-safe, группа свойств в объект.**

```java
@ConfigurationProperties(prefix = "app")   // ← привяжет все ключи app.* к полям
public class AppProperties {
    private String greeting;
    private int retryAttempts;     // ← app.retry-attempts (kebab → camelCase автоматически)
    private Feature feature;       // ← вложенный объект для app.feature.*

    // геттеры/сеттеры...
    public static class Feature {
        private boolean darkMode;  // ← app.feature.dark-mode
        // геттеры/сеттеры...
    }
}
```

Чтобы Spring подхватил такой класс, включите сканирование (например, `@ConfigurationPropertiesScan` на
главном классе) и внедряйте его как обычный бин:

```java
@Service
public class GreetingService {
    private final AppProperties props;

    public GreetingService(AppProperties props) {   // ← вся группа настроек одним объектом
        this.props = props;
    }
}
```

✅ Для одного-двух значений хватит `@Value`. Для группы связанных свойств предпочитайте
`@ConfigurationProperties`: это типобезопасно, легко валидировать (`@Validated`) и удобно тестировать.

## Профили: разное поведение в разных окружениях

Профиль (profile) — это именованный набор настроек/бинов под конкретное окружение: `dev`, `test`, `prod`.

```java
@Repository
@Profile("dev")   // ← этот бин создаётся ТОЛЬКО при активном профиле dev
public class InMemoryOrderRepository implements OrderRepository { /* ... */ }

@Repository
@Profile("prod")   // ← а этот — только в проде
public class JdbcOrderRepository implements OrderRepository { /* ... */ }
```

Какой профиль активен, задаётся свойством `spring.profiles.active`:

```yaml
# application.yml
spring:
  profiles:
    active: dev   # ← или передать снаружи: java -Dspring.profiles.active=prod -jar app.jar
```

Профильные свойства держат в отдельных файлах: `application-dev.yml`, `application-prod.yml`. Spring
наложит их поверх базового `application.yml` в зависимости от активного профиля.

## @Import: подключение конфигураций

Когда конфигураций несколько, `@Import` собирает их вместе — удобно для модульной структуры.

```java
@Configuration
@Import({ HttpConfig.class, SecurityConfig.class })   // ← подтянуть бины из этих классов
public class AppConfig {
}
```

Так вы разбиваете настройку на тематические блоки (HTTP, безопасность, кэш) и явно их собираете в одной
точке.

## Заключение

**Что изучено:**

- `@Configuration` + `@Bean` — программное объявление бинов; `@ComponentScan` — автоматический поиск.
- `@SpringBootApplication` = `@Configuration` + `@ComponentScan` + `@EnableAutoConfiguration`.
- Java-config вытеснил XML: типобезопасно, дружит с рефакторингом.
- Свойства живут в `application.yml`/`.properties`; читаются через `@Value` или type-safe `@ConfigurationProperties`.
- `@Profile` + `spring.profiles.active` дают разное поведение по окружениям; `@Import` собирает конфигурации.

**Как применять на практике:**

- Главный класс с `@SpringBootApplication` держите в корневом пакете — тогда сканирование охватит весь проект.
- Группы настроек выносите в `@ConfigurationProperties`, отдельные значения — в `@Value`.
- Разделяйте окружения профилями и файлами `application-{profile}.yml`, секреты не коммитьте.
- Дробите большую конфигурацию на тематические классы и собирайте через `@Import`.

**Что дальше:** спустимся на сервисный слой — туда, где живёт бизнес-логика приложения.

[⬅ Бины и их жизненный цикл](02-beans-and-lifecycle.md) · [📑 Оглавление модуля](README.md) · [Сервисный слой и аннотация @Service ➡](04-service-layer.md)
