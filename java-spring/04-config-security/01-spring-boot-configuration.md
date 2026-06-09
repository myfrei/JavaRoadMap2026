# Лекция: Конфигурирование Spring Boot приложения

> Java Spring · Модуль 4 — Конфигурация и безопасность · [⬅ К содержанию трека](../README.md)

## Введение

Представьте, что вы собрали кофемашину и хотите продать её в разные страны. В одной розетки на 220 вольт,
в другой — на 110, где-то воду наливают жёсткую, где-то мягкую. Глупо паять отдельную модель под каждый
рынок — гораздо умнее сделать один аппарат с переключателями и настройками. Прошивка одна, поведение
разное.

С приложением та же история. На вашем ноутбуке оно ходит в локальную базу, в проде — в боевой кластер,
на сервере коллеги — в тестовый стенд. Перекомпилировать `jar` под каждое окружение — путь в ад. Давайте
разберём, как Spring Boot выносит все настройки *наружу*, чтобы один и тот же артефакт запускался где
угодно, меняя лишь параметры.

## Внешняя конфигурация и порядок приоритетов

Spring Boot читает настройки из множества источников и **накладывает их друг на друга**. Если один и тот
же ключ задан в нескольких местах, побеждает источник с более высоким приоритетом. Это называется
**externalized configuration** — конфигурация живёт вне кода.

Упрощённая иерархия (от низшего приоритета к высшему):

```
application.yml                      ← базовые значения (в jar)
        ▼  перекрывается
application-{profile}.yml            ← настройки активного профиля (dev/prod)
        ▼  перекрывается
переменные окружения (env)           ← APP_DB_PASSWORD=... (внешний мир)
        ▼  перекрывается
аргументы командной строки           ← --server.port=9000 (самый высокий приоритет)
```

То есть аргумент `--server.port=9000` переопределит порт, заданный в `application.yml`. А переменная
окружения переопределит профильный файл. Это удобно: дефолты лежат в проекте, а на конкретной машине вы
точечно подкручиваете нужное, ничего не пересобирая.

```bash
# дефолт из application.yml — порт 8080, но мы переопределяем его аргументом
java -jar app.jar --server.port=9000        # ← аргумент победит любой файл
```

## Формат файла: .properties или .yaml

Настройки можно писать в двух форматах. Они эквивалентны по возможностям, отличается только синтаксис.

```properties
# application.properties — плоские ключи через точку
spring.datasource.url=jdbc:postgresql://localhost:5432/appdb
spring.datasource.username=app
server.port=8080
```

```yaml
# application.yml — то же самое, но с вложенностью (без повторов префиксов)
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/appdb
    username: app
server:
  port: 8080
```

| Критерий | `.properties` | `.yaml` |
|---|---|---|
| Вложенность | ❌ повторяем `spring.datasource.` | ✅ дерево, без повторов |
| Списки и сложные структуры | ⚠️ через индексы `[0]`, `[1]` | ✅ нативно, дефисами |
| Читаемость больших конфигов | ❌ простыни плоских строк | ✅ компактнее |
| Чувствительность к отступам | ✅ нет | ⚠️ да, только пробелы |

В современных проектах чаще выбирают `yaml` за читаемость. ⚠️ Но помните: в YAML отступы делаются
**только пробелами**, табы ломают парсер.

## @Value против @ConfigurationProperties

Прочитать значение из конфига в код можно двумя способами. Сравним их на примере настроек внешнего API.

```yaml
# application.yml
payment:
  api-url: https://pay.example.com
  timeout-ms: 5000
  retries: 3
```

**Способ 1 — `@Value`:** вытаскиваем по одному ключу.

```java
@Service
public class PaymentClient {
    private final String apiUrl;

    public PaymentClient(@Value("${payment.api-url}") String apiUrl) {   // ← один ключ = одна строка
        this.apiUrl = apiUrl;
    }
}
```

**Способ 2 — `@ConfigurationProperties`:** маппим целую группу в типизированный объект.

```java
@ConfigurationProperties(prefix = "payment")   // ← берёт всю ветку payment.*
public record PaymentProperties(
        String apiUrl,        // ← payment.api-url    (relaxed binding: kebab → camelCase)
        int timeoutMs,        // ← payment.timeout-ms
        int retries           // ← payment.retries
) {}
```

```java
@Configuration
@EnableConfigurationProperties(PaymentProperties.class)   // ← регистрируем как бин
public class AppConfig { }

@Service
public class PaymentClient {
    private final PaymentProperties props;

    public PaymentClient(PaymentProperties props) {   // ← внедряем целиком, type-safe
        this.props = props;
    }
}
```

| Критерий | `@Value` | `@ConfigurationProperties` |
|---|---|---|
| Сколько ключей удобно читать | 1–2 | группу целиком |
| Тип-безопасность | ⚠️ строки + SpEL | ✅ типизированный объект |
| Валидация (`@Validated`, `jakarta.validation`) | ❌ нет | ✅ есть |
| Подсказки в IDE | ❌ нет | ✅ есть (через metadata) |
| Группировка по смыслу | ❌ разбросано | ✅ всё в одном классе |

✅ Для пары значений подойдёт `@Value`. Для связного набора настроек предпочитайте
`@ConfigurationProperties` — это type-safe, проверяемо и читается как обычный Java-объект.

## Профили под окружения

**Профиль (profile)** — это именованный набор настроек под конкретное окружение. Файл
`application-{profile}.yml` подхватывается, только когда профиль активен.

