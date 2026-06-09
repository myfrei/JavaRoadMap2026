# JPA, Hibernate и проблема N+1

> Модуль 5 — Базы данных · [к модулю](../../../course/05-databases.md)

## Зачем это нужно

ORM (Object-Relational Mapping) убирает рутину: вместо ручного маппинга `ResultSet` в объекты ты
описываешь сущности, а Hibernate генерирует SQL. Это ускоряет разработку, но прячет SQL — и без
понимания, какие запросы реально уходят в БД, легко получить проблему N+1 и положить сервис.

**Где применяется в проектах:** большинство Spring-приложений (Spring Data JPA); CRUD над доменными
сущностями; быстрая разработка слоя данных.

## Сущность и репозиторий

```java
@Entity
@Table(name = "orders")
public class Order {
    @Id @GeneratedValue
    private Long id;
    private String status;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<Item> items;            // связь «один-ко-многим»
}

// Spring Data: интерфейс -> готовая реализация CRUD
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatus(String status);   // SQL генерируется по имени метода
}
```

`JpaRepository` даёт `save`/`findById`/`findAll`/`delete` бесплатно, а методы по соглашению об
именах превращаются в запросы.

## Проблема N+1

```java
List<Order> orders = orderRepo.findByStatus("NEW");  // 1 запрос: SELECT * FROM orders ...
for (Order o : orders) {
    o.getItems().size();                             // +1 запрос на КАЖДЫЙ заказ (lazy load)
}
// 100 заказов -> 1 + 100 = 101 запрос вместо 1-2. Это N+1.
```

Симптом: страница «тормозит», в логах сотни одинаковых мелких `SELECT`. Лечение — загрузить связь
одним запросом:

```java
@Query("SELECT o FROM Order o JOIN FETCH o.items WHERE o.status = :status")
List<Order> findWithItems(String status);            // один запрос с JOIN
```

## Lazy vs Eager

- `LAZY` — связь грузится при первом обращении (по умолчанию для коллекций). Экономно, но рискует N+1.
- `EAGER` — грузится сразу. Удобно, но тащит лишнее и тоже плодит запросы.

Практика: держать `LAZY` и осознанно догружать через `JOIN FETCH`/EntityGraph там, где нужно.

## ORM не отменяет знание SQL

Hibernate генерирует SQL, но отвечаешь за него ты. Включай показ SQL в dev-режиме и смотри, что
реально уходит в БД — иначе «удобный» код порождает катастрофу запросов.

## Связь с кодом модуля

Обобщённый `InMemoryRepository` из
[homework](../homework/src/main/java/com/javaroadmap/m05/homework/Mod05Homework.java) — это
упрощённая модель того, что делает `JpaRepository` (CRUD по id). А `pageOf` — идея пагинации,
которую в JPA даёт `Pageable`.

## Итог

**Что изучено:**
- JPA/Hibernate маппят сущности на таблицы; Spring Data даёт CRUD «из коробки».
- Проблема N+1: ленивые связи в цикле → лавина запросов; лечится `JOIN FETCH`.
- `LAZY` vs `EAGER`; ORM не освобождает от контроля над SQL.

**Как применять на практике:**
- Начинать с `JpaRepository`, но всегда смотреть сгенерированный SQL в dev.
- Держать связи `LAZY` и догружать пачкой (`JOIN FETCH`/EntityGraph) под конкретный сценарий.
- Для тяжёлых отчётов не бояться писать нативный SQL вместо ORM (см. модуль 22).
