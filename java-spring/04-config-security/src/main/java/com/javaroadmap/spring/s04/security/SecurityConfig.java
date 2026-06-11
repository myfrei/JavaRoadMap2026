package com.javaroadmap.spring.s04.security;

import static org.springframework.security.config.Customizer.withDefaults;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Архитектура Security (статьи 02–03): запрос проходит ЦЕПОЧКУ фильтров.
 * Цепочек может быть несколько — DispatcherServlet здесь ни при чём,
 * выбор цепочки происходит раньше, по securityMatcher и @Order.
 *
 * <p>Здесь две цепочки: /api/jwt/** живёт по Bearer-токену (статья 06),
 * остальной /api/** — по HTTP Basic. Пути вне /api/** этим модулем
 * не защищаются (их займёт домашка).
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /** Цепочка №1: stateless-ресурсы по JWT. Проверяется первой (@Order). */
    @Bean
    @Order(1)
    public SecurityFilterChain jwtChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/api/jwt/**")
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .oauth2ResourceServer(oauth -> oauth.jwt(withDefaults()))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrf -> csrf.disable());
        return http.build();
    }

    /** Цепочка №2: остальной API по HTTP Basic + URL-авторизация по ролям (статья 04). */
    @Bean
    @Order(2)
    public SecurityFilterChain basicChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/api/**")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .httpBasic(withDefaults())
                .cors(withDefaults())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrf -> csrf.disable()); // stateless API без сессий — CSRF-токену негде жить
        return http.build();
    }

    /**
     * Учебные пользователи в памяти. {noop} = пароль без хеша — ТОЛЬКО для
     * примеров; в проде BCrypt/Argon2 через PasswordEncoder.
     */
    @Bean
    public UserDetailsService users() {
        return new InMemoryUserDetailsManager(
                User.withUsername("user").password("{noop}user123").roles("USER").build(),
                User.withUsername("admin").password("{noop}admin123").roles("ADMIN", "USER").build());
    }

    /** CORS (статья 05): браузеру с другого origin разрешён только список ниже. */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "DELETE"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}
