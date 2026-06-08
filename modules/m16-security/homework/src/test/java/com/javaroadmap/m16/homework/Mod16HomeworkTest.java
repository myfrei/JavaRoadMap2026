package com.javaroadmap.m16.homework;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Домашка модуля 16 — Безопасность")
class Mod16HomeworkTest {

    @Test
    @DisplayName("Задание 1: constantTimeEquals")
    void step1_constantTimeEquals() {
        byte[] a = "abc".getBytes(StandardCharsets.UTF_8);
        assertTrue(Mod16Homework.constantTimeEquals(a, "abc".getBytes(StandardCharsets.UTF_8)));
        assertFalse(Mod16Homework.constantTimeEquals(a, "abd".getBytes(StandardCharsets.UTF_8)));
        assertFalse(Mod16Homework.constantTimeEquals(a, "ab".getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    @DisplayName("Задание 2: hmacSha256Hex (известный вектор)")
    void step2_hmac() {
        assertEquals(
                "f7bc83f430538424b13298e6aa6fb143ef4d59a14946175997479dbc2d1a3cd8",
                Mod16Homework.hmacSha256Hex("key", "The quick brown fox jumps over the lazy dog"));
    }

    @Test
    @DisplayName("Задание 3: isStrongPassword")
    void step3_isStrongPassword() {
        assertTrue(Mod16Homework.isStrongPassword("Abcd1234"));
        assertFalse(Mod16Homework.isStrongPassword("abcd1234"));
        assertFalse(Mod16Homework.isStrongPassword("Abc1"));
    }

    @Test
    @DisplayName("Задание 4: maskEmail")
    void step4_maskEmail() {
        assertEquals("j***@example.com", Mod16Homework.maskEmail("john.doe@example.com"));
    }

    @Test
    @DisplayName("Задание 5: sanitizeFilename")
    void step5_sanitizeFilename() {
        assertEquals("file.txt", Mod16Homework.sanitizeFilename("../../etc/file.txt"));
        assertEquals("a.txt", Mod16Homework.sanitizeFilename("/tmp/a.txt"));
    }
}
