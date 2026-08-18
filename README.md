# SplitIt

Repo de entrenamiento de **Laboratorio II** (Austral). Un stack full-stack listo para construir una app de dividir gastos: frontend en Next.js, API en Spring Boot + Kotlin, y Postgres atrás.

```
┌─────────────┐     /api/*      ┌─────────────┐     JDBC      ┌─────────────┐
│   Next.js   │ ──────────────► │ Spring Boot │ ────────────► │  Postgres   │
│   :3000     │                 │    :8080    │               │    :5432    │
└─────────────┘                 └─────────────┘               └─────────────┘
```

Si el backend responde `pong` a `/api/ping`, estás en negocio.

## Stack

| Capa        | Tecnología                          |
| ----------- | ----------------------------------- |
| Frontend    | Next.js 16 · React 19 · Tailwind 4 · shadcn |
| Backend     | Spring Boot 4 · Kotlin · Java 21 · JPA |
| Base de datos | PostgreSQL 15                     |
| Runtime     | Docker Compose                      |

## Estructura

```
splitit-training/
├── client/              # Next.js
├── server/              # Spring Boot (Kotlin)
├── docker-compose.yml
└── .env.example
```

## Arranque rápido

Necesitás [Docker](https://docs.docker.com/get-docker/) y Docker Compose.

```bash
cp .env.example .env
# completá las variables

docker compose up --build
```

| Servicio  | URL                                      |
| --------- | ---------------------------------------- |
| Frontend  | http://localhost:3000                    |
| Backend   | http://localhost:8080                    |
| Ping      | http://localhost:8080/api/ping → `pong`  |
| Postgres  | `localhost:5432`                         |

Hay una página de smoke test en `/test` que pega al ping del backend.

## Variables de entorno

Copiá `.env.example` y llenalo. Lo mínimo:

```env
POSTGRES_USER=
POSTGRES_PASSWORD=
POSTGRES_DB=
POSTGRES_PORT=5432
POSTGRES_HOST=localhost

NEXT_PUBLIC_API_URL=http://localhost:8080
```

En Docker, Compose pisa `POSTGRES_HOST` a `db` (el nombre del servicio). En local, dejalo en `localhost`.

## Desarrollo local (sin Docker para todo)

Postgres sí o sí tiene que estar corriendo. Lo más fácil:

```bash
docker compose up db
```

**Backend**

```bash
cd server
./gradlew bootRun
```

**Frontend**

```bash
cd client
npm install
npm run dev
```

## Tests

```bash
cd server
./gradlew test
```

---

Laboratorio II · Universidad Austral
```
