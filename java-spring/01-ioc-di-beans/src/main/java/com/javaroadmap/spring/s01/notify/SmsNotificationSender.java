package com.javaroadmap.spring.s01.notify;

import org.springframework.stereotype.Component;

/**
 * Альтернативный канал. Имя бина задано явно — внедряется
 * через @Qualifier("sms") (см. ScopesAndWiringTest).
 */
@Component("sms")
public class SmsNotificationSender implements NotificationSender {

    @Override
    public String send(String to, String text) {
        return "sms -> %s: %s".formatted(to, text);
    }
}
