package com.javaroadmap.spring.s01.lifecycle;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * BeanPostProcessor (статья 02): контейнер пропускает через него КАЖДЫЙ бин
 * до и после инициализации. На этом механизме построены @Transactional,
 * @Async и прочая «магия» Spring. Здесь — просто учёт инициализированных бинов.
 */
@Component
public class InitTrackingBeanPostProcessor implements BeanPostProcessor {

    private final List<String> initializedBeans = new ArrayList<>();

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (bean.getClass().getPackageName().startsWith("com.javaroadmap.spring.s01")) {
            initializedBeans.add(beanName);
        }
        return bean; // вернуть можно и обёртку (прокси) — так работает AOP
    }

    public List<String> initializedBeans() {
        return List.copyOf(initializedBeans);
    }
}
