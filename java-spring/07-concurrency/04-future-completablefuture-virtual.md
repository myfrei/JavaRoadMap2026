# Лекция: Future, CompletableFuture или виртуальный поток?

> Java Spring · Модуль 7 — Многопоточность в Java · [⬅ К содержанию трека](../README.md)

## Введение

Представьте, что вы заказали пиццу и получили на руки чек с номером заказа. Сам чек — не пицца, это
**обещание**: «позже по нему заберёшь готовое». Так работает `Future` в Java. Проблема в том, что
обычный `Future` умеет только одно — заставить вас стоять у окна выдачи (блокирующий `get()`), пока
готовят. А если нужно по готовности пиццы автоматически заказать напиток, а из двух заказов собрать
комбо — `Future` пожимает плечами.

В этой лекции мы пройдём эволюцию: от блокирующего `Future` к компонуемому `CompletableFuture`, а
затем к **виртуальным потокам** (Project Loom, стабильны с Java 21), которые перевернули подход к
конкурентности. Давайте разбираться.

## Ограничения обычного Future

`Future` из лекции 1 возвращает результат `Callable`, но его API беден:

```java
ExecutorService pool = Executors.newFixedThreadPool(2);
Future<Integer> f = pool.submit(() -> 6 * 7);

Integer result = f.get();    // ← БЛОКИРУЕМ поток до готовности. Это всё, что он умеет.
```

Чего нет у `Future`:
- ❌ Нельзя сказать «когда будет готово — сделай вот это» (нет колбэков).
- ❌ Нельзя соединить два `Future` в один результат (нет композиции).
- ❌ Обработка ошибок — только через `try/catch` вокруг `get()`.

Любое ожидание = заблокированный поток. Для цепочки из пяти зависимых вызовов это пять блокировок.

## CompletableFuture: компонуем асинхронность

`CompletableFuture` (с Java 8) — это `Future`, который умеет строить **конвейер** преобразований без
единого блокирующего `get()` в середине. Ключевые операции:

- `thenApply(fn)` — преобразовать результат (как `map`).
- `thenCompose(fn)` — выстроить **зависимые** асинхронные шаги (как `flatMap`).
- `thenCombine(other, fn)` — слить результаты **двух** независимых задач.
- `exceptionally(fn)` / `handle(fn)` — перехватить ошибку и подставить запасное значение.

```java
import java.util.concurrent.CompletableFuture;

CompletableFuture<String> pipeline =
    CompletableFuture.supplyAsync(() -> fetchUserId("alice"))   // ← старт в общем пуле
        .thenApply(id -> id.trim())                             // ← преобразовали
        .thenCompose(id -> loadProfileAsync(id))                // ← зависимый async-шаг
        .thenCombine(loadSettingsAsync(), (prof, set) -> prof + "/" + set)  // ← слили две
        .exceptionally(ex -> "default-profile");                // ← запасной путь при ошибке

String value = pipeline.join();   // ← блокируемся ОДИН раз, в самом конце
```

Дождаться **нескольких** задач сразу:

```java
CompletableFuture<Void> all = CompletableFuture.allOf(taskA, taskB, taskC);
all.join();   // ← ждём, пока завершатся все три
```

⚠️ Различайте `thenApply` и `thenApplyAsync`. Первый выполняет функцию в том потоке, что завершил
предыдущий шаг; второй отправляет её в пул (по умолчанию — `commonPool`, можно передать свой
`Executor`). Для блокирующих шагов используйте `*Async` со **своим** `Executor`, чтобы не занимать
общий пул.

## Виртуальные потоки: thread-per-task снова в моде

В лекции 1 мы предостерегали: «не создавайте потоки пачками». Причина — обычный (platform) поток
тяжёлый: он привязан 1:1 к потоку ОС и тратит ~1 МБ стека. Поэтому и появились пулы.

