# docker compose и несколько инстансов за балансировщиком

> Модуль 20 — Docker · [к модулю](../../../course/20-docker.md)

## Зачем это нужно

Реальный сервис редко живёт один: ему нужна база данных, кэш, иногда брокер. `docker compose`
описывает всё это окружение в одном файле и поднимает одной командой. А запуск приложения в
**нескольких инстансах за балансировщиком** — основа горизонтального масштабирования и
отказоустойчивости.

**Где применяется в проектах:** локальная разработка (поднять app + Postgres + Kafka); интеграционные
тесты; демо-стенды; понимание того, как тот же принцип работает в Kubernetes.

## compose: окружение одним файлом

```yaml
services:
  db:
    image: postgres:17
    environment:
      POSTGRES_USER: app
      POSTGRES_PASSWORD: secret
      POSTGRES_DB: appdb
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U app -d appdb"]
      interval: 5s
      retries: 5

  app:
    build:
      context: ../../..
      dockerfile: modules/m20-docker/docker/Dockerfile
    depends_on:
      db:
        condition: service_healthy   # стартуем app только когда БД готова
    expose:
      - "8080"                        # порт виден ТОЛЬКО во внутренней сети
```

- `depends_on … condition: service_healthy` — порядок запуска по готовности, а не «просто после».
- `expose` (а не `ports`) — порт доступен другим сервисам, но не публикуется наружу.
- Полный файл: [`../docker/docker-compose.yml`](../docker/docker-compose.yml).

## Несколько инстансов

```bash
docker compose -f modules/m20-docker/docker/docker-compose.yml up --build --scale app=3
```

`--scale app=3` поднимает три контейнера сервиса `app`. Все слушают порт 8080 **внутри** сети, но
наружу его никто не публикует — за это отвечает балансировщик.

## Балансировщик (nginx) — один вход, много инстансов

```yaml
  lb:
    image: nginx:1.27
    volumes:
      - ./nginx.conf:/etc/nginx/conf.d/default.conf:ro
    ports:
      - "8080:80"        # единственный внешний вход
    depends_on:
      - app
```

```nginx
resolver 127.0.0.11 valid=10s;     # встроенный DNS Docker отдаёт IP всех реплик "app"
server {
    listen 80;
    location / {
        set $backend "http://app:8080";
        proxy_pass $backend;        # переменная заставляет nginx перечитывать DNS -> round-robin
    }
}
```

Как это работает: имя `app` в Docker-сети резолвится в IP всех трёх реплик. nginx раскидывает
запросы между ними. Падение одного инстанса не роняет сервис — запросы идут на живые.
Полный конфиг: [`../docker/nginx.conf`](../docker/nginx.conf).

## Связь с домашкой модуля

В [`Mod20Homework`](../homework/src/main/java/com/javaroadmap/m20/homework/Mod20Homework.java):
`instanceNames`/`portRange` моделируют раздачу имён и портов нескольким инстансам;
`mergeComposeEnv` — слияние переменных окружения (база + override), как это делает compose.

## Итог

**Что изучено:**
- `docker compose` описывает всё окружение; `depends_on … healthy` управляет порядком готовности.
- `--scale app=N` запускает N инстансов; `expose` vs `ports`.
- Балансировка через nginx + Docker DNS: один внешний порт, round-robin по репликам, отказоустойчивость.

**Как применять на практике:**
- Поднимать app + БД + брокер одной командой для разработки и интеграционных тестов.
- Масштабировать сервис горизонтально и ставить перед ним балансировщик.
- Переносить эти же идеи в Kubernetes (Deployment + Service) — модуль 17.
