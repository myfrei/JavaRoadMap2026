# Collections Framework: List, Set, Map и выбор реализации

> Модуль 2 — Глубокая Java · [к модулю](../../../course/02-deep-java.md)

## Зачем это нужно

Коллекции — рабочая лошадка каждого приложения. Правильный выбор структуры данных — это разница
между O(1) и O(n) на каждой операции, то есть между быстрым и тормозящим сервисом. Понимать,
*когда* `ArrayList`, а когда `HashMap` или `HashSet`, важнее, чем знать их наизусть.

**Где применяется в проектах:** буквально везде — кэши, индексы, дедупликация, группировки, очереди
задач, хранение результатов запросов.

## Три семейства

| Интерфейс | Смысл | Частая реализация | Когда |
|-----------|-------|-------------------|-------|
| `List` | упорядоченный, дубликаты ок | `ArrayList` | индекс/итерация |
| `Set` | уникальные элементы | `HashSet` | дедуп, «есть ли» |
| `Map` | ключ → значение | `HashMap` | поиск по ключу |

## List — индекс и порядок

```java
List<String> names = new ArrayList<>();
names.add("Ann");
names.add("Bob");
System.out.println(names.get(0));   // Ann — доступ по индексу O(1)
System.out.println(names.size());   // 2
```

`ArrayList` — массив под капотом: быстрый доступ по индексу и итерация, дорогая вставка в середину.

## Set — уникальность и быстрая проверка

```java
Set<String> seen = new HashSet<>();
seen.add("a");
seen.add("a");                      // дубликат игнорируется
System.out.println(seen.size());    // 1
System.out.println(seen.contains("a"));  // true — проверка O(1) в среднем
```

`HashSet` идеален для дедупликации и ответа на вопрос «видели ли мы это уже».

## Map — поиск по ключу

```java
Map<String, Integer> ages = new HashMap<>();
ages.put("Ann", 30);
ages.getOrDefault("Bob", -1);       // -1, если ключа нет — без NPE
ages.computeIfAbsent("Bob", k -> 0);// удобно для «ленивой» инициализации

// группировка значений по ключу
Map<Character, List<String>> byFirst = new HashMap<>();
for (String w : List.of("apple", "avocado", "banana")) {
    byFirst.computeIfAbsent(w.charAt(0), k -> new ArrayList<>()).add(w);
}
// {a=[apple, avocado], b=[banana]}
```

`computeIfAbsent` убирает ручную проверку «если ключа нет — создать список».

## Неизменяемые коллекции

`List.of(...)`, `Set.of(...)`, `Map.of(...)` создают компактные **неизменяемые** коллекции —
отлично для констант и возврата данных, которые нельзя менять снаружи.

```java
List<Integer> primes = List.of(2, 3, 5, 7);   // попытка add(...) бросит UnsupportedOperationException
```

## Связь с домашкой модуля

В [`Mod02Homework`](../homework/src/main/java/com/javaroadmap/m02/homework/Mod02Homework.java):
`groupByFirstLetter` — `computeIfAbsent` (как выше); `distinctSorted` — уникальность через `Set`/стрим.
А обобщённый `InMemoryRepository` из модуля 05 — это `Map` под капотом.

## Итог

**Что изучено:**
- `List`/`Set`/`Map` и их назначение; `ArrayList`/`HashSet`/`HashMap` как реализации по умолчанию.
- `getOrDefault`, `computeIfAbsent`, `merge` — лаконичная работа с `Map`.
- Неизменяемые коллекции `*.of(...)`.

**Как применять на практике:**
- Выбирать структуру по операции: индекс → `List`, уникальность/проверка → `Set`, поиск по ключу → `Map`.
- Использовать `HashSet` для дедупликации и `HashMap` для индексов/кэшей в памяти.
- Возвращать неизменяемые коллекции из методов, чтобы вызывающий не сломал ваше состояние.
