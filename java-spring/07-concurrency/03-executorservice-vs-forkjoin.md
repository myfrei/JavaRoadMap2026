# Лекция: ExecutorService vs ForkJoin

> Java Spring · Модуль 7 — Многопоточность в Java · [⬅ К содержанию трека](../README.md)

## Введение

В прошлых лекциях мы запускали потоки руками: `new Thread(...).start()`. Это как нанимать нового
курьера ради каждой посылки, а после доставки увольнять. Дорого и неуправляемо: создание потока
стоит около мегабайта стека и времени, а тысяча одновременных потоков положит сервер.

Решение — **пул потоков**: держим бригаду готовых работников и раздаём им задачи из очереди. В Java
два мира пулов: универсальный `ExecutorService` (для независимых задач — обработка запросов,
I/O) и `ForkJoinPool` (для рекурсивного распараллеливания одной большой задачи). Давайте разберём
оба и поймём, где чей дом.

## Зачем нужны пулы

Пул решает три задачи: **переиспользует** потоки (не создаём заново на каждую задачу),
**ограничивает** их количество (защита от перегрузки) и **развязывает** отправку задачи и её
исполнение через очередь.

```java
import java.util.concurrent.*;

ExecutorService pool = Executors.newFixedThreadPool(4);   // ← бригада из 4 потоков

for (int i = 0; i < 100; i++) {
    int id = i;
    pool.submit(() -> System.out.println("Задача " + id));  // ← 100 задач, 4 потока
}
```

Сто задач выполнят всего четыре потока, разбирая их из очереди по мере освобождения.

## ExecutorService: submit, invokeAll и аккуратное закрытие

`submit` возвращает `Future`, `invokeAll` запускает пачку `Callable` и ждёт всех. Самое важное —
**корректно закрыть** пул, иначе JVM не завершится (потоки пула — не daemon):

```java
ExecutorService pool = Executors.newFixedThreadPool(4);

List<Callable<Integer>> tasks = List.of(
    () -> 1, () -> 2, () -> 3
);
List<Future<Integer>> results = pool.invokeAll(tasks);   // ← ждёт завершения всех
for (Future<Integer> f : results) System.out.println(f.get());

pool.shutdown();                                          // ← перестать принимать новые
if (!pool.awaitTermination(10, TimeUnit.SECONDS)) {       // ← дождаться текущих
    pool.shutdownNow();                                   // ← не дождались — прервать
}
```

✅ Паттерн graceful shutdown: `shutdown()` (запрет новых задач) → `awaitTermination(timeout)` →
`shutdownNow()` (если не успели). ❌ Забыть `shutdown()` — частая причина «зависшего» приложения.

> С Java 21 `ExecutorService` реализует `AutoCloseable`, поэтому в простых случаях можно писать
> `try (var pool = Executors.newFixedThreadPool(4)) { ... }` — `close()` сам сделает `shutdown()` и
> дождётся задач.

## Почему Executors.newFixedThreadPool опасен в проде

Фабрики `Executors.newFixedThreadPool` и `newSingleThreadExecutor` используют **неограниченную**
очередь (`LinkedBlockingQueue` без лимита). Под наплывом задач очередь растёт, пока приложение не
упадёт с `OutOfMemoryError`. А `newCachedThreadPool` может создать **неограниченное число потоков**.
Поэтому в продакшене создают `ThreadPoolExecutor` явно — с **ограниченной** очередью и понятной
политикой отказа:

```java
ThreadPoolExecutor pool = new ThreadPoolExecutor(
    4,                                  // corePoolSize — базовое число потоков
    8,                                  // maximumPoolSize — максимум под пиком
    60, TimeUnit.SECONDS,               // keepAlive — простой лишних потоков до их удаления
    new ArrayBlockingQueue<>(1000),     // ← ОГРАНИЧЕННАЯ очередь, не растёт бесконечно
    new ThreadPoolExecutor.CallerRunsPolicy()   // ← переполнение: задачу выполнит сам вызывающий
);
```

Параметры пула:
- **corePoolSize** — сколько потоков держим всегда (даже простаивающих).
- **maximumPoolSize** — до скольки разрастаемся, когда очередь полна.
- **queue** — буфер задач между core и max; **ограниченный**, чтобы создавать backpressure.
- **rejection policy** — что делать при переполнении: `AbortPolicy` (бросить исключение, по
  умолчанию), `CallerRunsPolicy` (нагрузить отправителя — мягко тормозит входящий поток),
  `DiscardPolicy`/`DiscardOldestPolicy` (выбросить задачу).

## ForkJoinPool: work-stealing и divide-and-conquer

