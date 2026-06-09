# Лекция: Возможности синхронизации

> Java Spring · Модуль 7 — Многопоточность в Java · [⬅ К содержанию трека](../README.md)

## Введение

Представьте общую тетрадь, куда десять человек одновременно записывают баланс счёта. Один прочитал
«100», другой тоже прочитал «100», оба прибавили 50 и записали «150». Где-то потерялись 50 рублей —
их перезаписали. Именно это происходит с общими данными в многопоточной программе без синхронизации.

Синхронизация решает две беды сразу: **гонки** (race conditions — кто последний записал, того и
тапки) и **видимость** (один поток не видит изменений другого из-за кэшей процессора). Давайте
разберём весь арсенал `java.util.concurrent` — от тяжёлого `synchronized` до lock-free атомиков — и
поймём, когда что брать.

## Почему данные «ломаются»

```java
class Counter {
    private int value = 0;
    void increment() { value++; }    // ← НЕ атомарно: read -> +1 -> write (3 шага)
    int get() { return value; }
}
```

Если 8 потоков по 1000 раз позовут `increment()`, итог почти наверняка будет **меньше** 8000:
потоки читают одно и то же значение и затирают чужие обновления. Вторая проблема — даже корректно
записанное значение другой поток может **не увидеть**, потому что оно осело в кэше ядра. Обе беды
лечатся инструментами ниже.

## synchronized: монитор и его цена

Каждый объект в Java имеет встроенный **монитор** (mutex). `synchronized` пускает в защищённый код
только один поток, а заодно даёт видимость (happens-before на входе/выходе):

```java
class SafeCounter {
    private int value = 0;
    synchronized void increment() { value++; }   // ← монитор this, взаимное исключение
    synchronized int get() { return value; }     // ← и видимость гарантирована
}
```

✅ Просто и надёжно. ❌ Минусы: поток **блокируется** в ожидании (состояние BLOCKED), нельзя задать
таймаут или отменить ожидание, нет «честности» (fairness), и блокировка слишком грубая — весь метод
под одним замком. Для горячих участков это узкое место.

## volatile: только видимость, не атомарность

`volatile` гарантирует, что запись в поле сразу видна всем потокам (значение не кэшируется в ядре).
Идеально для **флагов**:

```java
private volatile boolean running = true;   // ← запись видна всем потокам немедленно
public void stop() { running = false; }
public void loop() { while (running) { /* работа */ } }
```

⚠️ `volatile` **не делает операции атомарными**. `value++` остаётся небезопасным даже с `volatile`,
потому что это всё ещё три шага. Запомните правило: `volatile` — для видимости одиночных
записей/чтений, но не для составных операций «прочитать-изменить-записать».

## Atomic*: lock-free через CAS

Для счётчиков и флагов вместо блокировок берут атомики из `java.util.concurrent.atomic`. Внутри —
аппаратная инструкция **CAS** (compare-and-swap): «запиши новое значение, только если текущее всё
ещё равно ожидаемому». Без блокировки потока:

```java
import java.util.concurrent.atomic.AtomicInteger;

AtomicInteger value = new AtomicInteger(0);
value.incrementAndGet();          // ← атомарно, lock-free; после 8×1000 -> ровно 8000

// CAS-операция явно:
value.compareAndSet(5, 6);        // ← поставить 6, только если сейчас 5
```

✅ Быстрее `synchronized` при высокой конкуренции за один счётчик. Для «горячих» счётчиков под
большой нагрузкой есть `LongAdder` — он распределяет инкременты по нескольким ячейкам и обгоняет
`AtomicLong`.

## ReentrantLock: гибкая замена synchronized

`ReentrantLock` делает то же, что `synchronized`, но с управлением: таймаут на захват, прерываемое
ожидание и «честность». Главное правило — **всегда** освобождать lock в `finally`:

```java
import java.util.concurrent.locks.ReentrantLock;

ReentrantLock lock = new ReentrantLock();   // new ReentrantLock(true) -> fair (FIFO)

lock.lock();
try {
    // критическая секция
} finally {
    lock.unlock();                          // ← ИНАЧЕ deadlock на следующем заходе
}

// неблокирующая попытка с таймаутом:
if (lock.tryLock(1, java.util.concurrent.TimeUnit.SECONDS)) {
    try { /* ... */ } finally { lock.unlock(); }
} else {
    // не дождались — делаем что-то ещё, не зависаем навсегда
}
```

| Критерий | `synchronized` | `ReentrantLock` |
|----------|----------------|------------------|
| Освобождение | Авто (на выходе из блока) | Вручную в `finally` |
| Таймаут / `tryLock` | ❌ Нет | ✅ Есть |
| Прерываемое ожидание | ❌ Нет | ✅ `lockInterruptibly()` |
| Честность (fairness) | ❌ Нет | ✅ Опционально |
| `Condition` (несколько очередей ожидания) | Один `wait`/`notify` | ✅ Много `Condition` |
| Простота | ✅ Проще | Сложнее, легко забыть `unlock` |

