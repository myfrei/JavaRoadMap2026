# 📋 TASKS — план наполнения трека Java Spring кодом

> Рабочий план: для каждого модуля `java-spring/NN-slug/` сделать **примеры кода**, **тесты**
> и папку **homework** (формулировка задачи + ожидаемый результат + приобретённые навыки).
> Выполненную задачу отмечай `[x]`. Порядок — сверху вниз, по модулям.

**Статусы:** `[ ]` — не начато · `[x]` — выполнено

**Структура кода модуля** (как в `modules/mNN-slug/`; зафиксировано в задаче 0.1):

```
java-spring/NN-slug/            Gradle-подпроект :java-spring:NN-slug (статьи лежат рядом)
  build.gradle.kts              зависимости модуля (Spring Boot BOM из version catalog)
  src/main/java/...             примеры кода по статьям; пакет com.javaroadmap.spring.sNN
  src/test/java/...             тесты к примерам
  homework/
    README.md                   формулировка задач + ожидаемый результат + навыки
    src/main/java/...           заготовки (методы кидают UnsupportedOperationException)
    src/test/java/...           «красные» тесты (red → green)
```

**Definition of Done для каждого модуля:**
- примеры кода покрывают темы статей модуля (по одной показательной демке на статью или связке статей);
- к примерам есть юнит-/интеграционные тесты, `./gradlew build` остаётся зелёным;
- `homework/README.md` содержит: формулировку каждой задачи, ожидаемый результат,
  раздел «🎓 Навыки после модуля»;
- homework — отдельный source set с задачей `homeworkTest` (красная до решения, не входит в `build`);
- готовые решения homework **не** выкладываются — только заготовки и тесты.

---

## 0. Инфраструктура (один раз, до модулей)

- [x] 0.1. Выбрать и зафиксировать схему подключения подпроектов трека в `settings.gradle.kts`
      — все 7 модулей включены как `java-spring:NN-slug`; `mainClass` для трека выводится как `com.javaroadmap.spring.sNN.Main`
- [x] 0.2. Добавить общий конфиг для подпроектов трека — в `gradle/libs.versions.toml` springBoot → 3.5.14,
      добавлены `spring-boot-bom` и стартеры (web, test); подключение через `platform(libs.spring.boot.bom)`
- [x] 0.3. Распространить механизм `homeworkTest` на подпроекты `java-spring` — работает без изменений:
      блок в корневом `build.gradle.kts` срабатывает на `homework/` в каталоге любого подпроекта
- [x] 0.4. Проверка: `./gradlew build` зелёный с каркасом первого модуля (Main + SmokeTest на `@SpringBootTest`)

---

## Модуль 1. IoC, DI и бины (`01-ioc-di-beans/`) 🟢🟡

- [x] 1.1. Примеры кода: IoC/DI (конструкторное внедрение), жизненный цикл бина (`@PostConstruct`/`@PreDestroy`, `BeanPostProcessor`), Java-конфигурация (`@Configuration`/`@Bean`), слой сервисов, простой контроллер, скоупы и wiring (`@Primary`, `@Qualifier`)
- [x] 1.2. Тесты: поднятие контекста (`@SpringBootTest`), проверка скоупов и порядка колбэков жизненного цикла, тест сервисного слоя с мок-зависимостью (+ `@WebMvcTest` контроллера)
- [x] 1.3. Homework: README с формулировками задач, ожидаемым результатом и навыками + заготовки + красные тесты (5 заданий / 7 тестов)
- [x] 1.4. Проверка модуля: `build` зелёный, `homeworkTest` красный (7/7 падают на отсутствии бинов); ссылки на код добавлены в `01-ioc-di-beans/README.md`

## Модуль 2. Spring Data JPA (`02-spring-data-jpa/`) 🟡

- [x] 2.1. Примеры кода: сущности и репозитории (derived queries, `@Query` + join fetch против N+1), связи `@OneToMany`/`@ManyToMany` (каскады, orphanRemoval), миграции Liquibase на H2 (`ddl-auto=validate`), отчёт через `JdbcTemplate`
- [x] 2.2. Тесты: `@DataJpaTest` на H2 (каскады, orphanRemoval, lazy + `LazyInitializationException`, join fetch), тест миграций (таблицы + сид), JPA и SQL в одной транзакции
- [x] 2.3. Homework: README (задачи / ожидаемый результат / навыки) + заготовка `Mod02Homework` + красные тесты (6 заданий)
- [x] 2.4. Проверка модуля: `build` зелёный, `homeworkTest` красный (6/6); ссылки в README модуля

## Модуль 3. MVC и контроллеры (`03-mvc-controllers/`) 🟡

