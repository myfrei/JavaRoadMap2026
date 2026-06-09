# Лекция: Spring Data JPA: репозитории из коробки

> Java Spring · Модуль 2 — Spring Data JPA и базы данных · [⬅ К содержанию трека](../README.md)

## Введение

Представьте, что вы устроились в кафе официантом, и вам выдали блокнот, в котором уже распечатаны бланки заказов: «принять», «найти по столику», «отменить». Вам не нужно каждый раз заново чертить таблички — вы просто заполняете готовое. Именно это делает **Spring Data JPA**: вы объявляете *интерфейс* репозитория, а Spring сам пишет за вас реализацию с методами `save`, `findById`, `findAll`, `delete`.

Вы наверняка уже писали (или видели) классический DAO на чистом JDBC. Давайте честно вспомним, как это выглядит, и почему от такого кода хочется сбежать.

## Боль ручного DAO на JDBC

Чтобы достать один заказ по id, на голом JDBC приходится написать примерно вот столько:

```java
public Order findById(Long id) {
    String sql = "SELECT id, status FROM orders WHERE id = ?";
    try (Connection conn = dataSource.getConnection();           // ← открыли соединение вручную
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setLong(1, id);                                       // ← биндинг параметра по индексу
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                Order order = new Order();
                order.setId(rs.getLong("id"));                   // ← маппинг колонок в поля руками
                order.setStatus(rs.getString("status"));
                return order;
            }
            return null;
        }
    } catch (SQLException e) {                                   // ← checked-исключение на каждый чих
        throw new RuntimeException("Не удалось загрузить заказ", e);
    }
}
```

❌ Что здесь плохо:

- Половина строк — инфраструктура (открыть/закрыть соединение, поймать `SQLException`), а не бизнес-логика.
- Маппинг `ResultSet → Order` дублируется в каждом методе.
- Для `findAll`, `save`, `update`, `delete` всё это копируется снова и снова — это и есть **boilerplate**.

А теперь то же самое на Spring Data JPA — целиком:

```java
public interface OrderRepository extends JpaRepository<Order, Long> { }
```

✅ Одна строка — и у вас есть `findById`, `findAll`, `save`, `deleteById` и ещё пара десятков методов. Реализацию Spring сгенерирует в рантайме сам.

## Что вообще даёт Spring Data JPA

Spring Data JPA — это «надстройка» над JPA/Hibernate. Сам JPA умеет маппить объекты на таблицы, но писать запросы и управлять `EntityManager` вам бы пришлось вручную. Spring Data забирает эту рутину:

- **CRUD из коробки** — стандартные операции без единой строки реализации.
- **Derived queries** — методы-запросы, которые Spring собирает по имени метода.
- **`@Query`** — когда нужен свой JPQL или нативный SQL.
- **Пагинация и сортировка** через `Pageable` / `Sort`.
- Единая иерархия исключений (`DataAccessException`) поверх `SQLException`.

## Иерархия интерфейсов репозитория

Spring Data — это лестница интерфейсов: чем ниже спускаетесь, тем больше готовых методов получаете.

```
Repository<T, ID>                 ← корневой маркер, методов нет
        ▲
CrudRepository<T, ID>             ← save / findById / findAll / delete / count
        ▲
PagingAndSortingRepository<T,ID>  ← + findAll(Pageable) / findAll(Sort)
        ▲
JpaRepository<T, ID>              ← + saveAll / flush / saveAndFlush / deleteAllInBatch
```

В 95% случаев вы наследуете `JpaRepository` — он включает всё, что выше.

| Интерфейс | Что добавляет | Когда брать |
|-----------|---------------|-------------|
| `Repository<T, ID>` | Ничего (маркер) | Нужен «голый» контракт без лишних методов |
| `CrudRepository<T, ID>` | `save`, `findById`, `findAll`, `delete`, `count` | Простой CRUD без пагинации |
| `PagingAndSortingRepository<T, ID>` | `findAll(Pageable)`, `findAll(Sort)` | Нужны страницы и сортировка |
| `JpaRepository<T, ID>` | Батч-операции, `flush`, `getReferenceById` | Дефолтный выбор в Spring-проектах |

## Derived query methods — запрос из имени метода

Самая «магическая» фишка: вы пишете имя метода, а Spring парсит его и собирает запрос. Достаточно, чтобы имена полей в методе совпадали с полями сущности.