`Condition` заменяет `wait/notify` и позволяет иметь **несколько** очередей ожидания на одном
замке — например, отдельно «не пусто» и «не полно» в очереди.

## ReadWriteLock: много читателей, один писатель

Если данные читают часто, а пишут редко, общий замок избыточен. `ReadWriteLock` пускает **много
читателей одновременно**, но писателя — эксклюзивно:

```java
import java.util.concurrent.locks.ReentrantReadWriteLock;

var rw = new ReentrantReadWriteLock();
var read = rw.readLock();
var write = rw.writeLock();

read.lock();   try { /* читают параллельно много потоков */ } finally { read.unlock(); }
write.lock();  try { /* пишет ровно один, читатели ждут */ } finally { write.unlock(); }
```

## Semaphore, CountDownLatch, CyclicBarrier

`Semaphore` ограничивает число потоков, одновременно входящих в ресурс (например, пул из 3
соединений):

```java
import java.util.concurrent.Semaphore;

Semaphore permits = new Semaphore(3);   // ← максимум 3 потока одновременно
permits.acquire();                       // ← занять разрешение (ждёт, если их нет)
try { /* работа с ограниченным ресурсом */ } finally { permits.release(); }
```

`CountDownLatch` — «дождаться, пока N событий случится» (одноразовый): главный поток ждёт, пока
воркеры досчитают счётчик до нуля.

```java
import java.util.concurrent.CountDownLatch;

CountDownLatch ready = new CountDownLatch(3);
// в каждом воркере по завершении: ready.countDown();
ready.await();   // ← главный поток ждёт все 3, затем продолжает
```

`CyclicBarrier` — «все собрались в точке, поехали дальше», и его, в отличие от latch, можно
**переиспользовать** циклически (по фазам).

## Конкурентные коллекции

`ArrayList` и `HashMap` **не потокобезопасны** — параллельная запись их портит (а у `HashMap` в
старых JDK могла зациклить). Вместо ручных замков берите готовые конкурентные коллекции:

```java
import java.util.concurrent.ConcurrentHashMap;

var cache = new ConcurrentHashMap<String, Integer>();
cache.merge("hits", 1, Integer::sum);   // ← атомарно: «прибавь 1 или положи 1»
```

✅ `ConcurrentHashMap` сегментирует блокировки и масштабируется на много потоков. Для очередей —
`ConcurrentLinkedQueue`, `LinkedBlockingQueue` (с блокирующими `put`/`take`); для списка с редкой
записью — `CopyOnWriteArrayList`.

## Что брать в каждом случае

| Задача | Инструмент |
|--------|------------|
| Флаг «работаем / стоп» | `volatile boolean` |
| Счётчик / накопитель | `AtomicInteger` / `LongAdder` |
| Защитить блок логики | `synchronized` (просто) или `ReentrantLock` (гибко) |
| Нужен таймаут на захват | `ReentrantLock.tryLock(...)` |
| Часто читаем, редко пишем | `ReadWriteLock` |
| Ограничить число потоков на ресурс | `Semaphore` |
| Дождаться завершения N задач | `CountDownLatch` |
| Общая map / queue / list | `ConcurrentHashMap` / `BlockingQueue` / `CopyOnWriteArrayList` |

## Заключение

**Что изучено:**
- Две корневые проблемы общих данных: гонки и видимость.
- `synchronized` (монитор) и его цена; `volatile` — только видимость, не атомарность.
- `Atomic*` и CAS как lock-free путь для счётчиков.
- `ReentrantLock` (tryLock, fairness, `Condition`), `ReadWriteLock`, `Semaphore`,
  `CountDownLatch`/`CyclicBarrier`.
- Конкурентные коллекции вместо ручной синхронизации.

**Как применять на практике:**
- Начинайте с простого: флаг — `volatile`, счётчик — `Atomic*`, map — `ConcurrentHashMap`.
- К `synchronized`/`ReentrantLock` прибегайте, когда защищаете составную инвариантную логику.
- Берите `tryLock` с таймаутом, чтобы поток не завис навсегда.

**Что дальше:** перестанем создавать потоки руками и доверим их пулам — `ExecutorService` и
`ForkJoinPool`.

### Полезные ссылки
- [Oracle Java Tutorials — Synchronization](https://docs.oracle.com/javase/tutorial/essential/concurrency/sync.html)
- [Baeldung — Guide to java.util.concurrent.Locks](https://www.baeldung.com/java-concurrent-locks)
- [Oracle Javadoc — package java.util.concurrent.atomic](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/atomic/package-summary.html)

---

[⬅ Thread, Runnable и Callable](01-thread-runnable-callable.md) · [📑 Оглавление модуля](README.md) · [ExecutorService vs ForkJoin ➡](03-executorservice-vs-forkjoin.md)
