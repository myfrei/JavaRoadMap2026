# SQL из Java: JDBC, PreparedStatement и пагинация

> Модуль 22 — PostgreSQL · [к модулю](../../../course/22-postgres.md)

## Зачем это нужно

Java общается с БД через JDBC. Делать это правильно — значит не открыть SQL-инъекцию, не утечь
соединения и не положить БД тяжёлой пагинацией. Эти базовые вещи нужны в каждом сервисе, даже если
поверх стоит Hibernate или Spring Data.

**Где применяется в проектах:** репозитории и DAO; отчёты и выгрузки; миграции; всё, что ходит в БД.

## PreparedStatement против инъекций

Никогда не склеивай значения в SQL строкой. Параметры (`?`) передаются отдельно от текста запроса —
это и безопасно (нет инъекции), и быстрее (план запроса переиспользуется):

```java
// ❌ ОПАСНО: конкатенация -> SQL-инъекция
String bad = "SELECT * FROM users WHERE name = '" + input + "'";

// ✅ ПРАВИЛЬНО: параметризованный запрос
String sql = "SELECT id, name FROM users WHERE city = ? AND age > ?";
try (var ps = connection.prepareStatement(sql)) {
    ps.setString(1, "NYC");      // значения подставляются драйвером, не в текст
    ps.setInt(2, 18);
    try (var rs = ps.executeQuery()) {
        while (rs.next()) {
            System.out.println(rs.getLong("id") + " " + rs.getString("name"));
        }
    }
}
```

`try-with-resources` гарантирует закрытие `PreparedStatement` и `ResultSet` даже при исключении —
иначе утекут ресурсы и пул соединений исчерпается.

## Составление запроса из Java

Динамический SQL собирают аккуратно: текст — из управляемых частей (имена колонок), значения —
всегда через `?`:

```java
String buildSelect(String table, List<String> cols, List<String> whereCols) {
    String select = cols.isEmpty() ? "*" : String.join(", ", cols);
    String sql = "SELECT " + select + " FROM " + table;
    if (!whereCols.isEmpty()) {
        String where = whereCols.stream()
                .map(c -> c + " = ?")               // плейсхолдеры, НЕ значения
                .collect(Collectors.joining(" AND "));
        sql += " WHERE " + where;
    }
    return sql;
}
// buildSelect("users", ["id","name"], ["age","city"])
//   -> "SELECT id, name FROM users WHERE age = ? AND city = ?"
```

## Keyset-пагинация вместо OFFSET

`OFFSET 100000 LIMIT 20` заставляет БД прочитать и выбросить 100000 строк — на больших страницах это
медленно. **Keyset** («seek») использует индекс и последнее увиденное значение:

```java
String keysetPage(String table, String keyColumn, int limit) {
    return "SELECT * FROM " + table
            + " WHERE " + keyColumn + " > ?"        // от последнего id предыдущей страницы
            + " ORDER BY " + keyColumn + " ASC LIMIT " + limit;
}
```

Каждая страница — быстрый index scan по диапазону, независимо от её номера.

## Транзакции

Связанные изменения оборачивают в транзакцию: либо всё, либо ничего.

```java
connection.setAutoCommit(false);
try {
    // несколько INSERT/UPDATE
    connection.commit();
} catch (SQLException e) {
    connection.rollback();   // откат при ошибке
    throw e;
}
```

## Связь с домашкой модуля

В [`Mod22Homework`](../homework/src/main/java/com/javaroadmap/m22/homework/Mod22Homework.java):
`buildSelect` — сборка `SELECT … WHERE … = ?` с плейсхолдерами; `keysetPage` — keyset-пагинация.
Оба задания учат «строить SQL из Java, не подставляя значения в текст».

## Итог

**Что изучено:**
- `PreparedStatement` с `?` защищает от инъекций и переиспользует план.
- `try-with-resources` для `Statement`/`ResultSet`/`Connection` — без утечек.
- Динамический SQL: имена — управляемо, значения — только через `?`.
- Keyset-пагинация быстрее `OFFSET`; транзакции для атомарности.

**Как применять на практике:**
- Всегда параметризовать запросы; никогда не склеивать значения строкой.
- Закрывать ресурсы через try-with-resources (или пул, см. HikariCP в модуле 05).
- Для бесконечных лент и больших выгрузок брать keyset-пагинацию вместо `OFFSET`.
