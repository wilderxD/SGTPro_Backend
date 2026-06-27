# SGTPro — Sistema de Gestión de Taller Automotriz

Backend REST para la gestión de órdenes de trabajo, vehículos, usuarios y catálogo de insumos en un taller automotriz.

## Stack

| Componente | Tecnología |
|---|---|
| Lenguaje | Java 17 |
| Framework | Spring Boot 3.2.5 |
| Persistencia | Spring Data JPA (Hibernate) |
| BD | MySQL 8+ |
| Seguridad | Spring Security + JWT (jjwt 0.11.5) |
| Documentación API | SpringDoc OpenAPI 2.5.0 (Swagger UI) |
| Tests | JUnit 5 + Mockito + MockMvc |
| Build | Maven |
| Contenedores | Docker + Docker Compose |

## Requisitos

- JDK 17
- Maven 3.8+ (o usar `mvnw` si está disponible)
- MySQL 8+ (o Docker para levantar la BD automáticamente)
- Docker y Docker Compose (opcional)

## Perfiles de ejecución

| Perfil | Uso | DDL | BD |
|---|---|---|---|
| `dev` | Desarrollo local | `update` | MySQL |
| `prod` | Producción | `validate` | MySQL |
| `test` | Tests unitarios | `update` | MySQL |
| `integrationtest` | Tests de integración | `create-drop` | H2 (embebida) |

## Ejecución

### Con Docker (recomendado)

```bash
# Levantar MySQL + app
docker compose up --build

# Solo MySQL (app corriendo localmente)
docker compose up mysql -d
```

### Con Maven local

```bash
# Asegúrate de tener MySQL corriendo en localhost:3306 con DB "sgtpro"

# Desarrollo
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Producción
mvn package -B
java -jar target/SGTPRO-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

## Variables de entorno

| Variable | Default | Descripción |
|---|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/sgtpro` | URL de conexión MySQL |
| `DB_USERNAME` | `root` | Usuario BD |
| `DB_PASSWORD` | `admin` | Contraseña BD |
| `JWT_SECRET` | *(base64 internos)* | Clave secreta para firmar JWT |
| `JWT_EXPIRATION` | `86400000` | Expiración del token (ms) |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:3000,http://localhost:4200` | Orígenes permitidos CORS |
| `PAGE_SIZE` | `8` | Tamaño de paginación |
| `SPRING_PROFILES_ACTIVE` | *(vacío)* | Perfil activo de Spring |

## Endpoints principales

### Autenticación
| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/api/auth/login` | Login, devuelve JWT |

### Órdenes de Trabajo (OT)
| Método | Ruta | Roles |
|---|---|---|
| `GET` | `/api/ordenes?estado=&placa=` | Todos |
| `GET` | `/api/ordenes/{idOt}` | Todos |
| `POST` | `/api/ordenes` | JEFE_TALLER, JEFE_DIRECTO |
| `POST` | `/api/ordenes/{idOt}/requerimientos` | MECANICO, JEFE_TALLER |
| `PATCH` | `/api/ordenes/requerimientos/{id}/despachar` | LOGISTICA |
| `PATCH` | `/api/ordenes/{idOt}/finalizar` | JEFE_TALLER, JEFE_DIRECTO |
| `PATCH` | `/api/ordenes/{idOt}/cancelar` | JEFE_TALLER, JEFE_DIRECTO |

### Maestros
| Método | Ruta | Entidad |
|---|---|---|
| `GET/POST/PUT/DELETE` | `/api/vehiculos[/{placa}]` | Vehículos |
| `GET/POST/PUT/DELETE` | `/api/usuarios[/{id}]` | Usuarios |
| `GET/POST/PUT/DELETE` | `/api/catalogoInsumos[/{id}]` | Catálogo de insumos |

## Documentación de la API

Disponible en: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## Tests

```bash
# Todos los tests
mvn test

# Tests de integración (requiere perfil integrationtest)
mvn verify -Dspring.profiles.active=integrationtest
```

El pipeline CI (GitHub Actions) ejecuta automáticamente los tests en cada push.
