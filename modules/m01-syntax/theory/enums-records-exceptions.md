# Enum, record и исключения

> Модуль 1 — Синтаксис и ООП · [к модулю](../../../course/01-syntax.md)

## Зачем это нужно

Три современных инструмента, которые делают код короче и безопаснее: `enum` — для фиксированного
набора вариантов, `record` — для неизменяемых данных без шаблонного кода, исключения — для честной
обработки ошибок вместо «магических» кодов возврата.

**Где применяется в проектах:** статусы (`OrderStatus`, `Direction`), типы события; DTO и value-объекты
(`record`); обработка ошибок на границах (валидация, БД, сеть).

## Enum — это больше, чем константы

`enum` — полноценный класс с фиксированным набором экземпляров. У него могут быть поля и методы.

```java
public enum Direction {
    NORTH, EAST, SOUTH, WEST;

    public Direction turnRight() {
        // values() — массив всех констант; ordinal() — индекс текущей
        return values()[(ordinal() + 1) % 4];
    }
}

Direction d = Direction.NORTH.turnRight();   // EAST
```

Плюс: компилятор знает все варианты. В `switch` по enum он подскажет, если забыл случай:

```java
String hint = switch (d) {
    case NORTH -> "вверх";
    case EAST  -> "вправо";
    case SOUTH -> "вниз";
    case WEST  -> "влево";
};   // switch-expression возвращает значение
```

## Record — данные без шаблона

`record` за одну строку даёт неизменяемый класс с конструктором, геттерами, `equals`, `hashCode`
и `toString`.

```java
public record Money(long amount, String currency) {}

var a = new Money(100, "USD");
var b = new Money(100, "USD");
System.out.println(a.equals(b));   // true — equals по значениям, не по ссылке
System.out.println(a.amount());    // 100  — геттер называется как поле
```

Можно добавить валидацию через компактный конструктор:

```java
public record Money(long amount, String currency) {
    public Money {                                  // compact constructor
        if (amount < 0) throw new IllegalArgumentException("amount < 0");
    }
}
```

Раньше такой класс занимал 40+ строк — теперь 1–4.

## Исключения: checked vs unchecked

- **unchecked** (`RuntimeException` и наследники: `IllegalArgumentException`, `NullPointerException`) —
  ошибки программиста/состояния; не обязаны объявляться.
- **checked** (`IOException`, `SQLException`) — ожидаемые внешние сбои; компилятор требует обработать
  или объявить `throws`.

```java
public long parsePositive(String s) {
    long v = Long.parseLong(s);              // бросит NumberFormatException (unchecked)
    if (v < 0) {
        throw new IllegalArgumentException("must be >= 0");
    }
    return v;
}
```

## try-with-resources

Для ресурсов (файлы, соединения) — автозакрытие, даже при исключении:

```java
try (var reader = Files.newBufferedReader(Path.of("data.txt"))) {
    return reader.readLine();
}   // reader.close() вызовется автоматически
```

## Связь с домашкой модуля

В [`Mod01Homework`](../homework/src/main/java/com/javaroadmap/m01/homework/Mod01Homework.java):
`Direction.turnRight()` (задание 5) — enum с поведением; `factorial` (задание 2) кидает
`IllegalArgumentException` на отрицательный вход — unchecked-исключение для нарушения контракта.

## Итог

**Что изучено:**
- `enum` — типобезопасный набор вариантов с полями/методами и проверяемым `switch`.
- `record` — неизменяемые данные с автоматическими `equals`/`hashCode`/`toString`.
- Разница checked/unchecked; `try-with-resources` для автозакрытия.

**Как применять на практике:**
- Заменять «магические строки/числа» статусов на `enum`.
- Использовать `record` для DTO, value-объектов и результатов методов.
- Кидать `IllegalArgumentException`/`IllegalStateException` при нарушении контракта,
  а checked-исключения ловить там, где действительно можешь обработать.
