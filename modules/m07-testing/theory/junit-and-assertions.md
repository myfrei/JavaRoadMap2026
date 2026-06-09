# JUnit 5 и ассерты

> Модуль 7 — Тестирование · [к модулю](../../../course/07-testing.md)

## Зачем это нужно

Тесты — это страховка, которая позволяет менять код без страха. Хорошо покрытый код можно
рефакторить, обновлять зависимости и расширять, мгновенно узнавая о поломках. JUnit — стандарт
тестирования в Java, на нём построено всё остальное (Mockito, Spring Test).

**Где применяется в проектах:** каждый модуль этого курса (`homeworkTest`!); CI-проверки перед мержем;
регрессионные тесты; документация поведения через примеры.

## Структура теста: Arrange–Act–Assert

```java
class CalculatorTest {

    @Test
    void addsTwoNumbers() {
        Calculator calc = new Calculator();   // Arrange — подготовка
        int result = calc.add(2, 3);          // Act — действие
        assertEquals(5, result);              // Assert — проверка
    }
}
```

Три фазы делают тест читаемым: видно, что готовим, что вызываем, что ожидаем.

## Жизненный цикл

```java
@BeforeEach
void setUp() { /* перед КАЖДЫМ тестом — свежее состояние */ }

@AfterEach
void tearDown() { /* после каждого — очистка */ }
```

`@BeforeEach` гарантирует изоляцию: тесты не влияют друг на друга через общее состояние.

## Полезные ассерты

```java
assertEquals(expected, actual);
assertTrue(list.isEmpty());
assertThrows(IllegalArgumentException.class, () -> calc.divide(1, 0));  // ждём исключение
assertAll(                                  // проверить всё, не падая на первом
    () -> assertEquals("Ann", user.name()),
    () -> assertEquals(30, user.age())
);
```

`assertThrows` — проверка, что код кидает нужное исключение (а не «как-нибудь падает»).

## Параметризованные тесты

Один тест на много входных данных — без копипасты:

```java
@ParameterizedTest
@CsvSource({ "1,1,2", "2,3,5", "10,-4,6" })
void adds(int a, int b, int expected) {
    assertEquals(expected, new Calculator().add(a, b));
}
```

## AssertJ — читаемые цепочки

Популярная альтернатива с «fluent»-стилем:

```java
assertThat(names)
        .hasSize(3)
        .contains("Ann")
        .doesNotContain("Eve");
```

Читается как предложение и даёт понятные сообщения об ошибке.

## Связь с кодом модуля

Все `*HomeworkTest` в курсе — это JUnit 5. Загляни в
[`Mod07HomeworkTest`](../homework/src/test/java/com/javaroadmap/m07/homework/Mod07HomeworkTest.java):
`@Test`, `assertEquals`, `assertThrows`, `@DisplayName` — ровно то, что описано выше. Эти тесты
«красные», пока ты не реализуешь задания.

## Итог

**Что изучено:**
- Структура теста Arrange–Act–Assert; `@BeforeEach` для изоляции.
- Ассерты: `assertEquals`/`assertThrows`/`assertAll`; параметризованные тесты.
- AssertJ для читаемых проверок.

**Как применять на практике:**
- Писать маленькие изолированные тесты на каждое поведение, а не один «большой».
- Проверять и ошибки (`assertThrows`), а не только счастливый путь.
- Выносить повторяющийся ввод в `@ParameterizedTest`; для читаемости брать AssertJ.
