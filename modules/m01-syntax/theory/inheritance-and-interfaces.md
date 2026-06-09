# Наследование, интерфейсы и полиморфизм

> Модуль 1 — Синтаксис и ООП · [к модулю](../../../course/01-syntax.md)

## Зачем это нужно

Полиморфизм позволяет писать код, который работает с **абстракцией**, а не с конкретным классом.
Это основа гибкой архитектуры: можно подменять реализации (в т.ч. в тестах) и расширять систему,
не переписывая существующий код.

**Где применяется в проектах:** интерфейсы репозиториев (`UserRepository` ← in-memory или JDBC);
стратегии (разные способы оплаты/доставки); плагины и расширения; всё, что мокается в тестах.

## Интерфейс — контракт

Интерфейс описывает *что* объект умеет, не говоря *как*.

```java
public interface Notifier {
    void send(String to, String message);   // контракт: «умею отправлять уведомление»
}

public final class EmailNotifier implements Notifier {
    @Override
    public void send(String to, String message) {
        System.out.println("EMAIL -> " + to + ": " + message);
    }
}

public final class SmsNotifier implements Notifier {
    @Override
    public void send(String to, String message) {
        System.out.println("SMS -> " + to + ": " + message);
    }
}
```

`@Override` — аннотация, которая просит компилятор проверить, что метод действительно переопределяет
контракт (защита от опечаток в сигнатуре).

## Полиморфизм: один код — разные реализации

```java
void notifyUser(Notifier notifier) {       // принимаем АБСТРАКЦИЮ, а не конкретный класс
    notifier.send("user@example.com", "Привет!");
}

notifyUser(new EmailNotifier());           // EMAIL -> ...
notifyUser(new SmsNotifier());             // SMS   -> ...
```

`notifyUser` не знает и не хочет знать, какой именно нотификатор пришёл. Это **«принимай интерфейсы»** —
ключевой приём для тестируемости: в тест можно передать заглушку.

## Наследование классов и `abstract`

Когда у реализаций есть общий код, его выносят в абстрактный класс:

```java
public abstract class BaseRepository<T> {
    protected final List<T> storage = new ArrayList<>();

    public void save(T item) { storage.add(item); }      // общая реализация
    public int count() { return storage.size(); }

    public abstract Optional<T> findById(long id);       // каждый наследник реализует по-своему
}
```

Правило: **«предпочитай композицию наследованию»**. Наследование уместно при настоящем отношении
«является» (is-a) и общем поведении; в остальных случаях гибче интерфейсы + композиция.

## default-методы интерфейсов

Интерфейс может дать реализацию по умолчанию — удобно расширять контракт, не ломая существующие классы:

```java
public interface Notifier {
    void send(String to, String message);

    default void broadcast(List<String> recipients, String message) {
        recipients.forEach(to -> send(to, message));     // переиспользует send
    }
}
```

## Связь с домашкой модуля

Enum `Direction` (задание 5) — простой пример полиморфизма поведения: метод `turnRight()`
возвращает следующую сторону света. А идею «принимай интерфейс» ты увидишь в модуле 10
([EventBus](../../m10-architecture/homework/src/main/java/com/javaroadmap/m10/homework/Mod10Homework.java))
и в репозиториях модуля 05.

## Итог

**Что изучено:**
- Интерфейс — контракт; `implements` + `@Override` — его реализация.
- Полиморфизм: код работает с абстракцией, реализации взаимозаменяемы.
- `abstract`-класс выносит общий код; default-методы расширяют контракт безопасно.

**Как применять на практике:**
- Объявлять зависимости как интерфейсы (`Repository`, `Notifier`) — это даёт тестируемость и гибкость.
- Подменять реализации (in-memory в тестах, реальные в проде) без изменения вызывающего кода.
- Не строить глубокие иерархии наследования — предпочитать интерфейсы и композицию.
