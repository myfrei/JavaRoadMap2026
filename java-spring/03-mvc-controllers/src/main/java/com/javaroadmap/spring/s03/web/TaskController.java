package com.javaroadmap.spring.s03.web;

import com.javaroadmap.spring.s03.model.Task;
import com.javaroadmap.spring.s03.service.TaskService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST-контроллер (статья 04). @RestController = @Controller + @ResponseBody:
 * каждый метод возвращает не имя view, а данные, которые конвертер
 * сериализует в JSON. Маршрут до метода строит DispatcherServlet (статья 02) —
 * см. handler()-проверки в TaskControllerTest.
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    /** Валидация на границе (статья 04): мусор не проходит дальше контроллера. */
    public record NewTaskRequest(
            @NotBlank(message = "title не должен быть пустым")
            @Size(max = 100, message = "title длиннее 100 символов")
            String title) {
    }

    private final TaskService tasks;

    public TaskController(TaskService tasks) {
        this.tasks = tasks;
    }

    @GetMapping
    public List<Task> all() {
        return tasks.all();
    }

    @GetMapping("/{id}")
    public Task byId(@PathVariable long id) {
        return tasks.byId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Task create(@Valid @RequestBody NewTaskRequest request) {
        return tasks.create(request.title());
    }

    @PatchMapping("/{id}/complete")
    public Task complete(@PathVariable long id) {
        return tasks.complete(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        tasks.delete(id);
    }
}
