# Дженерики и Optional

> Модуль 2 — Глубокая Java · [к модулю](../../../course/02-deep-java.md)

## Зачем это нужно

Дженерики дают **типобезопасность** без дублирования кода: один класс/метод работает с любым типом,
а компилятор ловит ошибки до запуска. `Optional` честно говорит «значение может отсутствовать» —
это лечит самую частую ошибку Java, `NullPointerException`.

**Где применяется в проектах:** репозитории и кэши (`Repository<T>`, `Cache<K,V>`); утилиты-обёртки;
возврат «может не найтись» из поиска (`findById -> Optional<User>`).

## Дженерик-класс

```java
public final class Box<T> {            // T — параметр типа
    private final T value;
    public Box(T value) { this.value = value; }
    public T get() { return value; }
}

Box<String> s = new Box<>("hi");
String v = s.get();                    // без приведения типов — компилятор знает, что это String
```

До дженериков пришлось бы хранить `Object` и приводить вручную (`(String) box.get()`) — с риском
`ClassCastException` в рантайме. Дженерики переносят эту проверку на этап компиляции.

## Дженерик-метод и границы (bounds)

```java
public static <T extends Comparable<T>> T max(List<T> items) {
    T best = items.get(0);
    for (T x : items) {
        if (x.compareTo(best) > 0) best = x;   // можно вызвать compareTo — гарантирует bound
    }
    return best;
}
```

`<T extends Comparable<T>>` — ограничение: тип обязан быть сравнимым, иначе метод не скомпилируется.

## Wildcards: ? extends / ? super

- `List<? extends Number>` — «читаю числа» (producer): можно брать, нельзя класть.
- `List<? super Integer>` — «кладу Integer» (consumer): можно класть, читать только как `Object`.

Мнемоника **PECS**: *Producer Extends, Consumer Super*.

## Optional вместо null

```java
public Optional<String> firstLongerThan(List<String> words, int len) {
    return words.stream()
            .filter(w -> w.length() > len)
            .findFirst();              // Optional<String>: либо значение, либо «пусто»
}

String shown = firstLongerThan(List.of("a", "bb"), 1)
        .map(String::toUpperCase)      // если есть — преобразуем
        .orElse("ничего");             // если нет — запасное значение
```

`Optional` заставляет явно обработать «отсутствие» через `map`/`orElse`/`ifPresent`, вместо того
чтобы забыть проверку на `null` и получить NPE.

> ⚠️ `Optional` хорош как **тип возврата** метода-поиска. Не делай из него поля и параметры —
> это анти-паттерн.

## Связь с домашкой модуля

`firstLongerThan` в [`Mod02Homework`](../homework/src/main/java/com/javaroadmap/m02/homework/Mod02Homework.java)
возвращает `Optional<String>`. А обобщённые `InMemoryRepository<K,V>` (модуль 05) и `LruCache<K,V>`
(модуль 11) — практическое применение дженериков.

## Итог

**Что изучено:**
- Дженерик-классы и методы дают типобезопасность без дублирования и приведений.
- Границы (`extends`) и wildcards (`? extends`/`? super`, правило PECS).
- `Optional` как явная замена `null` для «может отсутствовать».

**Как применять на практике:**
- Писать обобщённые репозитории/кэши/утилиты вместо копий под каждый тип.
- Возвращать `Optional<T>` из методов поиска; обрабатывать через `map`/`orElse`, а не разыменовывать.
- Не хранить `Optional` в полях и не передавать в параметрах.
