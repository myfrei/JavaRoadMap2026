package com.javaroadmap.m20.homework;

import java.util.List;
import java.util.Map;

/**
 * Домашка модуля 20 — «Docker» (advance-трек): образы, run-команды, масштабирование, линт Dockerfile.
 * Проверка: {@code ./gradlew :modules:m20-docker:homeworkTest}
 */
public final class Mod20Homework {

    private Mod20Homework() {
    }

    /**
     * Задание 1: разобрать ссылку на образ "[registry/]repo[:tag]".
     * registry — часть до первого '/', ЕСЛИ она похожа на хост (содержит '.'); иначе "". tag после ':' или "latest".
     * "ghcr.io/org/app:1.2" -> (ghcr.io, org/app, 1.2); "org/app" -> ("", org/app, latest).
     */
    public static ImageRef parseImageRef(String ref) {
        throw new UnsupportedOperationException("TODO задание 1: parseImageRef");
    }

    /** Задание 2: команда запуска: "docker run -d --name &lt;name&gt; -p &lt;host&gt;:&lt;container&gt; &lt;image&gt;". */
    public static String dockerRunCommand(String image, String name, int hostPort, int containerPort) {
        throw new UnsupportedOperationException("TODO задание 2: dockerRunCommand");
    }

    /** Задание 3: имена N инстансов: base-1 .. base-N (несколько копий приложения). */
    public static List<String> instanceNames(String base, int count) {
        throw new UnsupportedOperationException("TODO задание 3: instanceNames");
    }

    /** Задание 4: порты N инстансов: basePort, basePort+1, ... (каждой копии — свой порт). */
    public static List<Integer> portRange(int basePort, int count) {
        throw new UnsupportedOperationException("TODO задание 4: portRange");
    }

    /**
     * Задание 5: линт Dockerfile -> предупреждения В ПОРЯДКЕ ПРАВИЛ:
     * базовый образ не закреплён (":latest" или без тега) -> "pin base image version";
     * нет строки USER -> "set a non-root USER"; нет HEALTHCHECK -> "add a HEALTHCHECK".
     */
    public static List<String> lintDockerfile(List<String> lines) {
        throw new UnsupportedOperationException("TODO задание 5: lintDockerfile");
    }

    /** Задание 6: слить env для compose — значения override перекрывают base. */
    public static Map<String, String> mergeComposeEnv(Map<String, String> base, Map<String, String> override) {
        throw new UnsupportedOperationException("TODO задание 6: mergeComposeEnv");
    }
}

/** Разобранная ссылка на Docker-образ. */
record ImageRef(String registry, String repository, String tag) {
}
