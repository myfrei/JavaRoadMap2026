package com.javaroadmap.spring.s04.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;

/** @PreAuthorize (статья 04): правило срабатывает на вызове метода, без HTTP. */
@SpringBootTest
class MethodSecurityTest {

    @Autowired
    private ReportService reports;

    @Test
    @WithMockUser(roles = "USER")
    void userCannotCallAdminOnlyMethod() {
        assertThatExceptionOfType(AccessDeniedException.class)
                .isThrownBy(reports::sensitiveReport);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanCallAdminOnlyMethod() {
        assertThat(reports.sensitiveReport()).contains("отчёт");
    }

    @Test
    @WithMockUser(roles = "USER")
    void unrestrictedMethodIsOpenToAnyAuthenticatedUser() {
        assertThat(reports.publicSummary()).contains("сводка");
    }
}
