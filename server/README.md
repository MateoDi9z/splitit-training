# Server — SplitIt Training

API en **Spring Boot + Kotlin**. Recibe HTTP desde el Next.js (`:3000`), aplica reglas de negocio y habla con Postgres por JDBC.

Si `GET /api/ping` responde `pong`, el circuito está vivo.

```
Next.js :3000  ──/api/*──►  Spring Boot :8080  ──JDBC──►  Postgres :5432
```

## Stack

| Pieza           | Qué                                                                  |
| --------------- | -------------------------------------------------------------------- |
| Runtime         | Java 21 · Kotlin 2.3 · Spring Boot 4.1                               |
| Web             | Spring Web MVC (`spring-boot-starter-webmvc`)                        |
| Persistencia    | Spring Data JPA + Hibernate · driver PostgreSQL                      |
| Validación      | Jakarta Validation (`spring-boot-starter-validation`)                |
| JSON            | Jackson Kotlin module                                                |
| Dev             | Spring Boot DevTools · `springboot4-dotenv` (solo `developmentOnly`) |
| Calidad         | ktlint 1.5 · detekt 1.23                                             |
| Build           | Gradle 9.5 (wrapper) · Kotlin DSL                                    |
| Empaquetado     | `bootJar` · imagen Docker multi-stage (`eclipse-temurin:21`)         |

El paquete base es `edu.austral.splitit.training.server`.

## Arquitectura

El código está separado en **tres capas** (hexagonal / ports & adapters). La regla: las dependencias apuntan **hacia adentro**. `infrastructure` conoce a `application` y `domain`; `domain` no conoce Spring, HTTP ni JPA.

```
                    ┌─────────────────────────────────────┐
  HTTP /api/*       │           infrastructure            │
 ─────────────────► │  api (controller, dto)              │
                    │  config  ·  persistance (JPA)       │
                    └──────────────┬──────────────────────┘
                                   │
                    ┌──────────────▼──────────────────────┐
                    │            application              │
                    │  port (interfaces)                  │
                    │  service (casos de uso)             │
                    └──────────────┬──────────────────────┘
                                   │
                    ┌──────────────▼──────────────────────┐
                    │              domain                 │
                    │  model · repository · service       │
                    └─────────────────────────────────────┘
```

| Capa             | Qué vive acá                                                                 | Qué **no** |
| ---------------- | ---------------------------------------------------------------------------- | ---------- |
| `domain`         | Entidades, value objects, interfaces de repositorio, reglas de dominio       | HTTP, DTOs, anotaciones JPA de infraestructura, Spring Web |
| `application`    | Casos de uso (`service`) y puertos (`port`) que orquestan el dominio         | Controllers, SQL, detalles de framework |
| `infrastructure` | Adaptadores: REST, DTOs, beans de Spring, implementaciones JPA del repo      | Reglas de negocio |

Un request típico:

1. **Controller** (`infrastructure.api.controller`) recibe HTTP, valida el DTO y llama al application service.
2. **Application service** orquesta el caso de uso (prestar un libro, devolverlo, etc.).
3. **Domain** aplica las reglas (¿hay copias?, ¿préstamo duplicado?).
4. **Repository** (interfaz en `domain`, impl JPA en `infrastructure.persistance`) persiste.
5. El controller arma el DTO de respuesta. **No se exponen entidades JPA por la API.**

### Dónde va cada cosa nueva

| Si estás agregando…              | Va en                                      |
| -------------------------------- | ------------------------------------------ |
| Entidad / value object           | `domain/model`                             |
| Contrato de persistencia         | `domain/repository`                        |
| Regla que no depende de un caso  | `domain/service`                           |
| Interfaz hacia afuera o adentro  | `application/port`                         |
| Caso de uso (un flujo de la app) | `application/service`                      |
| Endpoint REST                    | `infrastructure/api/controller`            |
| Body / response de la API        | `infrastructure/api/dto`                   |
| `@Entity`, `JpaRepository`, SQL  | `infrastructure/persistance`               |
| CORS, Security, beans Spring     | `infrastructure/config`                    |

