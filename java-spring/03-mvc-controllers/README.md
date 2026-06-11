# Модуль 3. MVC и контроллеры — оглавление

> Формат модуля — серия лонгридов (лекций). Читай по порядку: каждая статья опирается на предыдущую и связана с ней по смыслу. Это финальный модуль трека по контроллерам и протоколам — от паттерна MVC до трёх способов общения сервисов: REST, gRPC и WebSocket.

## Содержание

1. [Паттерн MVC](01-mvc-pattern.md)
2. [DispatcherServlet: путь HTTP-запроса](02-dispatcher-servlet.md)
3. [Виды контроллеров в Spring](03-controller-types.md)
4. [REST и Spring](04-rest-with-spring.md)
5. [gRPC и Spring](05-grpc-with-spring.md)
6. [WebSocket и Spring](06-websocket-with-spring.md)

## 💻 Код модуля

Рядом со статьями живёт запускаемый код (Gradle-подпроект `:java-spring:03-mvc-controllers`,
домен — список задач):

- [`src/main/java/...`](src/main/java/com/javaroadmap/spring/s03/) — REST CRUD с валидацией и
  `@RestControllerAdvice`, контраст `@Controller`/`@RestController`, gRPC-сервис на in-process
  транспорте (контракт написан руками вместо protoc — видно устройство), WebSocket-эхо.
- [`src/test/java/...`](src/test/java/com/javaroadmap/spring/s03/) — `@WebMvcTest` с проверкой
  маршрутизации DispatcherServlet (`handler()`), валидации и advice; gRPC-вызов через in-process
  канал; живой WebSocket-тест на случайном порту.
- [`homework/`](homework/README.md) — домашка: 5 заданий с «красными» тестами
  (`./gradlew :java-spring:03-mvc-controllers:homeworkTest`).

```bash
./gradlew :java-spring:03-mvc-controllers:test          # тесты примеров
./gradlew :java-spring:03-mvc-controllers:homeworkTest  # «красные» задания
```

---

[📚 К треку Java Spring](../README.md)
