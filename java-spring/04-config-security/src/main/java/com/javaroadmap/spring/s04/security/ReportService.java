package com.javaroadmap.spring.s04.security;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

/**
 * Method security (статья 04): правило живёт на МЕТОДЕ и сработает при любом
 * пути вызова — из контроллера, шедулера, другого сервиса. URL-правила
 * защищают только HTTP-вход.
 */
@Service
public class ReportService {

    @PreAuthorize("hasRole('ADMIN')")
    public String sensitiveReport() {
        return "Полный отчёт по пользователям";
    }

    public String publicSummary() {
        return "Открытая сводка";
    }
}