### Árbol

```
server/
├── build.gradle.kts          # plugins, deps, ktlint, detekt
├── settings.gradle.kts
├── Dockerfile                # multi-stage: JDK 21 compile → JRE 21 run
├── .editorconfig             # indent 4, max_line_length 120 en .kt/.kts
└── src/
    ├── main/
    │   ├── kotlin/…/server/
    │   │   ├── ServerApplication.kt
    │   │   ├── domain/
    │   │   │   ├── model/
    │   │   │   ├── repository/
    │   │   │   └── service/
    │   │   ├── application/
    │   │   │   ├── port/
    │   │   │   └── service/
    │   │   └── infrastructure/
    │   │       ├── api/
    │   │       │   ├── controller/   # PingController
    │   │       │   └── dto/
    │   │       ├── config/           # CORS
    │   │       └── persistance/      # adapters JPA
    │   └── resources/
    │       └── application.yaml
    └── test/kotlin/…/server/
        └── ServerApplicationTests.kt
```

Hoy el esqueleto ya está; lo único cableado es el ping y CORS. Las carpetas de dominio / aplicación / persistencia están listas para features.

## API

| Método | Path        | Respuesta | Notas                          |
| ------ | ----------- | --------- | ------------------------------ |
| `GET`  | `/api/ping` | `pong`    | Smoke test. Sin auth, sin DB.  |

CORS (`infrastructure/config/ServerConfig.kt`, clase `CorsConfig`):

- Origins: `http://localhost:3000`, `http://127.0.0.1:3000`
- Methods: `GET POST PUT PATCH DELETE OPTIONS HEAD`
- Headers: `*` · credentials on · `maxAge` 3600s

## Requisitos

- **JDK 21** (toolchain de Gradle). El wrapper descarga Gradle 9.5.1 solo.
- **PostgreSQL** arriba (lo más fácil: `docker compose up db` desde la raíz).
- Un `.env` en la **raíz del repo** (no adentro de `server/`). Copiá `.env.example`.

## Variables de entorno

Spring importa `optional:file:../.env[.properties]` desde `application.yaml`. Compose, además, pasa el `.env` con `env_file` y pisa `POSTGRES_HOST=db`.

```env
POSTGRES_HOST=localhost
POSTGRES_PORT=5432
POSTGRES_DB=
POSTGRES_USER=
POSTGRES_PASSWORD=
```

| Variable            | Local (`./gradlew bootRun`) | Docker Compose          |
| ------------------- | --------------------------- | ----------------------- |
| `POSTGRES_HOST`     | `localhost`                 | `db` (nombre del servicio) |
| `POSTGRES_PORT`     | `5432`                      | `5432`                  |

La URL queda:

```
jdbc:postgresql://${POSTGRES_HOST}:${POSTGRES_PORT}/${POSTGRES_DB}
```

Sin Postgres (o con host/puerto/creds mal), la app **no arranca**: JPA necesita el datasource al levantar el contexto. `/api/ping` no usa la base, pero igual exige que el contexto Spring haya cargado.

## Arranque

Desde la raíz del repo, con el `.env` completo.

**Todo en Docker**

```bash
docker compose up --build
```

API en http://localhost:8080 · ping: http://localhost:8080/api/ping.

**Dev local** (hot reload con DevTools). Postgres en Docker, el server en la JVM:

```bash
docker compose up db
cd server
./gradlew bootRun
```

El IDE también puede correr `ServerApplication`.

Rebuild de la imagen (`docker compose up --build`) cuando cambie `build.gradle.kts`, el `Dockerfile`, o quieras probar el jar de producción. Cambios de código con `bootRun` no piden rebuild.

## Comandos

Correrlos desde `server/`. Usá siempre `./gradlew` (el wrapper), no un Gradle global.