`ForkJoinPool` создан для **одной большой задачи**, которую можно рекурсивно разбить на подзадачи
(divide-and-conquer): отсортировать массив, просуммировать дерево. Его фишка — **work-stealing**:
простаивающий поток «крадёт» подзадачи из очереди занятого, балансируя нагрузку.

```java
import java.util.concurrent.RecursiveTask;

class SumTask extends RecursiveTask<Long> {     // ← задача, возвращающая Long
    private static final int THRESHOLD = 1000;
    private final long[] arr; private final int lo, hi;
    SumTask(long[] arr, int lo, int hi) { this.arr = arr; this.lo = lo; this.hi = hi; }

    @Override protected Long compute() {
        if (hi - lo <= THRESHOLD) {             // ← база: считаем напрямую
            long s = 0; for (int i = lo; i < hi; i++) s += arr[i]; return s;
        }
        int mid = (lo + hi) >>> 1;
        SumTask left  = new SumTask(arr, lo, mid);
        SumTask right = new SumTask(arr, mid, hi);
        left.fork();                            // ← левую — асинхронно в пул
        long r = right.compute();               // ← правую считаем здесь (экономим поток)
        return left.join() + r;                 // ← дождались левую, сложили
    }
}

long total = ForkJoinPool.commonPool().invoke(new SumTask(data, 0, data.length));
```

⚠️ Правильный порядок — `left.fork()` → `right.compute()` → `left.join()`. Если сделать
`fork()` обеим и сразу `join()` левой, текущий поток будет простаивать вместо работы.

## Common pool и parallelStream

В JVM есть один общий `ForkJoinPool.commonPool()` — его же использует `parallelStream()`:

```java
long sum = java.util.stream.LongStream.rangeClosed(1, 1_000_000)
    .parallel()                  // ← выполнится в commonPool
    .sum();
```

⚠️ Общий пул один на всю JVM. Не запускайте в `parallelStream` **блокирующие** операции (сетевые
вызовы, I/O) — вы займёте потоки общего пула, и остальной параллельный код в приложении «встанет».
Для блокирующих задач — отдельный `ExecutorService` (а ещё лучше — виртуальные потоки из лекции 4).

## ExecutorService против ForkJoinPool

| Критерий | `ExecutorService` (ThreadPoolExecutor) | `ForkJoinPool` |
|----------|----------------------------------------|----------------|
| Тип задач | Независимые (запросы, I/O, batch) | Рекурсивно делимая одна задача |
| Балансировка | Общая очередь | Work-stealing (своя дека у потока) |
| API задачи | `Runnable` / `Callable` | `RecursiveTask` / `RecursiveAction` |
| Очередь | Настраиваемая (лучше ограниченная) | Внутренние деки потоков |
| Кто использует | Вы создаёте под нагрузку | `parallelStream`, `CompletableFuture` |
| Блокирующие задачи | ✅ Можно (свой пул) | ❌ Нежелательно (займёт common pool) |

## Заключение

**Что изучено:**
- Зачем пулы: переиспользование потоков, ограничение их числа, развязка через очередь.
- `ExecutorService`: `submit`/`invokeAll` и graceful `shutdown` → `awaitTermination` →
  `shutdownNow`.
- Почему `Executors.newFixedThreadPool` рискован (unbounded queue) и как настроить
  `ThreadPoolExecutor` (core/max/queue/rejection).
- `ForkJoinPool`, work-stealing и `RecursiveTask` для divide-and-conquer.
- Common pool, `parallelStream` и опасность блокирующих операций в нём.

**Как применять на практике:**
- Для серверной нагрузки создавайте явный `ThreadPoolExecutor` с ограниченной очередью и политикой
  отказа.
- Всегда закрывайте пул (`shutdown` + `awaitTermination`) или используйте try-with-resources.
- `ForkJoinPool`/`parallelStream` — для CPU-bound делимых задач, не для блокирующего I/O.

**Что дальше:** научимся **компоновать** асинхронные задачи без блокирующего `get()` — и встретим
виртуальные потоки.

### Полезные ссылки
- [Oracle Javadoc — ThreadPoolExecutor](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/ThreadPoolExecutor.html)
- [Oracle Javadoc — ForkJoinPool](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/ForkJoinPool.html)
- [Baeldung — Guide to the Fork/Join Framework](https://www.baeldung.com/java-fork-join)

---

[⬅ Возможности синхронизации](02-synchronization.md) · [📑 Оглавление модуля](README.md) · [Future, CompletableFuture или виртуальный поток? ➡](04-future-completablefuture-virtual.md)
