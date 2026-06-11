package com.javaroadmap.spring.s01.lifecycle;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Демонстрация жизненного цикла бина (статья 02): «пул соединений», который
 * записывает каждый шаг своей жизни. Порядок шагов проверяет BeanLifecycleTest:
 * constructor → @PostConstruct → работа → @PreDestroy (при закрытии контекста).
 */
@Component
public class ConnectionPool {

    private final List<String> events = new ArrayList<>();
    private boolean open;

    public ConnectionPool() {
        events.add("constructor");
    }

    @PostConstruct
    void open() {
        // Здесь зависимости уже внедрены — самое место «тяжёлой» инициализации.
        open = true;
        events.add("postConstruct");
    }

    @PreDestroy
    void close() {
        open = false;
        events.add("preDestroy");
    }

    public boolean isOpen() {
        return open;
    }

    public List<String> events() {
        return List.copyOf(events);
    }
}
