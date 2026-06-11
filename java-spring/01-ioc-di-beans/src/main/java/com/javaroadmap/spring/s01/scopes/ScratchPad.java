package com.javaroadmap.spring.s01.scopes;

import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Prototype-бин (статья 06): контейнер отдаёт НОВЫЙ экземпляр на каждый
 * getBean()/внедрение — в отличие от singleton-бинов, которых на контекст
 * ровно один. Номер экземпляра делает это видимым в тестах.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ScratchPad {

    private static final AtomicInteger COUNTER = new AtomicInteger();

    private final int instanceNumber = COUNTER.incrementAndGet();

    public int instanceNumber() {
        return instanceNumber;
    }
}
