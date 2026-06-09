# Лекция: JDBC Template vs Hibernate: когда нужно быстро

> Java Spring · Модуль 2 — Spring Data JPA и базы данных · [⬅ К содержанию трека](../README.md)

## Введение

Весь этот модуль мы пели Hibernate дифирамбы — и заслуженно: для CRUD над доменными сущностями он экономит кучу времени. Но опытный инженер знает второе правило: у любой абстракции есть цена, и иногда удобнее снять её и поработать с базой напрямую.

Представьте Hibernate как автомобиль с автоматической коробкой: 95% поездок по городу — комфорт и удобство. Но на гоночном треке (тяжёлый отчёт, заливка миллиона строк) вам нужна механика — полный контроль над каждым переключением. В Spring эта «механика» — `JdbcTemplate` и `JdbcClient`. Давайте разберём, где Hibernate начинает мешать и как переключиться на ручной SQL.

## Где Hibernate начинает мешать

Hibernate великолепен, пока вы работаете с *объектным графом*. Проблемы начинаются на других задачах:

- **Магия и непредсказуемый SQL.** Вы пишете Java, а в БД уходит SQL, который вы не видели. Под нагрузкой это сюрпризы: лишние `SELECT`, неожиданные `UPDATE` от dirty checking (см. [лекцию 5](05-hibernate-internals.md)).
- **N+1 на сложных выборках.** Хитрый отчёт со связями легко превращается в лавину запросов (см. [лекцию 3](03-entity-relationships.md)).
- **Тяжёлые отчёты и аналитика.** Запрос с агрегатами, оконными функциями, `GROUP BY` по пяти таблицам неудобно и неэффективно выражать через сущности — вам не нужны управляемые объекты, нужны строки с числами.
- **Bulk-операции.** «Обновить статус у миллиона заказов» через загрузку миллиона сущностей в персистентный контекст — это OutOfMemory. Один `UPDATE ... WHERE` сделает то же мгновенно.

✅ Вывод не «Hibernate плохой», а «у каждой задачи свой инструмент». Хорошая новость: в одном Spring-проекте они прекрасно уживаются.

## JdbcTemplate и JdbcClient: явный SQL под контролем

`JdbcTemplate` — классический помощник Spring поверх голого JDBC. Он берёт на себя рутину (соединения, `PreparedStatement`, закрытие ресурсов, перевод `SQLException` в `DataAccessException`), но **SQL вы пишете сами** — никакой генерации.

```java
@Repository
public class OrderReportRepository {

    private final JdbcTemplate jdbc;

    public OrderReportRepository(JdbcTemplate jdbc) {   // ← конструкторная инъекция
        this.jdbc = jdbc;
    }
}
```

`JdbcClient` (Spring 6.1+ / Boot 3.2+) — современная «текучая» (fluent) обёртка над тем же `JdbcTemplate`, с именованными параметрами и более читаемым API:

```java
List<OrderRow> rows = jdbcClient
        .sql("SELECT id, status FROM orders WHERE status = :status")   // явный SQL
        .param("status", "NEW")                                        // именованный параметр
        .query(OrderRow.class)                                         // маппинг в record
        .list();
```

## Пример: query + RowMapper и batchUpdate

**Чтение отчёта.** `RowMapper` превращает строку `ResultSet` в объект — но, в отличие от Hibernate, это **обычный** объект (не управляемая сущность, без персистентного контекста и lazy-прокси):

```java
// record под результат отчёта — это НЕ @Entity, просто переносчик данных
public record StatusCount(String status, long total) { }

public List<StatusCount> countByStatus() {
    String sql = """
            SELECT status, COUNT(*) AS total
            FROM orders
            GROUP BY status
            ORDER BY total DESC
            """;                                          // ← ровно тот SQL, что уйдёт в БД
    return jdbc.query(sql, (rs, rowNum) ->                // RowMapper: строка → объект
            new StatusCount(
                    rs.getString("status"),
                    rs.getLong("total")));
}
```

Никакой магии: один запрос, один проход, предсказуемый план. Для аналитики это именно то, что нужно.

**Массовая вставка.** `batchUpdate` шлёт пачку строк одним батчем — на порядок быстрее, чем по одному `INSERT`, и без раздувания персистентного контекста:

```java
public void insertAll(List<Order> orders) {
    String sql = "INSERT INTO orders (order_number, status) VALUES (?, ?)";
    jdbc.batchUpdate(sql, orders, 500,                    // ← размер батча
            (ps, order) -> {                              // как заполнить ? для каждой строки
                ps.setString(1, order.getNumber());
                ps.setString(2, order.getStatus().name());
            });
}
```

