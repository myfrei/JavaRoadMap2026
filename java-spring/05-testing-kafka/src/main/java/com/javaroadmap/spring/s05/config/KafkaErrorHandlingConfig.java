package com.javaroadmap.spring.s05.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

/**
 * Ретраи и DLT (статья 05): упавшее сообщение повторяется ещё 2 раза
 * без паузы, затем публикуется в "<topic>-dlt". Boot сам подставит
 * этот CommonErrorHandler во все listener-контейнеры.
 */
@Configuration
public class KafkaErrorHandlingConfig {

    @Bean
    public DefaultErrorHandler kafkaErrorHandler(KafkaTemplate<Object, Object> template) {
        var sendToDlt = new DeadLetterPublishingRecoverer(template);
        return new DefaultErrorHandler(sendToDlt, new FixedBackOff(0L, 2L));
    }
}
