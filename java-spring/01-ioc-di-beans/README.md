# Модуль 1. IoC, DI и бины — оглавление

Набор лонгридов о фундаменте Spring: инверсия управления, внедрение зависимостей и жизнь бинов в
контейнере. Читать по порядку — каждая статья опирается на предыдущую. В конце модуля у вас будет прочная
база перед работой с базой данных и вебом.

- [Инверсия управления и внедрение зависимостей](01-ioc-and-di.md)
- [Бины и их жизненный цикл](02-beans-and-lifecycle.md)
- [Конфигурация Spring-приложений](03-spring-configuration.md)
- [Сервисный слой и аннотация @Service](04-service-layer.md)
- [Контроллеры: @Controller и @RestController](05-controllers.md)
- [Scope бинов и выбор реализации](06-bean-scopes-and-wiring.md)

## 💻 Код модуля

Рядом со статьями живёт запускаемый код (Gradle-подпроект `:java-spring:01-ioc-di-beans`):

- [`src/main/java/...`](src/main/java/com/javaroadmap/spring/s01/) — примеры по статьям: конструкторное
  внедрение, lifecycle-колбэки и `BeanPostProcessor`, `@Configuration`/`@Bean`, сервисный слой,
  REST-контроллер, скоупы и `@Primary`/`@Qualifier`.
- [`src/test/java/...`](src/test/java/com/javaroadmap/spring/s01/) — тесты к примерам
  (`@SpringBootTest`, `@WebMvcTest` + MockMvc, юнит-тесты с моками).
- [`homework/`](homework/README.md) — домашка: 5 заданий с «красными» тестами
  (`./gradlew :java-spring:01-ioc-di-beans:homeworkTest`).

```bash
./gradlew :java-spring:01-ioc-di-beans:test          # тесты примеров
./gradlew :java-spring:01-ioc-di-beans:homeworkTest  # «красные» задания
```

[📚 К треку Java Spring](../README.md)