```yaml
# application.yml — общие настройки + дефолтный профиль
spring:
  profiles:
    active: dev          # ← активный профиль по умолчанию
```

```yaml
# application-dev.yml — локальная разработка
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/appdb
logging:
  level:
    root: DEBUG          # ← подробные логи локально
```

```yaml
# application-prod.yml — продакшн
spring:
  datasource:
    url: ${DB_URL}       # ← адрес придёт из переменной окружения
logging:
  level:
    root: WARN           # ← в проде только важное
```

Активировать профиль можно аргументом или переменной окружения — без пересборки:

```bash
java -jar app.jar --spring.profiles.active=prod     # ← запускаем с профилем prod
```

## Условные бины: @ConditionalOnProperty

Иногда нужно включать компонент только при определённой настройке — например, отправку метрик включать в
проде и выключать локально. Для этого есть `@ConditionalOnProperty`.

```java
@Service
@ConditionalOnProperty(
        prefix = "metrics",
        name = "enabled",
        havingValue = "true")   // ← бин создастся, только если metrics.enabled=true
public class MetricsReporter {
    // ... отправка метрик во внешнюю систему
}
```

```yaml
# application-prod.yml
metrics:
  enabled: true     # ← в проде включено; в dev ключа нет → бин не создаётся
```

Так одна кодовая база гибко собирает разный набор бинов под окружение, не трогая Java-код.

## Секреты: env и secret-manager, не git

Самое опасное в конфигурации — это пароли, токены и ключи. ⚠️ **Их нельзя коммитить в репозиторий.**
Даже приватный репозиторий — это копии на ноутбуках, в CI, в бэкапах и навсегда в истории git.

```yaml
# ❌ НИКОГДА не делайте так — секрет попадёт в git и останется в истории
spring:
  datasource:
    password: SuperSecret123
```

```yaml
# ✅ значение приходит из переменной окружения, в файле — только ссылка
spring:
  datasource:
    password: ${DB_PASSWORD}        # ← заполняется снаружи, в коде секрета нет
```

Откуда брать сами значения:

- ✅ **Переменные окружения** — простой и переносимый способ (`export DB_PASSWORD=...`).
- ✅ **Secret-manager** — HashiCorp Vault, AWS Secrets Manager, Kubernetes Secrets: хранят секреты
  централизованно, с ротацией и доступом по правам.
- ❌ **Файлы в git** — даже «временно», даже «потом удалю». Удалить из истории сложно, а утечка уже
  случилась.

⚠️ **Relaxed binding** работает и здесь: переменная окружения `DB_PASSWORD` свяжется со свойством
`db.password`, а `SPRING_DATASOURCE_PASSWORD` — со `spring.datasource.password`. Spring сам приводит
ВЕРХНИЙ_РЕГИСТР_С_ПОДЧЁРКИВАНИЯМИ к точечной нотации.

## Кратко об Actuator

Когда приложение работает в проде, хочется заглянуть внутрь: живо ли оно, какие настройки применились,
сколько памяти ест. Для этого есть стартер **Spring Boot Actuator** — он добавляет служебные HTTP-эндпоинты.

```yaml
# application.yml — открываем только нужные эндпоинты
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics   # ← health (жив?), info (о приложении), metrics
```

```http
GET /actuator/health        ← проверка живости (для load balancer / Kubernetes)

200 OK
{ "status": "UP" }
```

⚠️ Actuator отдаёт чувствительную информацию (конфиг, окружение), поэтому в проде его эндпоинты
**обязательно** закрывают авторизацией — об этом весь дальнейший модуль.

## Заключение

**Что изучено:**

- Конфигурация выносится наружу (externalized configuration); источники накладываются по приоритету:
  аргументы > env > `application-{profile}.yml` > `application.yml`.
- Форматы `.properties` и `.yaml` равнозначны; `yaml` читаемее, но чувствителен к отступам.
- `@Value` — для отдельных значений; `@ConfigurationProperties` — для групп настроек (type-safe, с валидацией).
- Профили (`application-{profile}.yml`) разделяют настройки по окружениям; `@ConditionalOnProperty` включает бины по условию.
- Actuator даёт служебные эндпоинты для мониторинга — но требует защиты в проде.

**Как применять на практике:**

- Дефолты держите в `application.yml`, отличия окружений — в профильных файлах.
- Связные настройки оборачивайте в `@ConfigurationProperties`-record, а не в россыпь `@Value`.
- Секреты — только через env или secret-manager; в файле оставляйте `${PLACEHOLDER}`, никогда не само значение.
- Профиль и порт переключайте аргументами/переменными, не пересобирая артефакт.

**Что дальше:** прежде чем настраивать Spring Security, разберём базовые понятия безопасности
веб-приложений — кого и от чего мы вообще защищаем.

## Ресурсы

- [Spring Boot — Externalized Configuration](https://docs.spring.io/spring-boot/reference/features/external-config.html) — официальная документация по источникам и приоритетам.
- [Spring Boot — Profiles](https://docs.spring.io/spring-boot/reference/features/profiles.html) — профили окружений.
- [Spring Boot — Actuator](https://docs.spring.io/spring-boot/reference/actuator/index.html) — служебные эндпоинты.

[⬅ К треку](../README.md) · [📑 Оглавление модуля](README.md) · [Основы безопасности веб-приложений ➡](02-web-security-basics.md)
