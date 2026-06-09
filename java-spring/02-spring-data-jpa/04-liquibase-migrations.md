# Лекция: Версионирование базы данных с Liquibase

> Java Spring · Модуль 2 — Spring Data JPA и базы данных · [⬅ К содержанию трека](../README.md)

## Введение

Код вы версионируете через git — каждый коммит фиксирует изменение, историю видно, можно откатиться. А что со схемой базы данных? Если один разработчик добавил колонку у себя локально, второй про неё не знает, а на проде её вообще нет — вы получаете три разные базы и веселье при деплое.

Представьте Liquibase как git для структуры БД: каждое изменение схемы — это отдельный «коммит» (changeset), они идут по порядку, применяются ровно один раз и записываются в журнал. Давайте разберёмся, почему автогенерация схемы Hibernate — это ловушка, и как навести порядок миграциями.

## ⚠️ Почему ddl-auto=update в проде — это зло

У Hibernate есть соблазнительная настройка `spring.jpa.hibernate.ddl-auto`, которая умеет создавать и менять таблицы по вашим сущностям.

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update   # ❌ НИКОГДА в проде
```

Почему `update` опасен на проде:

- **Только добавляет, не удаляет и не меняет.** Переименовали поле — Hibernate создаст новую колонку и оставит старую; уменьшили длину — проигнорирует.
- **Непредсказуем.** Итоговый SQL зависит от версии Hibernate и диалекта — вы не контролируете, что именно выполнится на боевой БД.
- **Нет отката и истории.** Что-то сломалось — откатить нечем, журнала изменений нет.
- **Опасно для данных.** В худших сценариях (`create`, `create-drop`) таблицы пересоздаются — данные теряются.

| Значение `ddl-auto` | Что делает | Где допустимо |
|---------------------|------------|----------------|
| `none` | ничего | ✅ прод (схему ведёт Liquibase) |
| `validate` | сверяет сущности со схемой, не меняет | ✅ прод (отличная страховка) |
| `update` | досоздаёт недостающее | ⚠️ только локально «на поиграть» |
| `create` / `create-drop` | пересоздаёт схему | ❌ только тесты/прототипы |

✅ Правило: схему ведёт инструмент миграций (Liquibase), а Hibernate ставим в `validate` — пусть проверяет, что сущности и таблицы совпадают.

## Liquibase или Flyway

Два главных инструмента миграций в Java-мире. Оба решают одну задачу, но по-разному.

| Критерий | Liquibase | Flyway |
|----------|-----------|--------|
| Формат миграций | XML, YAML, JSON, SQL | в основном SQL (+ Java) |
| Откат (`rollback`) | ✅ встроенный, в т.ч. авто | только в платной версии |
| Абстракция от СУБД | ✅ один changelog на разные БД | ❌ SQL обычно под конкретную БД |
| Кривая входа | чуть круче (свой DSL) | проще (просто SQL-файлы) |
| Контроль над SQL | через `sql`/`<sql>` changeset | максимальный (везде SQL) |

Грубо: **Flyway** — если вы любите чистый SQL и одну СУБД; **Liquibase** — если нужна переносимость между базами и встроенный откат. В этой лекции берём Liquibase. (Сам выбор «ORM или сырой SQL» для запросов — отдельная тема, к ней вернёмся в [лекции 6](06-jdbc-template-vs-hibernate.md).)

## Changelog и changeset

Две главные сущности Liquibase:

- **changeset** — одно атомарное изменение схемы (создать таблицу, добавить колонку). У него есть `id` и `author` — вместе они уникально идентифицируют изменение.
- **changelog** — упорядоченный список changeset'ов (или ссылок на файлы с ними). Это «лог коммитов» вашей схемы.

Liquibase ведёт служебную таблицу `DATABASECHANGELOG`: применил changeset — записал туда его id+author+контрольную сумму. При следующем запуске он пропускает уже применённые и накатывает только новые. Поэтому миграции **идемпотентны**: запусти приложение хоть десять раз — изменения применятся ровно один раз.

## Пример changeset: создаём таблицу и добавляем колонку

Liquibase поддерживает несколько форматов. Покажем два самых частых.

**Вариант 1 — XML** (классика, максимум возможностей и переносимость):

```xml
<!-- db/changelog/changes/001-create-orders.xml -->
<databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
            http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-latest.xsd">

    <changeSet id="001-create-orders" author="ivanov">   <!-- ← id + author = уникальный ключ -->
        <createTable tableName="orders">
            <column name="id" type="BIGINT" autoIncrement="true">
                <constraints primaryKey="true" nullable="false"/>
            </column>
            <column name="order_number" type="VARCHAR(32)">
                <constraints nullable="false" unique="true"/>
            </column>
            <column name="status" type="VARCHAR(20)"/>
        </createTable>
    </changeSet>
