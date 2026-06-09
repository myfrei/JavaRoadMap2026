# Паттерны проектирования (GoF)

> Модуль 10 — Архитектура · [к модулю](../../../course/10-architecture.md)

## Зачем это нужно

Паттерны — это проверенные решения повторяющихся задач проектирования и общий словарь: сказав
«здесь стратегия», ты передаёшь коллеге целую идею одним словом. Знание паттернов ускоряет
проектирование и чтение чужого кода.

**Где применяется в проектах:** обработка вариативного поведения (стратегия); подписки и события
(наблюдатель); создание объектов (фабрика); конвейеры обработки.

## Strategy — взаимозаменяемые алгоритмы

Выносим «как именно делать» в отдельную абстракцию, чтобы менять алгоритм на лету:

```java
@FunctionalInterface
interface Discount { double apply(double price); }

double price = 100;
Discount none    = p -> p;
Discount percent = p -> p * 0.9;
Discount half    = p -> p * 0.5;

System.out.println(percent.apply(price));   // 90.0 — поведение передано как параметр
```

В Java стратегию часто и не выделяют в классы — хватает лямбды/функционального интерфейса.

## Observer — подписка на события

Объект уведомляет подписчиков об изменениях, не зная, кто они:

```java
class EventBus<T> {
    private final List<Consumer<T>> subscribers = new ArrayList<>();
    void subscribe(Consumer<T> handler) { subscribers.add(handler); }
    void publish(T event) { subscribers.forEach(h -> h.accept(event)); }
}

var bus = new EventBus<String>();
bus.subscribe(msg -> System.out.println("LOG: " + msg));
bus.subscribe(msg -> audit(msg));
bus.publish("user registered");             // получат оба подписчика
```

Издатель развязан с подписчиками — основа событийных систем (и Kafka на большом масштабе).

## Factory — инкапсуляция создания

Прячем «как создаётся объект» за методом — клиент просит результат, не зная деталей:

```java
static Notifier create(String channel) {
    return switch (channel) {
        case "email" -> new EmailNotifier();
        case "sms"   -> new SmsNotifier();
        default -> throw new IllegalArgumentException("unknown: " + channel);
    };
}
```

## Decorator / pipeline — наслоение поведения

Оборачиваем поведение слоями или собираем конвейер преобразований:

```java
int result = List.<IntUnaryOperator>of(x -> x + 1, x -> x * 2)
        .stream().reduce(IntUnaryOperator.identity(), IntUnaryOperator::andThen)
        .applyAsInt(3);                     // (3+1)*2 = 8
```

## Не плоди паттерны ради паттернов

Паттерн — инструмент против конкретной боли (вариативность, связанность). Применять их «потому что
красиво» — это переусложнение. Сначала проблема, потом паттерн.

## Связь с кодом модуля

В [homework](../homework/src/main/java/com/javaroadmap/m10/homework/Mod10Homework.java):
`priceAfter` (Strategy), `EventBus` (Observer), `pipeline` (конвейер/Decorator) — ты реализуешь
именно эти паттерны.

## Итог

**Что изучено:**
- Strategy (взаимозаменяемые алгоритмы), Observer (подписка/события), Factory (создание),
  Decorator/pipeline (наслоение поведения).
- В Java многие паттерны выражаются лямбдами и функциональными интерфейсами.

**Как применять на практике:**
- Использовать паттерны как общий словарь и готовые решения, а не самоцель.
- Вариативное поведение задавать стратегиями/лямбдами; события — через наблюдателя.
- Применять паттерн, когда есть конкретная боль, а не «на всякий случай».