| Comando                     | Qué hace                                                                 |
| --------------------------- | ------------------------------------------------------------------------ |
| `./gradlew bootRun`         | Levanta la app en `:8080`. DevTools recarga al recompilar.               |
| `./gradlew test`            | Suite JUnit 5.                                                           |
| `./gradlew ktlintFormat`    | **Formatea** `.kt` / `.kts` (ktlint 1.5). Escribe en disco.              |
| `./gradlew ktlintCheck`     | Solo chequea estilo. Sale ≠ 0 si hay diff. No toca archivos.             |
| `./gradlew detekt`          | Análisis estático (code smells, complejidad). Config default de detekt.  |
| `./gradlew compileKotlin`   | Compila `main` sin tests.                                                |
| `./gradlew check`           | Verificación completa: ktlint + detekt + tests.                          |
| `./gradlew bootJar`         | Fat jar ejecutable en `build/libs/` (`server-0.0.1-SNAPSHOT.jar`).       |
| `./gradlew build`           | `check` + empaquetado.                                                   |
| `./gradlew clean`           | Borra `build/`.                                                          |
| `./gradlew tasks`           | Lista las tasks del proyecto.                                            |

Antes de un PR, en este orden:

```bash
./gradlew ktlintFormat
./gradlew detekt
./gradlew test
```

o, equivalente:

```bash
./gradlew ktlintFormat check
```

Estilo: 4 espacios, línea de 120 (`server/.editorconfig`). `ktlint.ignoreFailures` está en `false`: un lint roto **falla el build**.

### Pre-commit

El hook del repo, sobre `server/`:

1. `ktlintCheck` → si falla, corre `ktlintFormat` y **aborta el commit** (hay que stagear de nuevo).
2. `detekt`
3. `compileKotlin`

Instalarlo una vez desde la raíz:

```bash
./hooks/install.sh
```

## Tests

```bash
./gradlew test
```

Hoy hay un `@SpringBootTest` que solo verifica que el contexto carga (`ServerApplicationTests`). Eso **necesita Postgres**: es un test de arranque, no un unitario.

Para reglas de negocio, preferí tests del application/domain service **sin** levantar Spring (mock del repository). Dejá `@SpringBootTest` para humo / integración.

Reportes:

- Tests: `build/reports/tests/test/index.html`
- ktlint: `build/reports/ktlint/`
- detekt: `build/reports/detekt/`

## Docker

`Dockerfile` de dos etapas:

1. **build** — `eclipse-temurin:21-jdk`, copia el wrapper y el `src`, corre `./gradlew bootJar --no-daemon -x test`, se queda con el jar (no el `-plain`).
2. **runtime** — `eclipse-temurin:21-jre`, `java -jar app.jar`, puerto **8080**.

Compose (`backend` en la raíz):

```yaml
backend:
  build: ./server
  depends_on:
    db:
      condition: service_healthy
  env_file: .env
  environment:
    POSTGRES_HOST: db
    SPRING_PROFILES_ACTIVE: docker
  ports:
    - "8080:8080"
```

Adentro de la red de Compose el host de Postgres es `db`, no `localhost`. El healthcheck de `db` evita que el server arranque antes de que Postgres acepte conexiones.

```bash
docker compose up --build backend   # server + db
docker compose logs -f backend
docker compose down                 # contenedores; el volumen de Postgres queda
```

## Troubleshooting corto

| Síntoma | Qué mirar |
| ------- | --------- |
| La app no arranca / error de datasource | `.env` en la raíz, `POSTGRES_*`, Postgres levantado (`docker compose up db`). |
| CORS en el browser, Postman anda | Origins/methods en `CorsConfig`. `PATCH`/`DELETE` tienen que estar en `allowedMethods`. |
| `ktlintCheck` rojo en el hook | `./gradlew ktlintFormat`, stageá, commit de nuevo. |
| Cambios que no aparecen en Docker | Falta `--build`. En dev usá `bootRun`, no la imagen. |
| Puerto 5432 ocupado | Hay otro Postgres en el host. Paralo o cambiá `POSTGRES_PORT`. |