```java
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByStatus(OrderStatus status);                 // WHERE status = ?
    List<Order> findByStatusAndTotalGreaterThan(OrderStatus s,    // WHERE status = ? AND total > ?
                                                BigDecimal total);
    Optional<Order> findFirstByCustomerIdOrderByCreatedAtDesc(    // самый свежий заказ клиента
                                                Long customerId);
    boolean existsByNumber(String number);                        // SELECT count(*) > 0 ...
    long countByStatus(OrderStatus status);                       // SELECT count(*) ...
}
```

Разберём по косточкам: префикс (`findBy`, `existsBy`, `countBy`, `deleteBy`) задаёт тип операции, дальше идут имена полей, соединённые `And`/`Or`, а суффиксы (`GreaterThan`, `Like`, `Between`, `OrderBy...Desc`) уточняют условие. Имя метода — это и есть запрос.

⚠️ Не увлекайтесь: метод вроде `findByStatusAndCustomerNameAndCreatedAtBetweenOrderByTotalDesc` читается тяжело. Как только имя становится «простынёй» — переходите на `@Query`.

## @Query — когда имени мало

Если запрос сложный, пишем его явно. По умолчанию это **JPQL** — язык запросов JPA, который оперирует *сущностями и их полями*, а не таблицами и колонками.

```java
public interface OrderRepository extends JpaRepository<Order, Long> {

    // JPQL: обращаемся к сущности Order и полю customer.id, а не к таблице orders
    @Query("SELECT o FROM Order o WHERE o.customer.id = :customerId AND o.status = :status")
    List<Order> findActiveByCustomer(@Param("customerId") Long customerId,
                                     @Param("status") OrderStatus status);

    // Нативный SQL: пишем настоящий SQL под конкретную СУБД
    @Query(value = "SELECT * FROM orders WHERE total > :min ORDER BY total DESC",
           nativeQuery = true)                                    // ← флаг «это сырой SQL»
    List<Order> findExpensive(@Param("min") BigDecimal min);
}
```

Ключевая разница: в JPQL `Order` — это **класс-сущность**, а в `nativeQuery = true` `orders` — это **имя таблицы** в БД. JPQL переносимее между базами; нативный SQL даёт доступ к специфичным фичам СУБД (оконные функции, хинты). Подробнее про связку JPA ↔ Hibernate — в [лекции 5](05-hibernate-internals.md).

## Пагинация и сортировка: Pageable и Sort

Никогда не тащите из таблицы «всё». Для постраничной выдачи передайте в метод `Pageable`:

```java
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);   // ← страница вместо списка
}

// В сервисе:
Pageable firstPage = PageRequest.of(0, 20, Sort.by("createdAt").descending());
Page<Order> page = orderRepository.findByStatus(OrderStatus.NEW, firstPage);

page.getContent();        // 20 заказов текущей страницы
page.getTotalElements();  // всего записей по фильтру
page.getTotalPages();     // всего страниц
```

✅ `Page` сам делает два запроса — на данные и на `count`, так что вы сразу знаете общее число страниц. Если `count` не нужен (бесконечная лента) — верните `Slice` вместо `Page` и сэкономите один запрос.

## Заключение

**Что изучено:**
- Ручной DAO на JDBC — это горы boilerplate: соединения, `SQLException`, маппинг руками.
- Spring Data JPA генерирует реализацию репозитория по интерфейсу.
- Иерархия `Repository → CrudRepository → PagingAndSortingRepository → JpaRepository`; обычно берём `JpaRepository`.
- Derived query methods собирают запрос из имени; `@Query` (JPQL или `nativeQuery=true`) — для сложных случаев.
- `Pageable` и `Sort` — для страниц и сортировки.

**Как применять на практике:**
- Стартуйте с `interface ... extends JpaRepository<T, ID>` и derived-методов — этого хватает для большинства CRUD.
- Имя метода стало нечитаемым — переходите на `@Query`.
- Любую выдачу списков отдавайте через `Pageable`, а не `findAll()`.

**Что дальше:** разберёмся, из чего состоит сама сущность — аннотации, генерация id и почему `equals`/`hashCode` нельзя строить на id.

[⬅ К треку](../README.md) · [📑 Оглавление модуля](README.md) · [Сущности и репозитории ➡](02-entities-and-repositories.md)
