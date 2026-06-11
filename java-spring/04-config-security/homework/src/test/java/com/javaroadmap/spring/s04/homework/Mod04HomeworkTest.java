package com.javaroadmap.spring.s04.homework;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/** «Красные» тесты домашки модуля 4. Менять их не нужно. */
@SpringBootTest
@AutoConfigureMockMvc
class Mod04HomeworkTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Test
    @DisplayName("Задание 1: HwProperties — типобезопасная конфигурация с префиксом hw")
    void task1_configurationProperties() {
        HwProperties props = context.getBean(HwProperties.class);

        assertThat(props.motd()).isEqualTo("Добро пожаловать в домашку модуля 4");
        assertThat(props.maxAttempts()).isEqualTo(3);
    }

    @Test
    @DisplayName("Задание 2: /hw/public/ping открыт анонимам")
    void task2_publicPing() throws Exception {
        mockMvc.perform(get("/hw/public/ping"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pong").value(true));
    }

    @Test
    @DisplayName("Задание 2: /hw/secret — 401 без аутентификации, 200 с Basic")
    void task2_secret() throws Exception {
        mockMvc.perform(get("/hw/secret"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/hw/secret").with(httpBasic("user", "user123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user").value("user"));
    }

    @Test
    @DisplayName("Задание 2: /hw/admin/panel — 403 для USER, 200 для ADMIN")
    void task2_adminPanel() throws Exception {
        mockMvc.perform(get("/hw/admin/panel").with(httpBasic("user", "user123")))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/hw/admin/panel").with(httpBasic("admin", "admin123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.panel").value("ok"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Задание 3: exportAll запрещён роли USER")
    void task3_userDenied() {
        HwReportService reports = context.getBean(HwReportService.class);

        assertThatExceptionOfType(AccessDeniedException.class)
                .isThrownBy(reports::exportAll);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Задание 3: exportAll доступен роли ADMIN")
    void task3_adminAllowed() {
        HwReportService reports = context.getBean(HwReportService.class);

        assertThat(reports.exportAll()).isEqualTo("Выгрузка всех данных");
    }

    @Test
    @DisplayName("Задание 4: preflight с https://hw.example.com получает CORS-заголовки")
    void task4_corsPreflight() throws Exception {
        mockMvc.perform(options("/hw/public/ping")
                        .header(HttpHeaders.ORIGIN, "https://hw.example.com")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "https://hw.example.com"));
    }

    @Test
    @DisplayName("Задание 5: issueToken — валидный JWT c subject, ролями и сроком 15 минут")
    void task5_issueToken() {
        HwTokenService tokens = context.getBean(HwTokenService.class);

        String token = tokens.issueToken("neo", List.of("ROLE_USER"));
        Jwt jwt = jwtDecoder.decode(token);

        assertThat(jwt.getSubject()).isEqualTo("neo");
        assertThat(jwt.getClaimAsStringList("roles")).containsExactly("ROLE_USER");
        Duration lifetime = Duration.between(jwt.getIssuedAt(), jwt.getExpiresAt());
        assertThat(lifetime).isEqualTo(Duration.ofMinutes(15));
        assertThat(jwt.getExpiresAt()).isAfter(Instant.now());
    }
}
