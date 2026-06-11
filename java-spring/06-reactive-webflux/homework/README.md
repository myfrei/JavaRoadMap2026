# Домашка модуля 6 — Реактивный стек

> Статьи модуля: [оглавление](../README.md) · Примеры кода: [`src/main/java/...`](../src/main/java/com/javaroadmap/spring/s06/)

Заготовки лежат в [`homework/src/main/java/.../homework/`](src/main/java/com/javaroadmap/spring/s06/homework/).
Сделай «красные» тесты ([`Mod06HomeworkReactorTest`](src/test/java/com/javaroadmap/spring/s06/homework/Mod06HomeworkReactorTest.java) —
чистый Reactor, [`Mod06HomeworkWebTest`](src/test/java/com/javaroadmap/spring/s06/homework/Mod06HomeworkWebTest.java) —
R2DBC + WebFlux) зелёными. **Файлы с тестами трогать не нужно.**
Главное правило: **никаких block()** — только операторы.

## Как работать

1. Открой заготовку задания, прочитай TODO и формулировку ниже.
2. Реализуй задания по порядку: сначала операторы, потом интеграция.
3. После каждого задания запускай проверку:
   ```bash
   ./gradlew :java-spring:06-reactive-webflux:homeworkTest
   ```
4. Цель — **зелёный** `homeworkTest`. Общий `./gradlew build` остаётся зелёным независимо от домашки.

## Задания (по порядку)

### 1. Базовые операторы (статья 02)

`ReactorKata`: `greet`, `topN`, `sumOfPositive`

**Задача.** Собери конвейеры: `map` для приветствия, `sort` + `take` для топ-N,
`filter` + `reduce` для суммы положительных (пустой поток → 0).

**Ожидаемый результат.** StepVerifier видит ровно ожидаемые элементы и `onComplete`;
ни один метод не блокируется.

### 2. Ошибки и время (статьи 02, 06)

`ReactorKata`: `priceOrDefault`, `ticker`

**Задача.** `priceOrDefault` — ошибка источника не выходит наружу, вместо неё запасное
значение (`onErrorReturn`). `ticker` — `interval(period)` → `"tick-1..3"` → завершение.

**Ожидаемый результат.** Тест тикера проходит мгновенно при периоде в 10 минут —
благодаря `StepVerifier.withVirtualTime`. Пойми, почему без виртуального времени
такой тест шёл бы полчаса.

### 3. Реактивная композиция над R2DBC (статья 04)

`ProductSearchService`

**Задача.** Сделай класс бином и реализуй: `affordable(maxCents)` — товары не дороже
порога, отсортированные по цене (derived query репозитория + `sort`); `totalValueCents` —
сумма цен всех товаров (`map` + `reduce`), пусто → 0.

**Ожидаемый результат.** На сиде теста (120 ₽, 350 ₽, 999 ₽): `affordable(400_00)` →
Мышь, Клавиатура; `totalValueCents` → 146900.

### 4. Functional endpoint (статья 03)

`HwRouter`

**Задача.** Объяви `RouterFunction`-бин: `GET /hw/ping` → `{"pong": true}`
(образец — `ProductRouter`).

**Ожидаемый результат.** `WebTestClient` получает 200 и JSON с `pong: true`.

## 🎓 Навыки после модуля

- Мыслишь **конвейерами**: собираешь поведение из операторов, не блокируя поток.
- Обрабатываешь **ошибки внутри потока** (`onErrorReturn`/`onErrorResume`) — без try/catch.
- Тестируешь реактивный код **StepVerifier-ом**, включая время — через `withVirtualTime`.
- Работаешь с **R2DBC**: реактивные derived queries и композиция `Flux`/`Mono` над ними.
- Пишешь **functional endpoints** и понимаешь, чем они отличаются от аннотационных контроллеров.
- Понимаешь **backpressure**: кто управляет спросом и что делать, когда источник быстрее
  потребителя (разобрано в примерах: `BackpressureTest`).

## Готово, когда

- [ ] `./gradlew :java-spring:06-reactive-webflux:homeworkTest` зелёный (все задания).
- [ ] Решение закоммичено в свой репозиторий/ветку.

---

[📚 К модулю](../README.md) · [← Домашка 5](../../05-testing-kafka/homework/README.md) · [К треку Java Spring](../../README.md)
