# Docker: сборка, compose и несколько инстансов

Готовые **шаблоны** для проекта модуля 20. Адаптируй под своё приложение (когда оно станет
долгоживущим сервисом — например, после модуля 06 «Web и API» на Spring Boot).

| Файл | Что показывает |
|------|----------------|
| [`Dockerfile`](Dockerfile) | multi-stage сборка JVM-приложения: `JDK (build)` → `JRE (runtime)`, non-root, минимальный образ |
| [`docker-compose.yml`](docker-compose.yml) | приложение + Postgres + nginx-балансировщик; масштабирование `--scale app=N` |
| [`nginx.conf`](nginx.conf) | round-robin по всем инстансам `app` через встроенный DNS Docker |

## Сборка образа

```bash
# из КОРНЯ репозитория (контекст должен видеть ./gradlew и модули)
docker build -f modules/m20-docker/docker/Dockerfile -t javaroadmap/app:dev .
```

## Запуск окружения

```bash
docker compose -f modules/m20-docker/docker/docker-compose.yml up --build
```

## Несколько инстансов за балансировщиком

```bash
# 3 копии приложения; снаружи по-прежнему один порт 8080 -> nginx раскидывает запросы
docker compose -f modules/m20-docker/docker/docker-compose.yml up --build --scale app=3

# проверка распределения (несколько запросов попадут на разные инстансы)
for i in $(seq 1 6); do curl -s http://localhost:8080/ ; echo; done
```

Падение одного инстанса не роняет сервис: nginx продолжит слать запросы на оставшиеся.

## Чек-лист

- [ ] Образ собирается multi-stage и не тащит JDK/исходники в runtime.
- [ ] `docker compose up` поднимает приложение + Postgres (с healthcheck).
- [ ] `--scale app=3` даёт 3 инстанса за одним внешним портом.
- [ ] Связал это с домашкой: [`../homework/README.md`](../homework/README.md).
