package com.javaroadmap.spring.s03.web;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.javaroadmap.spring.s03.model.Task;
import com.javaroadmap.spring.s03.service.TaskService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * @WebMvcTest (статьи 02 и 04): поднят только MVC-слой. Запрос проходит
 * настоящий путь DispatcherServlet → HandlerMapping → метод контроллера →
 * конвертер JSON → @RestControllerAdvice. Сервис — мок.
 */
@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService tasks;

    @Test
    void dispatcherServletRoutesToTheRightHandlerMethod() throws Exception {
        when(tasks.byId(1)).thenReturn(new Task(1, "выучить MVC", false));

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                // главная проверка статьи 02: КУДА DispatcherServlet привёл запрос
                .andExpect(handler().handlerType(TaskController.class))
                .andExpect(handler().methodName("byId"))
                .andExpect(jsonPath("$.title").value("выучить MVC"));
    }

    @Test
    void postWithValidBodyReturns201() throws Exception {
        when(tasks.create("новая задача")).thenReturn(new Task(5, "новая задача", false));

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"новая задача\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    void postWithBlankTitleReturns400WithFieldError() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"   \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("title не должен быть пустым"));

        Mockito.verifyNoInteractions(tasks); // мусор не дошёл до сервиса
    }

    @Test
    void domainExceptionBecomes404ViaAdvice() throws Exception {
        when(tasks.byId(99)).thenThrow(new TaskNotFoundException(99));

        mockMvc.perform(get("/api/tasks/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Задача 99 не найдена"));
    }

    @Test
    void deleteReturns204() throws Exception {
        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent());
    }
}
