package com.javaroadmap.spring.s04.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Аутентификация и авторизация (статьи 02–04): 401 — «кто ты?»,
 * 403 — «знаю, кто ты, но нельзя». Разница — половина собеседований.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SecurityAccessTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void publicEndpointIsOpenToAnonymous() throws Exception {
        mockMvc.perform(get("/api/public/ping"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pong").value(true));
    }

    @Test
    void protectedEndpointWithoutCredentialsIs401() throws Exception {
        mockMvc.perform(get("/api/user/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void basicAuthGivesAccessAndPrincipal() throws Exception {
        mockMvc.perform(get("/api/user/me").with(httpBasic("user", "user123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"));
    }

    @Test
    void adminEndpointForUserIs403() throws Exception {
        mockMvc.perform(get("/api/admin/stats").with(httpBasic("user", "user123")))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminEndpointForAdminIs200() throws Exception {
        mockMvc.perform(get("/api/admin/stats").with(httpBasic("admin", "admin123")))
                .andExpect(status().isOk());
    }

    @Test
    void wrongPasswordIs401() throws Exception {
        mockMvc.perform(get("/api/user/me").with(httpBasic("user", "оп-неверный")))
                .andExpect(status().isUnauthorized());
    }
}
