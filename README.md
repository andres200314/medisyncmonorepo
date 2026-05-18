# MediSync

Plataforma para conectar pacientes con farmacias en Medellín y consultar disponibilidad de medicamentos en tiempo real.

## Estructura del monorepo

```
medisyncmonorepo/
├── medisync/           # Backend — Spring Boot 3.5, Java 25, PostgreSQL
├── medisync-front/     # Frontend — React 19, Vite, Tailwind CSS
├── docker-compose.yml  # Stack completo (recomendado para empezar)
└── README.md
```

| Servicio   | Puerto (local) | Descripción                          |
|-----------|----------------|--------------------------------------|
| Frontend  | `5173`         | Interfaz web (Vite en dev, nginx en Docker) |
| Backend   | `8080`         | API REST + Swagger                   |
| PostgreSQL| `5432`         | Base de datos `medisync_db`          |

---

## Requisitos previos

Elige **una** de estas formas de trabajar:

### Opción A — Todo con Docker

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) (o Docker Engine + Compose v2)

### Opción B — Desarrollo local

- **Java 25** (JDK)
- **Node.js 22+** y npm
- **PostgreSQL 15+** (o solo Postgres vía Docker, ver abajo)
- **Gradle**: incluido en `medisync/gradlew` (no hace falta instalarlo globalmente)

---

## Inicio rápido con Docker (recomendado)

Desde la **raíz del monorepo**:

```bash
docker compose up --build
```

La primera vez puede tardar varios minutos (descarga de imágenes y compilación).

Cuando los contenedores estén en marcha:

| Recurso        | URL                          |
|----------------|------------------------------|
| Aplicación web | http://localhost:5173        |
| API            | http://localhost:8080/api    |
| Swagger UI     | http://localhost:5173/docs   |

En Docker, nginx del frontend hace proxy de `/api` hacia el backend; no necesitas configurar CORS manualmente en el navegador.

Detener los servicios:

```bash
docker compose down
```

Eliminar también el volumen de la base de datos:

```bash
docker compose down -v
```

---

## Desarrollo local (sin Docker en app)

Útil si quieres depurar o recargar cambios al instante en frontend/backend.

### 1. Base de datos

**Solo Postgres con Docker** (desde la raíz):

```bash
docker compose up postgres -d
```

**O Postgres instalado en tu máquina**: crea la base `medisync_db` y ajusta usuario/contraseña en `medisync/src/main/resources/application.yaml` si no usas los valores por defecto (`postgres` / `postgres`).

### 2. Backend

```bash
cd medisync

# Windows
gradlew.bat bootRun

# Linux / macOS
./gradlew bootRun
```

Variables de entorno opcionales:

| Variable        | Por defecto              | Uso                    |
|-----------------|--------------------------|------------------------|
| `DB_USERNAME`   | `postgres`               | Usuario PostgreSQL     |
| `DB_PASSWORD`   | `postgres`               | Contraseña PostgreSQL  |
| `JWT_SECRET`    | (valor en `application.yaml`) | Firma del JWT   |
| `JWT_EXPIRATION`| `86400000`               | Expiración del token (ms) |

Comprueba que responde: http://localhost:8080/docs

### 3. Frontend

En otra terminal:

```bash
cd medisync-front
npm install
npm run dev
```

El archivo `.env.development` ya apunta al backend:

```
VITE_API_BASE_URL=http://localhost:8080
```

Vite también proxifica `/api` a `http://localhost:8080` (ver `vite.config.js`), así que las peticiones funcionan aunque uses rutas relativas.

Abre: http://localhost:5173

> **Importante:** el backend debe estar corriendo en el puerto `8080` antes de buscar medicamentos o iniciar sesión.

---

## Scripts útiles

### Frontend (`medisync-front/`)

| Comando           | Descripción              |
|-------------------|--------------------------|
| `npm run dev`     | Servidor de desarrollo   |
| `npm run build`   | Build de producción      |
| `npm run preview` | Vista previa del build   |
| `npm run lint`    | ESLint                   |

### Backend (`medisync/`)

| Comando              | Descripción                    |
|----------------------|--------------------------------|
| `./gradlew bootRun`  | Arrancar la aplicación         |
| `./gradlew test`     | Tests unitarios + reporte JaCoCo |
| `./gradlew build`    | Compilar y empaquetar JAR      |

---

## Docker por carpeta (alternativa)

Si prefieres levantar solo el stack definido dentro de `medisync/` (mismos servicios, rutas de build relativas a esa carpeta):

```bash
cd medisync
docker compose up --build
```

El `docker-compose.yml` de la raíz del monorepo es el equivalente pensado para trabajar desde el directorio padre (`./medisync` y `./medisync-front`).

---

## Flujo típico de uso

1. **Paciente:** en el home, busca un medicamento por nombre.
2. **Farmacia:** regístrate en «Registrar mi farmacia», inicia sesión y gestiona inventario desde el dashboard.
3. **API:** documentación interactiva en `/docs` (Swagger).

Endpoints principales:

- `POST /api/auth/register` — registro de farmacia
- `POST /api/auth/login` — login (JWT)
- `GET /api/inventario/disponibilidad?medicamento=...` — búsqueda pública
- `GET /api/inventario/mi-inventario` — inventario del gestor (requiere token)

---

## Problemas frecuentes

### «No hay conexión con el servidor» en el home

- Verifica que el backend esté activo: http://localhost:8080/docs
- En desarrollo local, confirma `VITE_API_BASE_URL=http://localhost:8080` en `.env.development`

### Error de conexión a PostgreSQL

- Postgres debe estar escuchando en `localhost:5432`
- Con Docker: `docker compose ps` y revisa que `medisync-postgres` esté `healthy`

### Puerto 5432 o 8080 ya en uso

- Cierra otras instancias de Postgres/Spring Boot, o cambia el mapeo de puertos en `docker-compose.yml`

### `gradlew` / `mvn` no reconocido

- Usa el wrapper del proyecto: `medisync/gradlew.bat` (Windows) o `medisync/gradlew` (Unix), desde la carpeta `medisync`

### Java 25

El backend está configurado con toolchain Java 25. Si `bootRun` falla por versión, instala JDK 25 o ajusta `languageVersion` en `medisync/build.gradle` (solo para entornos que no soporten 25 aún).

---

## Tecnologías

| Capa      | Stack                                              |
|-----------|----------------------------------------------------|
| Backend   | Spring Boot 3.5, Spring Security, JPA, PostgreSQL, JWT, SpringDoc |
| Frontend  | React 19, Vite 8, Tailwind CSS 4                   |
| Infra     | Docker Compose, nginx (frontend en producción)     |

---

## Licencia

Proyecto académico / interno — consulta con el equipo antes de redistribuir.
