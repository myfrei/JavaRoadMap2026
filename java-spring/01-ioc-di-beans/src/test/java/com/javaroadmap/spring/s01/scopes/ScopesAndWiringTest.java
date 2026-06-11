package com.javaroadmap.spring.s01.scopes;

import static org.assertj.core.api.Assertions.assertThat;

import com.javaroadmap.spring.s01.notify.EmailNotificationSender;
import com.javaroadmap.spring.s01.notify.NotificationSender;
import com.javaroadmap.spring.s01.notify.SmsNotificationSender;
import com.javaroadmap.spring.s01.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

/** Скоупы и выбор реализации (статья 06) — на полном контексте приложения. */
@SpringBootTest
class ScopesAndWiringTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void singletonIsTheSameInstanceEveryTime() {
        assertThat(context.getBean(OrderService.class))
                .isSameAs(context.getBean(OrderService.class));
    }

    @Test
    void prototypeIsAFreshInstanceEveryTime() {
        var first = context.getBean(ScratchPad.class);
        var second = context.getBean(ScratchPad.class);

        assertThat(first).isNotSameAs(second);
        assertThat(first.instanceNumber()).isNotEqualTo(second.instanceNumber());
    }

    @Test
    void primaryWinsWhenTypeIsAmbiguous() {
        assertThat(context.getBean(NotificationSender.class))
                .isInstanceOf(EmailNotificationSender.class);
    }

    @Test
    void qualifierPicksTheNamedBean() {
        assertThat(context.getBean("sms", NotificationSender.class))
                .isInstanceOf(SmsNotificationSender.class);
    }
}
