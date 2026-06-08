# Конкурентность в Java — примеры кода от простого к продвинутому

> Дополнение к [Модулю 3. Конкурентность](./03-concurrency.md). Запускаемые примеры по нарастающей
> сложности. Код-скелет: `modules/m03-concurrency/` (`./gradlew :modules:m03-concurrency:run`).

> ⚙️ **Статус: каркас.** Примеры добавляются по мере наполнения модуля. Каждый пример — отдельный
> запускаемый класс в `modules/m03-concurrency/src/main/java/com/javaroadmap/m03/`.

---

## Уровень 0 — Первые потоки

- Пример 0.1 — `Thread` и `Runnable` _(заполнить)_
- Пример 0.2 — несколько потоков и `join()` _(заполнить)_

## Уровень 1 — Синхронизация

- Пример 1.1 — гонка данных и `synchronized` _(заполнить)_
- Пример 1.2 — `volatile` и видимость, happens-before _(заполнить)_

## Уровень 2 — java.util.concurrent

- Пример 2.1 — `ReentrantLock`, `Semaphore`, `CountDownLatch` _(заполнить)_
- Пример 2.2 — `ConcurrentHashMap` и `BlockingQueue` _(заполнить)_

## Уровень 3 — Пулы и асинхронность

- Пример 3.1 — `ExecutorService` и `Future` _(заполнить)_
- Пример 3.2 — `CompletableFuture`: композиция и обработка ошибок _(заполнить)_

## Уровень 4 — Loom

- Пример 4.1 — virtual threads: тысячи задач дёшево _(заполнить)_
- Пример 4.2 — structured concurrency и `StructuredTaskScope` _(заполнить)_

---

[📚 К содержанию](../README.md) · [← Модуль 3. Конкурентность](./03-concurrency.md)
