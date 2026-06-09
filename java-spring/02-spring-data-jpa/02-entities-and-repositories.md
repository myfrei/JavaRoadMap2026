# Лекция: Сущности и репозитории

> Java Spring · Модуль 2 — Spring Data JPA и базы данных · [⬅ К содержанию трека](../README.md)

## Введение

В [прошлой лекции](01-spring-data-jpa-intro.md) мы получили репозиторий «из коробки» — но репозиторий бесполезен без того, чем он управляет, без **сущности** (entity). Сущность — это Java-класс, который JPA умеет хранить в таблице и доставать обратно.

Представьте сущность как анкету: класс описывает поля анкеты (имя, дата рождения), а таблица в базе — это шкаф, где лежат заполненные бланки. Аннотации — это пометки на полях анкеты: «это уникальный номер», «это значение храни строкой». Давайте разберём, какими пометками связать класс с таблицей и где новички чаще всего ранят себя.

## Минимальная сущность: @Entity, @Table, @Id, @GeneratedValue

```java
import jakarta.persistence.*;          // ← Spring Boot 3.x: пакет jakarta, не javax!

@Entity                                // ← «этот класс — сущность JPA»
@Table(name = "orders")                // ← в какой таблице храним (иначе имя = имя класса)
public class Order {

    @Id                                // ← первичный ключ
    @GeneratedValue(strategy = GenerationType.IDENTITY)   // ← кто генерирует id
    private Long id;

    @Column(name = "order_number", nullable = false, unique = true, length = 32)
    private String number;             // ← маппинг на колонку с ограничениями

    protected Order() { }              // ← пустой конструктор обязателен для Hibernate

    public Order(String number) {      // ← бизнес-конструктор для нашего кода
        this.number = number;
    }
    // геттеры опущены для краткости
}
```

Разберём:

- `@Entity` помечает класс как управляемый JPA. Без него Hibernate про класс «не знает».
- `@Table(name = ...)` задаёт имя таблицы. Хорошая привычка — указывать явно, чтобы не зависеть от регистра и реализации.
- `@Id` отмечает первичный ключ.
- `protected` конструктор без аргументов нужен Hibernate, чтобы создавать прокси и объекты при чтении. ⚠️ Часто про него забывают — и получают `InstantiationException` в рантайме.

## Стратегии @GeneratedValue и почему это влияет на батчинг

Стратегия генерации id — не косметика: она напрямую определяет, сможет ли Hibernate сохранять записи **пачками** (batch insert).

```java
@GeneratedValue(strategy = GenerationType.IDENTITY)   // id даёт автоинкремент БД
// vs
@GeneratedValue(strategy = GenerationType.SEQUENCE,   // id даёт sequence в БД
                generator = "order_seq")
@SequenceGenerator(name = "order_seq",
                   sequenceName = "order_id_seq",
                   allocationSize = 50)               // ← резервируем по 50 id за раз
```

| Стратегия | Откуда id | Батч-вставка | Где работает |
|-----------|-----------|--------------|--------------|
| `IDENTITY` | `AUTO_INCREMENT` колонки | ❌ выключена | MySQL, частая дефолтная |
| `SEQUENCE` | sequence объект БД | ✅ работает | PostgreSQL, Oracle, H2 |
| `TABLE` | отдельная таблица-счётчик | ✅ работает | везде, но медленно |
| `AUTO` | выбирает провайдер | зависит | переносимо, но непредсказуемо |

⚠️ Ключевой момент: при `IDENTITY` Hibernate обязан выполнить `INSERT` немедленно для каждой записи, чтобы узнать сгенерированный id, — поэтому **batch insert невозможен**. При `SEQUENCE` Hibernate забирает диапазон id заранее (`allocationSize`) и может склеить много `INSERT` в один батч. Если вы льёте тысячи строк — `SEQUENCE` даёт кратный выигрыш.

✅ Практика: на PostgreSQL берите `SEQUENCE` с `allocationSize`, а не `IDENTITY`.

## @Column, @Enumerated, @Embedded

Три аннотации, которые встречаются в каждой второй сущности.

```java
@Entity
@Table(name = "orders")
public class Order {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_seq")
    private Long id;

    @Enumerated(EnumType.STRING)            // ← храним enum как текст "NEW", а не число 0
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @Embedded                               // ← поля Address «впаиваются» колонками в orders
    private Address shippingAddress;
}

@Embeddable                                 // ← не отдельная таблица, а часть владельца
public class Address {
    @Column(name = "ship_city")  private String city;
    @Column(name = "ship_street") private String street;
}
```

