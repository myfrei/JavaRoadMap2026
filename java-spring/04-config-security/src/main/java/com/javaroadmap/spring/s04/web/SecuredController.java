package com.javaroadmap.spring.s04.web;

import java.util.List;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Защищённая часть API (статья 04). Кто я и какие у меня права —
 * из Authentication, который Security кладёт в контекст запроса.
 */
@RestController
@RequestMapping("/api")
public class SecuredController {

    @GetMapping("/user/me")
    public Map<String, Object> me(Authentication auth) {
        List<String> roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        return Map.of("username", auth.getName(), "roles", roles);
    }

    /** До этого метода неадмина не пустит URL-правило basicChain. */
    @GetMapping("/admin/stats")
    public Map<String, Object> stats() {
        return Map.of("users", 2, "uptime", "учебная заглушка");
    }
}
