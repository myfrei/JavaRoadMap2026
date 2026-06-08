package com.javaroadmap.m20.homework;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Домашка модуля 20 — Docker")
class Mod20HomeworkTest {

    @Test
    @DisplayName("Задание 1: parseImageRef")
    void step1_parseImageRef() {
        assertEquals(new ImageRef("ghcr.io", "org/app", "1.2"), Mod20Homework.parseImageRef("ghcr.io/org/app:1.2"));
        assertEquals(new ImageRef("", "org/app", "latest"), Mod20Homework.parseImageRef("org/app"));
        assertEquals(new ImageRef("", "nginx", "1.25"), Mod20Homework.parseImageRef("nginx:1.25"));
    }

    @Test
    @DisplayName("Задание 2: dockerRunCommand")
    void step2_dockerRunCommand() {
        assertEquals(
                "docker run -d --name web -p 8080:80 nginx:1.25",
                Mod20Homework.dockerRunCommand("nginx:1.25", "web", 8080, 80));
    }

    @Test
    @DisplayName("Задание 3: instanceNames")
    void step3_instanceNames() {
        assertEquals(List.of("app-1", "app-2", "app-3"), Mod20Homework.instanceNames("app", 3));
    }

    @Test
    @DisplayName("Задание 4: portRange")
    void step4_portRange() {
        assertEquals(List.of(8080, 8081, 8082), Mod20Homework.portRange(8080, 3));
    }

    @Test
    @DisplayName("Задание 5: lintDockerfile")
    void step5_lintDockerfile() {
        assertEquals(
                List.of("set a non-root USER", "add a HEALTHCHECK"),
                Mod20Homework.lintDockerfile(
                        List.of("FROM eclipse-temurin:25", "COPY . /app", "CMD [\"java\",\"-jar\",\"app.jar\"]")));
        assertEquals(
                List.of("pin base image version"),
                Mod20Homework.lintDockerfile(List.of("FROM ubuntu:latest", "USER app", "HEALTHCHECK CMD true")));
    }

    @Test
    @DisplayName("Задание 6: mergeComposeEnv")
    void step6_mergeComposeEnv() {
        assertEquals(
                Map.of("A", "1", "B", "9", "C", "3"),
                Mod20Homework.mergeComposeEnv(Map.of("A", "1", "B", "2"), Map.of("B", "9", "C", "3")));
    }
}
