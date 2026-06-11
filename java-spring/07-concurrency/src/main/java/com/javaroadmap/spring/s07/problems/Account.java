package com.javaroadmap.spring.s07.problems;

/**
 * Лекарство от дедлока (статья 06): глобальный ПОРЯДОК захвата замков.
 * Кто бы кому ни переводил, замки всегда берутся по возрастанию id —
 * цикл ожидания становится невозможным.
 */
public class Account {

    private final long id;
    private long balanceCents;

    public Account(long id, long initialCents) {
        this.id = id;
        this.balanceCents = initialCents;
    }

    public static void transfer(Account from, Account to, long amountCents) {
        Account first = from.id < to.id ? from : to;
        Account second = from.id < to.id ? to : from;
        synchronized (first) {
            synchronized (second) {
                from.balanceCents -= amountCents;
                to.balanceCents += amountCents;
            }
        }
    }

    public synchronized long balanceCents() {
        return balanceCents;
    }

    public long id() {
        return id;
    }
}
