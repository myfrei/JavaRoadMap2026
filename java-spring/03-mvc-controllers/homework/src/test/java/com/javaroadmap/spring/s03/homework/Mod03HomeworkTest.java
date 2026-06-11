package com.javaroadmap.spring.s03.homework;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * «Красные» тесты REST-части домашки модуля 3. Менять их не нужно.
 * Контроллер появится в контексте, как только ты повесишь аннотации.
 */
@SpringBootTest
@AutoConfigureMockMvc
class Mod03HomeworkTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NotesService notes;

    @BeforeEach
    void resetStorage() {
        notes.clear();
    }

    @Test
    @DisplayName("Задание 1: GET /api/notes — список и фильтр ?contains=")
    void task1_listAndFilter() throws Exception {
        notes.create("купить молоко");
        notes.create("выучить Spring MVC");

        mockMvc.perform(get("/api/notes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        mockMvc.perform(get("/api/notes").param("contains", "spring"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].text").value("выучить Spring MVC"));
    }

    @Test
    @DisplayName("Задание 1: GET /api/notes/{id} — заметка по id")
    void task1_byId() throws Exception {
        long id = notes.create("одна заметка").id();

        mockMvc.perform(get("/api/notes/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("одна заметка"));
    }

    @Test
    @DisplayName("Задание 2: POST /api/notes — 201 при валидном теле")
    void task2_createValid() throws Exception {
        mockMvc.perform(post("/api/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"новая заметка\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.text").value("новая заметка"));
    }

    @Test
    @DisplayName("Задания 2 и 4: POST с пустым text — 400 и сообщение по полю")
    void task2_createBlankText() throws Exception {
        mockMvc.perform(post("/api/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"  \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.text").value("text не должен быть пустым"));
    }

    @Test
    @DisplayName("Задание 3: DELETE /api/notes/{id} — 204, повторное удаление — 404")
    void task3_delete() throws Exception {
        long id = notes.create("временная").id();

        mockMvc.perform(delete("/api/notes/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/api/notes/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Задание 4: NoteNotFoundException — 404 с телом {\"error\": ...}")
    void task4_notFoundBody() throws Exception {
        mockMvc.perform(get("/api/notes/777"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Заметка 777 не найдена"));
    }
}
