# Client — Biblioteca

Frontend de SplitIt Training: un catálogo de biblioteca en **Next.js** (App Router) que habla con la API de Spring Boot.

La app deja ver libros, filtrarlos por título y reservar una copia. No hay auth todavía: el circuito es página → hook → `fetch` → `/api/*`.

```
browser  →  Next.js :3000  →  Spring Boot :8080  →  Postgres
```

## Stack

| Pieza       | Qué                                                                               |
| ----------- | --------------------------------------------------------------------------------- |
| Runtime     | Next.js 16 · React 19 · TypeScript (strict)                                       |
| Estilos     | Tailwind CSS 4 · shadcn/ui (`base-nova`) · Base UI · Lucide                       |
| Formularios | React Hook Form · Zod · `@hookform/resolvers` (instalados, listos para usar)      |
| Calidad     | ESLint 9 (`eslint-config-next` + Prettier) · Prettier + plugin de clases Tailwind |
| Empaquetado | npm (`package-lock.json`) · imagen Docker `standalone`                            |

El alias `@/*` apunta a la raíz de `client/` (`@/lib/api`, `@/components/ui/button`, etc.).

## Cómo está armado

App Router. Las **páginas** son Server Components que montan **Client Components** donde hace falta estado, fetch o clicks. El layout (`app/layout.tsx`) pone el header “Biblioteca”, las fuentes Geist y el contenedor de `max-w-2xl`.

```
client/
├── app/                     # rutas
│   ├── layout.tsx           # shell (header + <main>)
│   ├── page.tsx             # GET /  — catálogo
│   ├── books/[id]/page.tsx  # GET /books/:id
│   ├── test/page.tsx        # GET /test — smoke ping al backend
│   └── globals.css          # tokens Tailwind + shadcn
├── components/
│   ├── book-catalog.tsx     # búsqueda + lista
│   ├── book-detail.tsx      # ficha + reservar
│   └── ui/                  # primitivos shadcn (no editar a mano si podés evitarlo)
├── lib/
│   ├── types.ts             # Book, Loan
│   ├── api.ts               # cliente HTTP
│   ├── books.ts             # filtro, fechas, préstamo activo
│   ├── library.tsx          # hooks useBooks / useBook
│   └── utils.ts             # cn()
├── next.config.ts           # carga ../.env · output: "standalone"
├── eslint.config.mjs
├── .prettierrc
└── Dockerfile
```

Flujo de datos:

1. `lib/api.ts` arma la URL con `NEXT_PUBLIC_API_URL`, manda JSON y traduce errores a `ApiError`.
2. `lib/library.tsx` (`"use client"`) expone `useBooks` (catálogo) y `useBook` (ficha + `reserve()`). Manejan loading, error, 404 y el POST de préstamo.
3. Los componentes de `components/` solo renderizan. El filtro por título vive en `lib/books.ts` (client-side, sin query al server).

### Rutas

| URL          | Qué hace                                                                    |
| ------------ | --------------------------------------------------------------------------- |
| `/`          | Catálogo. Busca por título (sin acentos) y muestra copias disponibles.      |
| `/books/:id` | Detalle. Reserva con `POST /api/loans` si hay stock.                        |
| `/test`      | Smoke test: pega a `{NEXT_PUBLIC_API_URL}/api/ping` y muestra la respuesta. |

### API que consume

| Método | Path             | Uso                                                    |
| ------ | ---------------- | ------------------------------------------------------ |
| `GET`  | `/api/books`     | Listar catálogo                                        |
| `GET`  | `/api/books/:id` | Un libro                                               |
| `GET`  | `/api/loans`     | Préstamos (para saber si este libro ya está reservado) |
| `POST` | `/api/loans`     | Reservar (`{ "bookId": number }`)                      |
| `GET`  | `/api/ping`      | Solo la página `/test`                                 |

## Requisitos

- Node 22 (la imagen Docker usa `node:22-alpine`)
- npm
- Backend en `:8080` (o el host que pongas en `NEXT_PUBLIC_API_URL`)

## Variables de entorno

Next carga el `.env` **de la raíz del repo**, no uno adentro de `client/`. `next.config.ts` hace `dotenvConfig({ path: "../.env" })`.

Lo que usa el front:

```env
NEXT_PUBLIC_API_URL=http://localhost:8080
```

`NEXT_PUBLIC_*` se inyecta en el bundle. En Docker se pasa como **build arg** (queda horneada en la imagen). Si cambiás la URL, hay que rebuildear el frontend.

## Arranque

Desde la raíz del repo, con el `.env` ya copiado de `.env.example`.

**Todo en Docker**

```bash
docker compose up --build
```

Front en http://localhost:3000.

**Solo el front, en local** (el backend y Postgres tienen que estar arriba):

```bash
cd client
npm install
npm run dev
```

Hot reload en http://localhost:3000. Postgres más cómodo:

```bash
docker compose up db
```

y el API con `./gradlew bootRun` desde `server/`.

## Scripts

Correrlos desde `client/`.

| Comando                | Qué hace                                                         |
| ---------------------- | ---------------------------------------------------------------- |
| `npm run dev`          | Dev server (Turbopack).                                          |
| `npm run build`        | Build de producción (`.next/`, incluye `standalone`).            |
| `npm start`            | Sirve el build. Primero `npm run build`.                         |
| `npm run format`       | Prettier escribe (incluye orden de clases Tailwind).             |
| `npm run format:check` | Prettier solo chequea; sale ≠ 0 si hay diff.                     |
| `npm run lint`         | ESLint en todo el árbol, **cero warnings** (`--max-warnings=0`). |
| `npm run lint:fix`     | ESLint con `--fix`.                                              |
| `npx tsc --noEmit`     | Typecheck. No hay script `typecheck`; es este comando.           |

Prettier: `semi`, comillas dobles, `trailingComma: "es5"`, `printWidth: 100`, plugin `prettier-plugin-tailwindcss`. ESLint usa `eslint-config-next` (core-web-vitals + TypeScript) y `eslint-config-prettier` para no pelearse con el formatter.

Antes de un PR, en este orden:

```bash
npm run format
npm run lint
npx tsc --noEmit
```

El `pre-commit` del repo hace `format:check` → si falla formatea y **aborta el commit** (hay que stagear de nuevo) → `lint` → `tsc --noEmit`. Instalarlo una vez desde la raíz:

```bash
./hooks/install.sh
```

El hook llama a **pnpm** (`pnpm format:check`, etc.). Para desarrollarlo a mano, usá **npm**: es el lockfile y lo que usa el `Dockerfile`.

## Docker

`Dockerfile` de tres etapas: `npm ci` → `next build` con `NEXT_PUBLIC_API_URL` → runner con el output `standalone` (`node server.js`, user `nextjs`, puerto 3000).

Compose construye el servicio `frontend` así:

```yaml
build:
  context: ./client
  args:
    NEXT_PUBLIC_API_URL: ${NEXT_PUBLIC_API_URL}
```

Cambios de código en `npm run dev` no piden rebuild. Rebuild (`docker compose up --build`) cuando cambie `package.json`, el `Dockerfile`, o `NEXT_PUBLIC_API_URL`.

## UI (shadcn)

Estilo `base-nova`, CSS variables, iconos Lucide. Los primitivos viven en `components/ui/` y se generan con:

```bash
npx shadcn@latest add <componente>
```

No hace falta un `tailwind.config`: Tailwind 4 entra por PostCSS (`@tailwindcss/postcss`) y los tokens están en `app/globals.css`.
