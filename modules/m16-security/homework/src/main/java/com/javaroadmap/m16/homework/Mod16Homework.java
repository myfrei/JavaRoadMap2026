package com.javaroadmap.m16.homework;

/**
 * Домашка модуля 16 — «Безопасность» (constant-time, HMAC, пароли, маскирование).
 * Проверка: {@code ./gradlew :modules:m16-security:homeworkTest}
 */
public final class Mod16Homework {

    private Mod16Homework() {
    }

    /** Задание 1: сравнение байтов за постоянное время (без раннего выхода). */
    public static boolean constantTimeEquals(byte[] a, byte[] b) {
        throw new UnsupportedOperationException("TODO задание 1: constantTimeEquals");
    }

    /** Задание 2: HMAC-SHA256 в hex (используй javax.crypto.Mac, "HmacSHA256"). */
    public static String hmacSha256Hex(String key, String message) {
        throw new UnsupportedOperationException("TODO задание 2: hmacSha256Hex");
    }

    /** Задание 3: надёжный ли пароль: >=8 символов, есть заглавная, строчная и цифра. */
    public static boolean isStrongPassword(String password) {
        throw new UnsupportedOperationException("TODO задание 3: isStrongPassword");
    }

    /** Задание 4: маска e-mail: оставить первую букву локальной части. "john@x.com" -> "j***@x.com". */
    public static String maskEmail(String email) {
        throw new UnsupportedOperationException("TODO задание 4: maskEmail");
    }

    /** Задание 5: безопасное имя файла: убрать путь и "..", оставить базовое имя. */
    public static String sanitizeFilename(String path) {
        throw new UnsupportedOperationException("TODO задание 5: sanitizeFilename");
    }
}
