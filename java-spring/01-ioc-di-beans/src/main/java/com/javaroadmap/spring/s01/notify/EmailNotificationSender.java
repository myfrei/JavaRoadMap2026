package com.javaroadmap.spring.s01.notify;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * Канал по умолчанию: @Primary выигрывает, когда зависимость объявлена
 * просто как NotificationSender, без уточнения.
 */
@Primary
@Component
public class EmailNotificationSender implements NotificationSender {

    @Override
    public String send(String to, String text) {
        return "email -> %s: %s".formatted(to, text);
    }
}
