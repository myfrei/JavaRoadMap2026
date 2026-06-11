package com.javaroadmap.spring.s04.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Полный JWT-флоу (статья 06): Basic-логин → токен → Bearer-доступ
 * к stateless-ресурсу из другой security-цепочки.
 */
@SpringBootTest
@AutoConfigureMockMvc
class JwtFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void basicLoginYieldsTokenThatOpensJwtResource() throws Exception {
        String body = mockMvc.perform(post("/api/token").with(httpBasic("admin", "admin123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(body).get("token").asText();

        mockMvc.perform(get("/api/jwt/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sub").value("admin"))
                .andExpect(jsonPath("$.roles").value(org.hamcrest.Matchers.hasItem("ROLE_ADMIN")));
    }

    @Test
    void jwtResourceWithoutTokenIs401() throws Exception {
        mockMvc.perform(get("/api/jwt/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void garbageTokenIs401() throws Exception {
        mockMvc.perform(get("/api/jwt/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer не.настоящий.токен"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tokenEndpointRequiresAuthentication() throws Exception {
        mockMvc.perform(post("/api/token"))
                .andExpect(status().isUnauthorized());
    }
}