✅ Для заливки сотен тысяч строк `batchUpdate` обходит даже Hibernate с `SEQUENCE`-батчингом — и не рискует памятью.

## Когда что выбирать

Эвристика простая: **доменная работа с объектами — JPA; работа с данными как с данными — JDBC.**

| Задача | Инструмент | Почему |
|--------|------------|--------|
| CRUD над доменными сущностями | ✅ JPA / Hibernate | граф объектов, dirty checking, меньше кода |
| Сложные отчёты, аналитика, агрегаты | ✅ JdbcTemplate / JdbcClient | предсказуемый SQL, без N+1 |
| Bulk update/delete (миллионы строк) | ✅ JdbcTemplate | один `UPDATE`, без загрузки в контекст |
| Тонкая оптимизация конкретного запроса | ✅ JdbcTemplate | полный контроль над SQL и планом |
| Запросы под фичи конкретной СУБД | ✅ JdbcTemplate | сырой SQL, любые диалектные конструкции |
| Простой `findById` / `save` | ✅ JPA-репозиторий | две строки против ручного маппинга |

⚠️ Антипаттерн в обе стороны: писать весь домашний CRUD на голом JDBC (вернётся boilerplate из [лекции 1](01-spring-data-jpa-intro.md)) — и наоборот, выжимать тяжёлую аналитику через сущности.

## Гибрид в одном проекте

Главная мысль модуля: это **не** выбор «или–или». В одном Spring-приложении JPA и JdbcTemplate сосуществуют поверх **одного `DataSource`** и в **общих транзакциях** — Spring это поддерживает из коробки.

```java
@Service
public class OrderFacade {

    private final OrderRepository jpaRepository;          // JPA — для CRUD
    private final OrderReportRepository reportRepository;  // JdbcTemplate — для отчётов

    public OrderFacade(OrderRepository jpaRepository,
                       OrderReportRepository reportRepository) {
        this.jpaRepository = jpaRepository;
        this.reportRepository = reportRepository;
    }

    @Transactional
    public OrderDto create(String number) {               // доменная операция → JPA
        return OrderDto.from(jpaRepository.save(new Order(number)));
    }

    @Transactional(readOnly = true)
    public List<StatusCount> dashboard() {                // тяжёлый отчёт → JDBC
        return reportRepository.countByStatus();
    }
}
```

```
                      ┌──────────────────────┐
   доменный CRUD ───▶ │  Spring Data JPA      │ ─┐
                      └──────────────────────┘  │   ┌─────────────┐    ┌──────┐
                                                 ├─▶ │ DataSource  │ ─▶ │  БД  │
                      ┌──────────────────────┐  │   └─────────────┘    └──────┘
   отчёты / bulk ───▶ │  JdbcTemplate/Client  │ ─┘   (общий пул + транзакции)
                      └──────────────────────┘
```

✅ Один источник данных, одни транзакции — берите для каждой задачи тот инструмент, который ей подходит, не покидая проект.

## Заключение

**Что изучено:**
- Минусы Hibernate: скрытый SQL, риск N+1, неудобство тяжёлых отчётов и bulk-операций.
- `JdbcTemplate` / `JdbcClient` дают явный SQL, контроль и скорость, снимая лишь boilerplate JDBC.
- `RowMapper` маппит строки в обычные объекты; `batchUpdate` быстро льёт пачки данных.
- Правило выбора: доменный CRUD → JPA; отчёты/bulk/тонкая производительность → JDBC.
- JPA и JdbcTemplate работают вместе поверх одного `DataSource` и общих транзакций.

**Как применять на практике:**
- Начинайте с JPA-репозиториев; как только запрос «не лезет» в сущности — пишите его на `JdbcTemplate`.
- Все массовые операции (`bulk update/delete`, заливка данных) делайте сырым SQL, а не через сущности.
- Не выбирайте инструмент на весь проект — выбирайте под конкретную задачу.

**Что дальше:** вы прошли весь путь по данным — от репозиториев «из коробки» до осознанного выбора между ORM и голым SQL. Возвращайтесь к [оглавлению модуля](README.md) и закрепляйте материал на практике, а затем двигайтесь к следующему модулю трека.

[⬅ Hibernate изнутри: сущности и их особенности](05-hibernate-internals.md) · [📑 Оглавление модуля](README.md)
