# Классы и объекты: инкапсуляция и инварианты

> Модуль 1 — Синтаксис и ООП · [к модулю](../../../course/01-syntax.md)

## Зачем это нужно

Класс — основная единица проектирования в Java. Грамотный класс **защищает свои данные** и гарантирует,
что объект всегда в корректном состоянии (инвариант). Это снижает число багов: невалидное состояние
просто невозможно создать.

**Где применяется в проектах:** доменные модели (`Account`, `Order`, `User`); value-объекты (деньги,
координаты); любой класс, у которого есть правила («баланс не может уйти в минус», «email валиден»).

## Поля, конструктор, инкапсуляция

Инкапсуляция — это «прятать данные за методами». Поля делаем `private`, доступ — через методы,
которые проверяют правила.

```java
public final class BankAccount {
    private long balance;                 // приватное поле — снаружи не трогают напрямую

    public BankAccount(long initialBalance) {
        if (initialBalance < 0) {
            throw new IllegalArgumentException("balance must be >= 0");
        }
        this.balance = initialBalance;    // this.balance — поле, initialBalance — параметр
    }

    public long balance() {               // геттер без префикса get — идиоматично для простых аксессоров
        return balance;
    }
}
```

Ключевое: конструктор **не пускает** объект в невалидное состояние. Раз создав `BankAccount`,
ты уверен, что баланс неотрицателен.

## Методы хранят инвариант

```java
public void withdraw(long amount) {
    if (amount < 0) {
        throw new IllegalArgumentException("amount must be >= 0");
    }
    if (amount > balance) {
        throw new IllegalArgumentException("insufficient funds");  // защищаем инвариант
    }
    balance -= amount;
}

public void deposit(long amount) {
    if (amount < 0) {
        throw new IllegalArgumentException("amount must be >= 0");
    }
    balance += amount;
}
```

Снаружи нельзя выставить `balance = -100` — единственный путь к полю идёт через `deposit`/`withdraw`,
а они следят за правилами. Это и есть смысл инкапсуляции: **инвариант в одном месте**.

## `final` и неизменяемость

`final` у поля означает «присвоить один раз». Неизменяемые объекты безопасны в многопоточности
и предсказуемы:

```java
public final class Point {
    private final int x;
    private final int y;
    public Point(int x, int y) { this.x = x; this.y = y; }
    public int x() { return x; }
    public int y() { return y; }
}
```

## Связь с домашкой модуля

Класс `BankAccount` — это задание 4 в
[`Mod01Homework`](../homework/src/main/java/com/javaroadmap/m01/homework/Mod01Homework.java):
реализуй `deposit`/`withdraw`/`balance` так, чтобы овердрафт и отрицательные суммы кидали
`IllegalArgumentException`. Тест `step4_bankAccount` проверяет именно сохранение инварианта.

Проверка: `./gradlew :modules:m01-syntax:homeworkTest`.

## Итог

**Что изучено:**
- Поля `private`, доступ через методы — инкапсуляция.
- Конструктор валидирует и не пускает объект в невалидное состояние.
- Методы хранят инвариант в одном месте.
- `final`-поля → неизменяемые, безопасные объекты.

**Как применять на практике:**
- Проектировать доменные классы так, чтобы «плохое» состояние было невозможно создать.
- Прятать поля и валидировать на входе (конструктор + методы), а не размазывать проверки по коду.
- Предпочитать неизменяемые value-объекты там, где не нужна мутация (деньги, идентификаторы, координаты).
