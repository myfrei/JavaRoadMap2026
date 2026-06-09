# java.time: даты, время и длительности

> Модуль 4 — Core APIs · [к модулю](../../../course/04-core-apis.md)

## Зачем это нужно

Работа с датами — источник классических багов: часовые пояса, переход на летнее время, «полночь
не наступила». Старые `Date`/`Calendar` были изменяемыми и неудобными. Современный `java.time`
(JSR-310) — неизменяемый, типобезопасный и понятный. Использовать его правильно — значит не ловить
«плавающие» баги с временем.

**Где применяется в проектах:** сроки и дедлайны; расписания; биллинг по периодам; аудит (когда
произошло событие); вычисление возраста, рабочих дней, TTL.

## Главные типы

| Тип | Что хранит |
|-----|-----------|
| `LocalDate` | дата без времени и зоны (2026-06-08) |
| `LocalTime` | время без даты (14:30) |
| `LocalDateTime` | дата + время, без зоны |
| `Instant` | момент на шкале UTC (для меток времени) |
| `ZonedDateTime` | дата+время с часовым поясом |
| `Duration` / `Period` | длительность (секунды) / период (дни-месяцы) |

## Создание и арифметика — без мутаций

```java
LocalDate today = LocalDate.of(2026, 6, 8);
LocalDate next = today.plusDays(10);     // НОВЫЙ объект; today не изменился
System.out.println(today.getDayOfWeek()); // MONDAY

boolean weekend = switch (today.getDayOfWeek()) {
    case SATURDAY, SUNDAY -> true;
    default -> false;
};
```

Все методы (`plusDays`, `withYear`, …) возвращают новый объект — `java.time` неизменяем, как `String`.

## Разница между датами

```java
long days = ChronoUnit.DAYS.between(
        LocalDate.of(2026, 1, 1),
        LocalDate.of(2026, 1, 11));      // 10

Duration d = Duration.ofSeconds(3661);
System.out.printf("%d:%02d:%02d%n",      // 1:01:01
        d.toHours(), d.toMinutesPart(), d.toSecondsPart());
```

`ChronoUnit.*.between` — для разницы в нужных единицах; `Duration` удобно раскладывать на части.

## Часовые пояса и Instant

Для меток времени событий храни `Instant` (UTC), а пояс применяй только при отображении:

```java
Instant now = Instant.now();                         // момент в UTC — так хранят в БД/логах
ZonedDateTime local = now.atZone(ZoneId.of("Europe/Moscow")); // для показа пользователю
```

Правило: **хранить в UTC, показывать в локальной зоне**.

## Связь с домашкой модуля

В [`Mod04Homework`](../homework/src/main/java/com/javaroadmap/m04/homework/Mod04Homework.java):
`daysBetween` — `ChronoUnit.DAYS.between`; `isWeekend` — `getDayOfWeek()` + `switch`;
`secondsToClock` — раскладка `Duration`/арифметика на `H:MM:SS`.

## Итог

**Что изучено:**
- `LocalDate`/`LocalDateTime`/`Instant`/`ZonedDateTime` и когда что брать.
- Неизменяемая арифметика дат; `ChronoUnit.between` и `Duration`.
- Принцип «хранить в UTC, показывать в зоне».

**Как применять на практике:**
- Для дедлайнов и расписаний — `LocalDate`/`LocalDateTime`; для меток событий — `Instant` (UTC).
- Никогда не считать даты «руками» (через миллисекунды) — есть `plus*`/`between`.
- Часовой пояс применять только на границе отображения, а не размазывать по логике.
