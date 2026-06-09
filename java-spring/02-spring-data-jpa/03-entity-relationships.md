# Лекция: Связи между сущностями

> Java Spring · Модуль 2 — Spring Data JPA и базы данных · [⬅ К содержанию трека](../README.md)

## Введение

Сущности редко живут поодиночке. У заказа есть позиции, у клиента — заказы, у книги — авторы. В реляционной базе эти связи держатся на внешних ключах (foreign key), а JPA даёт для них аннотации: `@OneToMany`, `@ManyToOne`, `@OneToOne`, `@ManyToMany`.

Представьте генеалогическое древо. У человека один отец (`@ManyToOne` со стороны детей), но много детей (`@OneToMany`). Связь одна и та же — просто смотрим с разных сторон. Давайте разберёмся, как описать эти стороны в коде, кто из них «главный», и почему ленивая загрузка может незаметно превратить один запрос в сотню.

## Четыре типа связей

```
@OneToOne     User  ───  Passport      один к одному
@OneToMany    Order ───< OrderItem     один ко многим  (у заказа много позиций)
@ManyToOne    OrderItem >─── Order      многие к одному (у позиции один заказ)
@ManyToMany   Book  >──< Author        многие ко многим
```

`@OneToMany` и `@ManyToOne` — это две стороны *одной* связи. Чаще всего вы описываете обе.

| Тип связи | Где живёт FK | Аннотация-владелец | Обратная сторона |
|-----------|--------------|--------------------|--------------------|
| Один-к-одному | в одной из таблиц | `@OneToOne` (с `@JoinColumn`) | `@OneToOne(mappedBy=...)` |
| Многие-к-одному | в таблице «многих» | `@ManyToOne` (с `@JoinColumn`) | `@OneToMany(mappedBy=...)` |
| Один-ко-многим | в таблице «многих» | сторона `@ManyToOne` | `@OneToMany(mappedBy=...)` |
| Многие-ко-многим | в join-таблице | `@ManyToMany` (с `@JoinTable`) | `@ManyToMany(mappedBy=...)` |

## Владелец связи и mappedBy

В реляционной модели связь хранится в **одной** колонке-внешнем ключе. Поэтому JPA должен знать, какая сторона «владеет» этим FK и обновляет его, а какая просто отражает связь. Владелец — та сторона, где стоит `@JoinColumn`; обратная сторона помечается `mappedBy`.

```java
@Entity
public class Order {
    @Id @GeneratedValue private Long id;

    // Обратная (inverse) сторона: НЕ владеет FK, только читает связь.
    // mappedBy = "order" → «внешним ключом управляет поле order в OrderItem»
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();
}

@Entity
public class OrderItem {
    @Id @GeneratedValue private Long id;

    // Владелец (owning side): здесь реально лежит колонка order_id
    @ManyToOne(fetch = FetchType.LAZY)        // ← про LAZY ниже, это важно
    @JoinColumn(name = "order_id")            // ← внешний ключ в таблице order_item
    private Order order;
}
```

⚠️ Частая ошибка: менять связь только на обратной стороне (`order.getItems().add(item)`) и удивляться, что в БД `order_id` остался `null`. Hibernate смотрит на **владельца** — обновлять надо `item.setOrder(order)`. Поэтому заводят хелпер, который синхронизирует обе стороны:

```java
public void addItem(OrderItem item) {
    items.add(item);          // обратная сторона — для удобства обхода в Java
    item.setOrder(this);      // ← владелец — то, что реально пойдёт в FK
}
```

## FetchType: LAZY против EAGER

Это решение влияет на каждый ваш запрос. `FetchType` определяет, *когда* подтягивается связанная сущность:

- `LAZY` — связь загружается **при первом обращении** к ней (отдельным запросом).
- `EAGER` — связь загружается **сразу** вместе с владельцем.

```java
@ManyToOne(fetch = FetchType.LAZY)   // ← рекомендуемое значение для *ToOne
private Order order;

@OneToMany(mappedBy = "order")       // коллекции LAZY по умолчанию — это хорошо
private List<OrderItem> items;
```

| Связь | Дефолт по спецификации | Рекомендация |
|-------|------------------------|--------------|
| `@OneToMany`, `@ManyToMany` | `LAZY` | оставить `LAZY` ✅ |
| `@ManyToOne`, `@OneToOne` | `EAGER` ❌ | принудительно `LAZY` ✅ |

⚠️ Коварство в том, что `@ManyToOne` и `@OneToOne` по умолчанию **EAGER**. Если этого не переопределить, каждый запрос к позиции будет тянуть заказ, тот — клиента, и так далее каскадом. ✅ Правило: **всё делать LAZY**, а нужные связи догружать осознанно (см. ниже).

## CascadeType: что делать со связанными при операции

Каскад говорит: «когда я делаю операцию с владельцем, повтори её на связанных».

```java
@OneToMany(mappedBy = "order",
           cascade = CascadeType.ALL,   // persist/merge/remove «протекают» на items
           orphanRemoval = true)        // item, отвязанный от заказа, будет удалён
private List<OrderItem> items = new ArrayList<>();
```

