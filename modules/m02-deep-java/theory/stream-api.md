# Stream API и лямбды

> Модуль 2 — Глубокая Java · [к модулю](../../../course/02-deep-java.md)

## Зачем это нужно

Stream API превращает обработку коллекций в декларативный конвейер: «отфильтровать → преобразовать →
собрать» вместо ручных циклов с временными списками. Код становится короче и читается как описание
намерения, а не как механика.

**Где применяется в проектах:** трансформации данных из БД/API; агрегации и отчёты; фильтрация и
группировка; подготовка DTO к ответу.

## Лямбда и функциональный интерфейс

Лямбда — это краткая запись реализации интерфейса с одним методом.

```java
// было: анонимный класс
Comparator<String> byLen1 = new Comparator<>() {
    public int compare(String a, String b) { return a.length() - b.length(); }
};
// стало: лямбда
Comparator<String> byLen2 = (a, b) -> a.length() - b.length();
```

## Конвейер: filter → map → collect

```java
List<String> words = List.of("alpha", "to", "stream", "of", "java");

List<String> result = words.stream()
        .filter(w -> w.length() > 2)      // промежуточная операция: отбор
        .map(String::toUpperCase)         // промежуточная: преобразование (method reference)
        .sorted()                          // промежуточная: сортировка
        .toList();                         // ТЕРМИНАЛЬНАЯ: запускает конвейер
// [ALPHA, JAVA, STREAM]
```

Ключевое:
- Промежуточные операции (`filter`, `map`, `sorted`) **ленивые** — ничего не делают, пока нет
  терминальной (`toList`, `count`, `reduce`).
- `String::toUpperCase` — ссылка на метод, короткая форма лямбды `w -> w.toUpperCase()`.

## Агрегации: reduce, sum, collect

```java
int sumEven = List.of(1, 2, 3, 4, 5, 6).stream()
        .filter(n -> n % 2 == 0)
        .mapToInt(Integer::intValue)
        .sum();                            // 12

String joined = List.of("a", "b", "c").stream()
        .map(String::toUpperCase)
        .collect(Collectors.joining(",")); // "A,B,C"

Map<Integer, List<String>> byLen = Stream.of("a", "bb", "cc")
        .collect(Collectors.groupingBy(String::length)); // {1=[a], 2=[bb, cc]}
```

`Collectors` — фабрика «сборщиков»: `joining`, `groupingBy`, `toMap`, `counting` и др.

## Когда НЕ нужен стрим

Для простого обхода с побочным эффектом обычный `for` часто понятнее. Стримы — для
*трансформаций данных*, а не для всего подряд.

## Связь с домашкой модуля

В [`Mod02Homework`](../homework/src/main/java/com/javaroadmap/m02/homework/Mod02Homework.java):
`sumEven` — `filter().mapToInt().sum()`; `joinUpper` — `map().collect(joining(","))`;
`firstLongerThan` — `filter().findFirst()` (вернёт `Optional`).
Проверка: `./gradlew :modules:m02-deep-java:homeworkTest`.

## Итог

**Что изучено:**
- Лямбды и ссылки на методы как краткая запись поведения.
- Конвейер `filter → map → collect`; ленивость промежуточных операций.
- Агрегации: `sum`, `reduce`, `Collectors.joining/groupingBy/toMap`.

**Как применять на практике:**
- Превращать выборки из БД/API в нужные DTO декларативно.
- Группировать и считать статистику без временных коллекций и ручных циклов.
- Не злоупотреблять: для простого побочного действия брать обычный цикл (читаемость важнее моды).