**Виртуальные потоки** (Project Loom, стабильны с **Java 21**, [JEP 444](https://openjdk.org/jeps/444))
меняют правила. Это сверхлёгкие потоки, которыми управляет JVM, а не ОС. Их можно создавать
**миллионами**. Когда виртуальный поток блокируется на I/O, JVM «снимает» (unmount) его с потока ОС
(carrier thread) и ставит туда другой — поток ОС не простаивает.

```java
// Один виртуальный поток:
Thread.startVirtualThread(() -> System.out.println("привет из виртуального потока"));

// thread-per-task: на КАЖДУЮ задачу — свой виртуальный поток. Снова можно!
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    for (int i = 0; i < 10_000; i++) {           // ← 10 000 потоков — это нормально
        executor.submit(() -> blockingHttpCall());   // блокирующий код — и это ОК
    }
}   // ← close() дождётся всех задач (try-with-resources)
```

✅ Революция в том, что **простой блокирующий код снова прост и масштабируем**. Не нужны
`CompletableFuture`-цепочки только ради того, чтобы не блокировать поток: пишите линейный код,
запускайте задачу-на-поток, а масштабирование берёт на себя JVM.

> **Structured concurrency** ([JEP 480](https://openjdk.org/jeps/480), preview в 21) идёт следом:
> `StructuredTaskScope` связывает время жизни параллельных подзадач с родительским блоком — если
> одна упала, остальные отменяются, и ничего не «утекает». Это превью-API, но за ним будущее
> разветвлённых задач.

## ⚠️ Pinning: где виртуальные потоки спотыкаются

У виртуальных потоков есть тонкость. Если поток заблокируется **внутри `synchronized`** или в
**нативном вызове (JNI)**, он «прибивается» (pinning) к несущему потоку ОС и не может его освободить.
Под высокой нагрузкой это сводит на нет всю экономию.

❌ Плохо для виртуального потока:
```java
synchronized (lock) {        // ← блокируясь здесь, virtual thread пиннит carrier thread
    blockingDatabaseCall();
}
```

✅ Замените монитор на `ReentrantLock` (из лекции 2) — он pinning не вызывает:
```java
lock.lock();
try {
    blockingDatabaseCall();  // ← carrier thread свободен для других виртуальных потоков
} finally {
    lock.unlock();
}
```

## Что выбрать

| Критерий | `Future` | `CompletableFuture` | Виртуальный поток |
|----------|----------|---------------------|-------------------|
| Появился | Java 5 | Java 8 | Java 21 (стабильно) |
| Композиция шагов | ❌ Нет | ✅ `thenApply`/`thenCompose`/`thenCombine` | ✅ Линейный код вместо цепочек |
| Обработка ошибок | `try/catch` вокруг `get` | ✅ `exceptionally`/`handle` | Обычный `try/catch` |
| Стиль кода | Блокирующий | Колбэки / конвейер | Прямой, последовательный |
| Цена при I/O | Блокирует поток ОС | Не блокирует (если async) | Не блокирует поток ОС |
| Когда брать | Простой одиночный результат | Сложный async-граф зависимостей | Массовый блокирующий I/O |

Практическое правило: **одна задача с результатом** — `Future`/`Callable`; **граф зависимых
асинхронных вычислений** — `CompletableFuture`; **тысячи блокирующих I/O-операций** (HTTP, БД) —
виртуальные потоки с thread-per-task.

## Заключение

**Что изучено:**
- Ограничения `Future`: блокирующий `get`, нет колбэков и композиции.
- `CompletableFuture`: конвейер через `thenApply`/`thenCompose`/`thenCombine`, `allOf`,
  `exceptionally`, разница sync/async-вариантов.
- Виртуальные потоки (Java 21, Loom): почему thread-per-task снова жизнеспособен и как работает
  unmount на блокировке.
- Structured concurrency как следующий шаг.
- Pinning на `synchronized`/JNI и обход через `ReentrantLock`.

**Как применять на практике:**
- Для зависимых асинхронных шагов стройте `CompletableFuture`-конвейер, блокируйтесь только в конце.
- Для блокирующего I/O в больших объёмах берите `newVirtualThreadPerTaskExecutor()` и пишите
  линейный код.
- В коде под виртуальные потоки заменяйте `synchronized` на `ReentrantLock`, чтобы избежать pinning.

**Что дальше:** заглянем под капот — почему вообще нужны happens-before и `volatile`, и как
устроена Java Memory Model.

### Полезные ссылки
- [Oracle Javadoc — CompletableFuture](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CompletableFuture.html)
- [JEP 444 — Virtual Threads](https://openjdk.org/jeps/444)
- [Baeldung — Guide to CompletableFuture](https://www.baeldung.com/java-completablefuture)

---

[⬅ ExecutorService vs ForkJoin](03-executorservice-vs-forkjoin.md) · [📑 Оглавление модуля](README.md) · [Java Memory Model и happens-before ➡](05-java-memory-model.md)
