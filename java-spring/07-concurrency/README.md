# Модуль 7. Многопоточность в Java — оглавление

> Java Spring · набор лонгридов о многопоточности в **core Java** (без Spring): от первого потока до виртуальных потоков, Java Memory Model и охоты на конкурентные баги.

Формат каждой статьи — лекция-лонгрид: введение с аналогией, разделы от простого к сложному с кодом и разбором, сравнительные таблицы, ASCII-схемы и практическое заключение. Код — на Java 21 (виртуальные потоки уже стабильны), идиоматичный `java.util.concurrent`.

## Содержание

1. [Thread, Runnable и Callable](01-thread-runnable-callable.md) — процесс vs поток, создание потоков, жизненный цикл, `Future`.
2. [Возможности синхронизации](02-synchronization.md) — `synchronized`, `volatile`, `Atomic*`, `ReentrantLock`, `Semaphore`, конкурентные коллекции.
3. [ExecutorService vs ForkJoin](03-executorservice-vs-forkjoin.md) — пулы потоков, `ThreadPoolExecutor`, work-stealing, `RecursiveTask`.
4. [Future, CompletableFuture или виртуальный поток?](04-future-completablefuture-virtual.md) — композиция асинхронных задач и Project Loom.
5. [Java Memory Model и happens-before](05-java-memory-model.md) — reordering, видимость, safe publication, `final`.
6. [Типичные проблемы многопоточности](06-concurrency-problems.md) — deadlock, livelock, starvation, race condition, диагностика.

---

[📚 К треку Java Spring](../README.md)