- `CascadeType.PERSIST` — сохранил заказ → сохранились новые позиции.
- `CascadeType.REMOVE` — удалил заказ → удалились его позиции.
- `CascadeType.ALL` — все операции разом.
- `orphanRemoval = true` — позиция, *удалённая из коллекции*, удаляется и из БД (это не то же самое, что `REMOVE`).

⚠️ Не ставьте `CascadeType.ALL` на `@ManyToOne` (со стороны позиции на заказ) — иначе удаление одной позиции попытается удалить весь заказ.

## Проблема N+1 и как её лечить

Та же беда, что мы видели в [образце модуля 5](../../modules/m05-databases/theory/jpa-hibernate-orm.md), но теперь разберём лечение детально. Сценарий: загрузили список заказов и в цикле обращаемся к их позициям.

```java
List<Order> orders = orderRepository.findAll();   // 1 запрос: SELECT * FROM orders
for (Order order : orders) {
    order.getItems().size();                       // +1 запрос НА КАЖДЫЙ заказ (lazy load)
}
// 100 заказов → 1 + 100 = 101 запрос. Это и есть N+1.
```

Симптом — в логе сотни почти одинаковых мелких `SELECT`, страница «думает». Три способа вылечить:

**1. JOIN FETCH в JPQL** — догружаем связь одним запросом:

```java
@Query("SELECT DISTINCT o FROM Order o JOIN FETCH o.items WHERE o.status = :status")
List<Order> findByStatusWithItems(@Param("status") OrderStatus status);   // 1 запрос с JOIN
```

`DISTINCT` нужен, чтобы из-за JOIN строки заказа не задублировались по числу позиций.

**2. @EntityGraph** — декларативно говорим, что догрузить, не переписывая запрос:

```java
@EntityGraph(attributePaths = "items")     // ← «при этом методе подтяни items сразу»
List<Order> findByStatus(OrderStatus status);
```

**3. Batch fetching** — настройкой `spring.jpa.properties.hibernate.default_batch_fetch_size=50` Hibernate грузит lazy-коллекции пачками через `IN (...)`, превращая 101 запрос в 1 + 2–3.

| Способ | Когда удобен |
|--------|--------------|
| `JOIN FETCH` | точечный сложный запрос, контроль над SQL |
| `@EntityGraph` | переиспользуем derived-метод, не хотим писать JPQL |
| `batch_fetch_size` | глобальная страховка от N+1 по всему приложению |

## ⚠️ Почему @ManyToMany часто стоит заменить промежуточной сущностью

`@ManyToMany` выглядит элегантно: соединил `Book` и `Author` — Hibernate сам сделает join-таблицу. Но как только связи понадобятся *собственные атрибуты* — всё рушится.

```java
// Наивно: «у заказа много товаров, у товара много заказов»
@ManyToMany
private List<Product> products;
```

А где хранить количество товара в заказе? Цену на момент покупки? В join-таблицу `@ManyToMany` свои поля добавить нельзя. ✅ Решение — превратить связь в **отдельную сущность** (`@OneToMany` + `@ManyToOne` с двух сторон):

```java
@Entity
public class OrderItem {                       // ← промежуточная сущность вместо @ManyToMany
    @Id @GeneratedValue private Long id;

    @ManyToOne(fetch = FetchType.LAZY) private Order order;
    @ManyToOne(fetch = FetchType.LAZY) private Product product;

    private int quantity;                      // ← атрибуты самой связи
    private BigDecimal priceAtPurchase;        //    которым негде жить в @ManyToMany
}
```

Бонусом вы получаете контроль над генерацией id связи и избавляетесь от сюрпризов `@ManyToMany` с удалением/перезаписью всей коллекции. Практический совет: используйте `@ManyToMany` только для совсем «чистых» связей-тегов без атрибутов — во всех остальных случаях берите промежуточную сущность.

## Заключение

**Что изучено:**
- Четыре типа связей; `@OneToMany`/`@ManyToOne` — две стороны одной связи.
- Владелец держит `@JoinColumn`, обратная сторона — `mappedBy`; FK обновляет владелец.
- `LAZY` против `EAGER`: `*ToOne` по умолчанию EAGER — переопределяйте на LAZY.
- `CascadeType` и `orphanRemoval` каскадируют операции на связанные сущности.
- N+1 лечится `JOIN FETCH`, `@EntityGraph` или `batch_fetch_size`.
- `@ManyToMany` без атрибутов — ок; со своими полями — промежуточная сущность.

**Как применять на практике:**
- Ставьте `fetch = FetchType.LAZY` на все `@ManyToOne`/`@OneToOne` руками.
- Заведите helper-метод, синхронизирующий обе стороны связи.
- При обходе коллекций в цикле — заранее догружайте через `JOIN FETCH`/`@EntityGraph`.

**Что дальше:** схема таблиц под все эти связи должна как-то создаваться и эволюционировать — переходим к версионированию БД через Liquibase.

[⬅ Сущности и репозитории](02-entities-and-repositories.md) · [📑 Оглавление модуля](README.md) · [Версионирование базы данных с Liquibase ➡](04-liquibase-migrations.md)
