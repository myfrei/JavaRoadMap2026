package com.javaroadmap.spring.s01.notify;

/**
 * Канал уведомлений. Реализаций несколько — выбор между ними
 * показывает @Primary и @Qualifier (статья 06).
 */
public interface NotificationSender {

    /** Возвращает текст отправленного сообщения — так канал легко проверить в тесте. */
    String send(String to, String text);
}