- [x] 3.1. Примеры кода: REST-контроллер (CRUD, валидация, `@RestControllerAdvice`), контраст `@Controller`/`@RestController`, gRPC-сервис (in-process, контракт руками — без protobuf-кодогенерации), WebSocket-эхо
- [x] 3.2. Тесты: `@WebMvcTest` + MockMvc (статусы, JSON, валидация, advice, `handler()` — маршрутизация DispatcherServlet), gRPC через in-process канал, живой WebSocket-тест на случайном порту
- [x] 3.3. Homework: README (задачи / ожидаемый результат / навыки) + заготовки (контроллер «заметок», валидация, advice, WS-хендлер) + красные тесты (5 заданий / 7 тестов)
- [x] 3.4. Проверка модуля: `build` зелёный, `homeworkTest` красный (7/7); ссылки в README модуля

## Модуль 4. Конфигурация и безопасность (`04-config-security/`) 🟡🔴

- [x] 4.1. Примеры кода: `@ConfigurationProperties` + профиль dev, две security-цепочки (Basic `/api/**` + JWT resource server `/api/jwt/**`), роли и `@PreAuthorize`, CORS-конфиг, выпуск JWT (HS256) через `JwtEncoder`
- [x] 4.2. Тесты: биндинг конфигурации/профилей, 401 vs 403 (`httpBasic`), method security (`@WithMockUser`), preflight CORS (разрешённый/чужой origin), полный JWT-флоу + мусорный токен
- [x] 4.3. Homework: README (задачи / ожидаемый результат / навыки) + заготовки (properties, своя цепочка `/hw/**`, `@PreAuthorize`, CORS, выпуск JWT) + красные тесты (5 заданий / 8 тестов)
- [x] 4.4. Проверка модуля: `build` зелёный, `homeworkTest` красный (8/8); ссылки в README модуля

## Модуль 5. Тестирование и Kafka (`05-testing-kafka/`) 🟡🔴

- [x] 5.1. Примеры кода: продюсер на `KafkaTemplate` (JSON-серде, ключ = orderId), идемпотентный `@KafkaListener`, топики как код, ретраи + DLT (`DefaultErrorHandler` + `DeadLetterPublishingRecoverer`)
- [x] 5.2. Тесты: пирамида — юнит продюсера с моком + интеграционные на EmbeddedKafka (доставка JSON-события, идемпотентность при повторной доставке, «ядовитое» событие → 3 попытки → DLT)
- [x] 5.3. Homework: README (задачи / ожидаемый результат / навыки) + заготовки (валидатор-юнит, продюсер, листенер с фильтром и идемпотентностью, проекция потока) + красные тесты (4 задания / 7 тестов)
- [x] 5.4. Проверка модуля: `build` зелёный, `homeworkTest` красный (7/7); ссылки в README модуля

## Модуль 6. Реактивный стек (`06-reactive-webflux/`) 🔴

- [x] 6.1. Примеры кода: операторы Mono/Flux на потоке цен (среднее, фильтр выбросов, fallback, timeout, `onBackpressureLatest`), WebFlux-контроллер + functional endpoints, R2DBC-репозиторий на реактивном H2, `WebClient`
- [x] 6.2. Тесты: `StepVerifier` (+ `withVirtualTime`, `TestPublisher`), backpressure через `thenRequest`/некомплаентный источник, `@WebFluxTest` + `WebTestClient`, `@DataR2dbcTest`, интеграционный `WebClient` на живом Netty
- [x] 6.3. Homework: README (задачи / ожидаемый результат / навыки) + заготовки (Reactor-ката, ошибки и виртуальное время, композиция над R2DBC, functional endpoint) + красные тесты (4 задания / 8 тестов)
- [x] 6.4. Проверка модуля: `build` зелёный, `homeworkTest` красный (8/8); ссылки в README модуля

## Модуль 7. Многопоточность (`07-concurrency/`) 🔴

- [x] 7.1. Примеры кода: Thread/Runnable/Callable (`FutureTask`), три счётчика (гонка → `synchronized` → атомик), `ExecutorService` vs ForkJoin на одной задаче, `CompletableFuture` + виртуальные потоки, `volatile`-флаг (JMM), детерминированный дедлок + лекарство (порядок замков)
- [x] 7.2. Тесты: детерминированные (латчи/рандеву, без `sleep`-магии), контенция счётчиков, композиция `CompletableFuture`, дедлок детектируется `ThreadMXBean`, инвариант сумм при встречных переводах
- [x] 7.3. Homework: README (задачи / ожидаемый результат / навыки) + заготовки (SafeCounter, ParallelMapper, AsyncCombiner, firstOf, VirtualBatch) + красные тесты (5 заданий / 6 тестов)
- [x] 7.4. Проверка модуля: `build` зелёный, `homeworkTest` красный (6/6); ссылки в README модуля

---

## Финал

- [x] F.1. Обновить `java-spring/README.md`: добавлена колонка «Код и домашка» в таблицу программы + команды запуска в «Как проходить»
- [x] F.2. Прогон всего: `./gradlew clean build` зелёный (59s), все 7 задач `homeworkTest` на месте, каждая проверена красной при закрытии своего модуля
- [x] F.3. Паритет сверен: каждый README модуля ссылается на код и домашку; примеры и задания покрывают темы статей (раскладка зафиксирована в отметках 1.1–7.4 выше)
