# ExecutorService и CompletableFuture

> Модуль 3 — Конкурентность · [к модулю](../../../course/03-concurrency.md)

## Зачем это нужно

Создавать потоки руками (`new Thread`) дорого и неуправляемо. Пулы потоков (`ExecutorService`)
переиспользуют потоки и ограничивают параллелизм, а `CompletableFuture` позволяет складывать
асинхронные операции в конвейер, не блокируя поток ожиданием.

**Где применяется в проектах:** параллельная обработка батчей; одновременные вызовы внешних сервисов
с последующим объединением; фоновые задачи; неблокирующие пайплайны.

## Пул потоков вместо ручных Thread

```java
import java.util.concurrent.*;

try (ExecutorService pool = Executors.newFixedThreadPool(4)) {
    Future<Integer> f = pool.submit(() -> 2 + 2);   // задача уходит в пул
    Integer result = f.get();                        // блокируемся, ждём результат -> 4
}   // try-with-resources закроет пул (Java 21+: close ждёт завершения задач)
```

- `submit` возвращает `Future` — «обещание результата».
- `Future.get()` блокирует поток до готовности.

## Параллельная сумма (split-map-reduce)

```java
static long parallelSum(int[] xs, int threads) throws Exception {
    try (ExecutorService pool = Executors.newFixedThreadPool(threads)) {
        int chunk = (int) Math.ceil(xs.length / (double) threads);
        List<Future<Long>> parts = new ArrayList<>();
        for (int i = 0; i < xs.length; i += chunk) {
            int from = i, to = Math.min(i + chunk, xs.length);
            parts.add(pool.submit(() -> {            // каждый поток суммирует свой кусок
                long s = 0;
                for (int j = from; j < to; j++) s += xs[j];
                return s;
            }));
        }
        long total = 0;
        for (Future<Long> p : parts) total += p.get();  // собираем частичные суммы
        return total;
    }
}
```

Идея: разбить работу на части → посчитать параллельно → сложить результаты.

## CompletableFuture: асинхронный конвейер

`CompletableFuture` позволяет описывать «что сделать с результатом», не блокируясь:

```java
CompletableFuture<Integer> price = CompletableFuture
        .supplyAsync(() -> fetchPrice("BTC"))     // асинхронно получить
        .thenApply(p -> p * 100)                  // преобразовать, когда будет готово
        .exceptionally(ex -> -1);                 // обработать ошибку

// объединить два независимых вызова
CompletableFuture<Integer> a = CompletableFuture.supplyAsync(() -> 10);
CompletableFuture<Integer> b = CompletableFuture.supplyAsync(() -> 20);
int sum = a.thenCombine(b, Integer::sum).join();  // 30, оба выполнялись параллельно
```

- `thenApply` — преобразование результата.
- `thenCombine` — объединение двух будущих значений.
- `exceptionally`/`handle` — обработка ошибок без try/catch вокруг `get`.

## Связь с домашкой модуля

В [`Mod03Homework`](../homework/src/main/java/com/javaroadmap/m03/homework/Mod03Homework.java):
`sumConcurrently` — паттерн split-map-reduce (как выше); `parallelSquares` — параллельное
преобразование с сохранением порядка (хорошо ложится на `CompletableFuture` + индексы).

## Итог

**Что изучено:**
- Пулы потоков переиспользуют потоки и ограничивают параллелизм; `Future` — обещание результата.
- Паттерн split-map-reduce для параллельных вычислений.
- `CompletableFuture` строит неблокирующие конвейеры (`thenApply`/`thenCombine`/`exceptionally`).

**Как применять на практике:**
- Не создавать потоки вручную — брать `ExecutorService` (или виртуальные потоки, см. соседнюю статью).
- Параллелить независимые вызовы внешних сервисов и объединять результаты через `CompletableFuture`.
- Всегда закрывать пул (try-with-resources) и обрабатывать ошибки явно.