</databaseChangeLog>
```

**Вариант 2 — YAML** (компактнее; добавляем колонку в уже существующую таблицу):

```yaml
# db/changelog/changes/002-add-total.yaml
databaseChangeLog:
  - changeSet:
      id: 002-add-total-column        # ← НЕ переиспользуем id из предыдущего changeset
      author: ivanov
      changes:
        - addColumn:
            tableName: orders
            columns:
              - column:
                  name: total
                  type: NUMERIC(12,2)
                  defaultValueNumeric: 0    # ← дефолт, чтобы не упасть на NOT NULL у старых строк
```

Контекст: XML переносим между базами (Liquibase сам подберёт тип под диалект), YAML читается легче, а для совсем нестандартного DDL всегда можно вставить `sql`-changeset с сырым SQL. Формат — дело вкуса команды, главное — единообразие.

## Откат изменений: rollback

Сильная сторона Liquibase — откат. Для многих операций (`createTable`, `addColumn`) он генерируется **автоматически**: откат `createTable` — это `dropTable`. Для нестандартных изменений откат описывают вручную:

```xml
<changeSet id="003-rename-status" author="ivanov">
    <renameColumn tableName="orders" oldColumnName="status" newColumnName="order_status"/>
    <rollback>                                  <!-- ← как откатить ЭТОТ changeset -->
        <renameColumn tableName="orders" oldColumnName="order_status" newColumnName="status"/>
    </rollback>
</changeSet>
```

Откат запускают командой, указав, до какой точки вернуться:

```bash
liquibase rollbackCount 1          # откатить последний применённый changeset
liquibase rollbackToDate 2026-06-01  # откатить всё, что накатили после даты
```

✅ Привычка описывать `rollback` для рискованных изменений превращает «упс, сломали прод» в «откатились за минуту».

## Интеграция со Spring Boot

Spring Boot обнаруживает Liquibase на classpath и применяет миграции **автоматически при старте** приложения — до того, как Hibernate проверит схему.

```yaml
# application.yml
spring:
  liquibase:
    enabled: true
    change-log: classpath:db/changelog/db.changelog-master.yaml   # ← корневой changelog
  jpa:
    hibernate:
      ddl-auto: validate     # ← Liquibase создаёт схему, Hibernate только проверяет
```

Корневой changelog обычно лишь подключает файлы по порядку — так история не превращается в один гигантский файл:

```yaml
# db/changelog/db.changelog-master.yaml
databaseChangeLog:
  - include: { file: db/changelog/changes/001-create-orders.xml }
  - include: { file: db/changelog/changes/002-add-total.yaml }
  - include: { file: db/changelog/changes/003-rename-status.xml }
```

Зависимость подключается одной строкой; версия централизованно лежит в version catalog проекта (`gradle/libs.versions.toml`):

```kotlin
dependencies {
    implementation("org.liquibase:liquibase-core")   // версию подтянет Spring Boot BOM
}
```

## Best practices

Несколько правил, которые экономят команде нервы:

- ✅ **Один changeset = одно логическое изменение.** Так откат точечный, а в `DATABASECHANGELOG` видно историю по шагам.
- ❌ **Никогда не редактируйте уже применённый changeset.** Liquibase хранит его контрольную сумму (checksum); правка «на месте» сломает запуск с ошибкой checksum на тех базах, где changeset уже накатан. Нужна правка — пишите **новый** changeset.
- ✅ **Уникальные `id` + `author`.** Дубли приводят к конфликтам при мерже веток.
- ✅ **Откат для рискованных изменений** описывайте сразу, а не «когда понадобится».
- ✅ **Бэкап перед накатом на прод** для разрушающих операций (`dropColumn`, `dropTable`) — Liquibase не страховка от потери данных, а инструмент управления изменениями.

## Заключение

**Что изучено:**
- `ddl-auto=update` в проде опасен: не удаляет/не меняет, непредсказуем, без отката.
- Liquibase против Flyway: Liquibase даёт встроенный rollback и переносимость, Flyway — простоту SQL.
- changeset (атомарное изменение) + changelog (упорядоченный список); журнал в `DATABASECHANGELOG`.
- Форматы XML/YAML/SQL; `rollback` авто- или ручной.
- Spring Boot применяет миграции при старте; Hibernate ставим в `validate`.

**Как применять на практике:**
- На любом серьёзном проекте: `ddl-auto: validate` + Liquibase ведёт схему.
- Дробите изменения на маленькие changeset'ы и **никогда** не правьте применённые.
- Для `drop`/`rename` сразу пишите `rollback` и делайте бэкап перед продом.

**Что дальше:** схема под контролем — заглянем под капот Hibernate: персистентный контекст, состояния сущности и откуда берётся `LazyInitializationException`.

[⬅ Связи между сущностями](03-entity-relationships.md) · [📑 Оглавление модуля](README.md) · [Hibernate изнутри: сущности и их особенности ➡](05-hibernate-internals.md)
