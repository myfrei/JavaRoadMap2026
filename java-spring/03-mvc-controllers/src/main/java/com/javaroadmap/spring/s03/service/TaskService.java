package com.javaroadmap.spring.s03.service;

import com.javaroadmap.spring.s03.model.Task;
import com.javaroadmap.spring.s03.web.TaskNotFoundException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

/** Бизнес-логика отдельно от веба: контроллеры — лишь адаптеры над этим сервисом. */
@Service
public class TaskService {

    private final Map<Long, Task> storage = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();

    public List<Task> all() {
        return storage.values().stream()
                .sorted(Comparator.comparingLong(Task::id))
                .toList();
    }

    public Task byId(long id) {
        Task task = storage.get(id);
        if (task == null) {
            throw new TaskNotFoundException(id);
        }
        return task;
    }

    public Task create(String title) {
        long id = sequence.incrementAndGet();
        Task task = new Task(id, title, false);
        storage.put(id, task);
        return task;
    }

    public Task complete(long id) {
        Task completed = byId(id).complete();
        storage.put(id, completed);
        return completed;
    }

    public void delete(long id) {
        if (storage.remove(id) == null) {
            throw new TaskNotFoundException(id);
        }
    }
}
