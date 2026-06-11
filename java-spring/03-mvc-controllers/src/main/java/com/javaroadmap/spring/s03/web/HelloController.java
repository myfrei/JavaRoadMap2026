package com.javaroadmap.spring.s03.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Контраст к @RestController (статья 03): «классический» @Controller
 * по умолчанию возвращает ИМЯ VIEW. Чтобы вернуть сами данные,
 * методу нужен явный @ResponseBody — у @RestController он встроен.
 */
@Controller
public class HelloController {

    @GetMapping("/hello")
    @ResponseBody
    public String hello() {
        return "Привет из @Controller + @ResponseBody";
    }
}