- `@Enumerated(EnumType.STRING)` — храните enum **строкой**, а не порядковым номером. ❌ `EnumType.ORDINAL` (значение по умолчанию!) ломается, стоит вам вставить новое значение в середину enum — все старые числа «сползут» и данные испортятся. ✅ Всегда указывайте `STRING` явно.
- `@Embedded` + `@Embeddable` — встраивает группу полей в ту же таблицу владельца. Удобно для value-объектов (адрес, деньги, период), которые логически едины, но не заслуживают своей таблицы.

## ⚠️ equals и hashCode для сущностей: по бизнес-ключу, НЕ по id

Это место, где спотыкается большинство — и баг проявляется не сразу. Соблазн велик: «сравнивать сущности по id, он же уникальный». Но id у новой сущности — `null`, пока она не сохранена.

```java
// ❌ ОПАСНО: equals/hashCode на основе id
@Override public boolean equals(Object o) {
    if (!(o instanceof Order other)) return false;
    return Objects.equals(id, other.id);   // у новой сущности id == null
}
@Override public int hashCode() {
    return Objects.hash(id);               // hashCode МЕНЯЕТСЯ после save() — id появился!
}
```

Почему это бомба замедленного действия:

1. Вы создаёте два разных новых заказа — у обоих `id == null` → они «равны» по `equals`. Положили в `HashSet` — один потерялся.
2. Вы положили сущность в `Set`, затем `save()` присвоил id → `hashCode` изменился → объект «пропал» из своего же набора.

✅ Правильно — строить `equals`/`hashCode` на **бизнес-ключе** (natural key): поле, уникальное по смыслу и неизменное (номер заказа, email, ISBN).

```java
@Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Order other)) return false;
    return number != null && number.equals(other.number);   // ← бизнес-ключ, не id
}
@Override public int hashCode() {
    return Objects.hash(number);          // стабилен на всём жизненном цикле объекта
}
```

Если стабильного бизнес-ключа нет — генерируйте UUID в конструкторе и стройте equals на нём.

## DTO vs Entity: не отдавайте сущности в API

Сильное искушение — вернуть `Order` прямо из контроллера. ❌ Так делать не надо, и вот почему:

- **Утечка структуры БД** наружу — клиент API становится завязан на ваши таблицы.
- **`LazyInitializationException`** — при сериализации Jackson дёргает lazy-связь вне транзакции (про это подробно в [лекции 5](05-hibernate-internals.md)).
- **Лишние/секретные поля** утекают в JSON (внутренние флаги, хеши).

✅ Возвращайте отдельный **DTO** (Data Transfer Object) — плоскую структуру ровно с теми полями, что нужны клиенту. В Java 21 идеально подходит `record`:

```java
public record OrderDto(Long id, String number, String status) {

    static OrderDto from(Order order) {            // ← маппинг entity → DTO в одном месте
        return new OrderDto(order.getId(),
                            order.getNumber(),
                            order.getStatus().name());
    }
}
```

## CRUD через репозиторий: собираем всё вместе

Сервис с конструкторной инъекцией репозитория (никаких `@Autowired` на поле):

```java
@Service
public class OrderService {

    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {   // ← конструкторная инъекция
        this.repository = repository;
    }

    @Transactional
    public OrderDto create(String number) {
        Order saved = repository.save(new Order(number));   // INSERT
        return OrderDto.from(saved);                        // наружу — DTO, не Order
    }

    @Transactional(readOnly = true)
    public OrderDto getById(Long id) {
        Order order = repository.findById(id)               // SELECT
                .orElseThrow(() -> new IllegalArgumentException("Заказ не найден: " + id));
        return OrderDto.from(order);
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);                          // DELETE
    }
}
```

✅ `@Transactional(readOnly = true)` на чтении — подсказка Hibernate, что dirty checking не нужен (мелкая, но приятная оптимизация).

## Заключение

**Что изучено:**
- Сущность = класс + `@Entity`/`@Table`/`@Id`; нужен пустой конструктор для Hibernate.
- Стратегии `@GeneratedValue`: `IDENTITY` убивает batch insert, `SEQUENCE` его разрешает.
- `@Enumerated(EnumType.STRING)` обязателен; `@Embedded` встраивает value-объекты.
- `equals`/`hashCode` — по бизнес-ключу, **никогда** по id.
- DTO вместо Entity в API: безопасность, отсутствие lazy-ошибок, чистый контракт.

**Как применять на практике:**
- На PostgreSQL — `SEQUENCE` + `allocationSize`, enum'ы — только `STRING`.
- Заведите бизнес-ключ (или UUID) и стройте `equals`/`hashCode` на нём.
- Для каждой сущности — DTO/`record` и один метод `from(...)` для маппинга.

**Что дальше:** свяжем сущности между собой — `@OneToMany`, `@ManyToOne`, владелец связи и печально известная проблема N+1.

[⬅ Spring Data JPA: репозитории из коробки](01-spring-data-jpa-intro.md) · [📑 Оглавление модуля](README.md) · [Связи между сущностями ➡](03-entity-relationships.md)
