# 🏥 VetControl API

Sistema de gestión veterinaria completo desarrollado con **Spring Boot 3**, **PostgreSQL** y **JWT**.

## 🚀 Tecnologías

- **Java 21** + Spring Boot 3.4
- **PostgreSQL 16** + Flyway (migraciones)
- **Spring Security 6** + JWT
- **Spring Data JPA** + Hibernate
- **Swagger/OpenAPI 3** (documentación automática)
- **Docker** + Docker Compose
- **Lombok** + Validaciones

## 📁 Estructura del Proyecto

```
vetcontrol-api/
├── src/main/java/com/vetcontrol/api/
│   ├── config/          # Configuraciones (OpenAPI, CORS)
│   ├── controller/      # Controladores REST
│   ├── dto/
│   │   ├── request/     # DTOs de entrada
│   │   └── response/    # DTOs de salida
│   ├── entity/          # Entidades JPA
│   │   └── enums/       # Enumeraciones
│   ├── exception/       # Excepciones y manejador global
│   ├── repository/      # Repositorios Spring Data
│   ├── security/        # JWT, filtros, configuración de seguridad
│   ├── seed/            # Datos iniciales de prueba
│   └── service/         # Lógica de negocio
├── src/main/resources/
│   ├── db/migration/    # Scripts Flyway
│   └── application.yml  # Configuración
├── Dockerfile
├── docker-compose.yml
└── pom.xml
```

## 🐳 Ejecución con Docker

```bash
# Clonar y ejecutar
git clone <repo>
cd vetcontrol-api
docker-compose up -d

# La API estará disponible en:
# http://localhost:8080/api/v1
```

## 🔧 Ejecución Local

### Requisitos
- Java 21
- Maven 3.9+
- PostgreSQL 16

### Pasos

```bash
# 1. Crear base de datos en PostgreSQL
CREATE DATABASE vetcontrol;

# 2. Configurar variables de entorno (copiar .env.example a .env)
cp .env.example .env

# 3. Ejecutar la aplicación
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# O compilar y ejecutar
mvn clean package -DskipTests
java -jar target/vetcontrol-api-1.0.0.jar
```

## 📚 Documentación API

Una vez ejecutada la aplicación:

- **Swagger UI**: http://localhost:8080/api/v1/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api/v1/v3/api-docs

## 🔐 Autenticación

La API usa **JWT Bearer Token**. Todos los endpoints (excepto auth) requieren el header:

```
Authorization: Bearer <token>
```

### Credenciales de Prueba (perfil dev)

| Usuario | Contraseña | Rol |
|---------|-----------|-----|
| admin | admin123 | ADMIN |
| drgarcia | vet123 | VETERINARIAN |
| dramirez | vet123 | VETERINARIAN |
| recepcion | rec123 | RECEPTIONIST |

### Login
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

## 📡 Endpoints Principales

### Autenticación
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/auth/login` | Iniciar sesión |
| POST | `/auth/register` | Registrar usuario |

### Usuarios (ADMIN)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/users` | Listar usuarios (paginado) |
| GET | `/users/{id}` | Obtener usuario |
| POST | `/users` | Crear usuario |
| PUT | `/users/{id}` | Actualizar usuario |
| DELETE | `/users/{id}` | Desactivar usuario |

### Clientes
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/clients` | Listar clientes |
| GET | `/clients/{id}` | Obtener cliente |
| GET | `/clients/dni/{dni}` | Buscar por DNI |
| POST | `/clients` | Crear cliente |
| PUT | `/clients/{id}` | Actualizar cliente |
| DELETE | `/clients/{id}` | Eliminar cliente |

### Mascotas
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/pets` | Listar mascotas |
| GET | `/pets/{id}` | Obtener mascota |
| GET | `/pets/client/{clientId}` | Mascotas por cliente |
| GET | `/pets/species` | Listar especies |
| POST | `/pets` | Crear mascota |
| PUT | `/pets/{id}` | Actualizar mascota |
| DELETE | `/pets/{id}` | Eliminar mascota |

### Citas
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/appointments` | Listar citas |
| GET | `/appointments/{id}` | Obtener cita |
| GET | `/appointments/date/{date}` | Citas por fecha |
| POST | `/appointments` | Crear cita |
| PUT | `/appointments/{id}` | Actualizar cita |
| PATCH | `/appointments/{id}/status` | Cambiar estado |
| DELETE | `/appointments/{id}` | Eliminar cita |

### Historia Clínica
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/medical-records` | Listar registros |
| GET | `/medical-records/{id}` | Obtener registro |
| GET | `/medical-records/pet/{petId}` | Registros por mascota |
| POST | `/medical-records` | Crear registro |
| PUT | `/medical-records/{id}` | Actualizar registro |
| DELETE | `/medical-records/{id}` | Eliminar registro |

### Productos
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/products` | Listar productos |
| GET | `/products/{id}` | Obtener producto |
| GET | `/products/type/{type}` | Por tipo |
| GET | `/products/active` | Activos |
| POST | `/products` | Crear producto |
| PUT | `/products/{id}` | Actualizar producto |
| DELETE | `/products/{id}` | Desactivar producto |

### Facturación
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/invoices` | Listar facturas |
| GET | `/invoices/{id}` | Obtener factura |
| POST | `/invoices` | Crear factura |
| PATCH | `/invoices/{id}/status` | Cambiar estado |
| DELETE | `/invoices/{id}` | Eliminar factura |

### Dashboard (ADMIN)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/dashboard/stats` | Estadísticas generales |

## 🗄️ Modelo de Base de Datos

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│    users    │     │   clients   │     │    pets     │
├─────────────┤     ├─────────────┤     ├─────────────┤
│ id (PK)     │     │ id (PK)     │◄────┤ id (PK)     │
│ username    │     │ full_name   │     │ name        │
│ email       │     │ dni (UQ)    │     │ species     │
│ password    │     │ phone       │     │ breed       │
│ full_name   │     │ email       │     │ age         │
│ role        │     │ address     │     │ client_id   │
│ active      │     └─────────────┘     └─────────────┘
└─────────────┘              │                  │
       ▲                     │                  │
       │                     │                  │
       │            ┌────────┴────────┐        │
       │            │                 │        │
┌──────┴──────┐    │        ┌────────┴────────┴────────┐
│appointments │    │        │    medical_records       │
├─────────────┤    │        ├──────────────────────────┤
│ id (PK)     │    │        │ id (PK)                  │
│ pet_id (FK) │────┘        │ pet_id (FK)              │
│ vet_id (FK) │◄────────────┤ veterinarian_id (FK)     │
│ date        │             │ diagnosis                │
│ time        │             │ treatment                │
│ status      │             │ observations             │
└─────────────┘             └──────────────────────────┘
       │
       │
┌──────┴──────┐     ┌─────────────┐     ┌─────────────┐
│  invoices   │     │invoice_items│     │  products   │
├─────────────┤     ├─────────────┤     ├─────────────┤
│ id (PK)     │◄────┤ id (PK)     │────►│ id (PK)     │
│ number (UQ) │     │ invoice_id  │     │ name        │
│ client_id   │     │ product_id  │     │ type        │
│ app_id (FK) │     │ description │     │ price       │
│ subtotal    │     │ quantity    │     │ stock       │
│ tax         │     │ unit_price  │     │ active      │
│ total       │     │ subtotal    │     └─────────────┘
│ status      │     └─────────────┘
└─────────────┘
```

## 🔒 Roles y Permisos

| Recurso | ADMIN | VETERINARIAN | RECEPTIONIST |
|---------|:-----:|:------------:|:------------:|
| Usuarios | ✅ CRUD | ❌ | ❌ |
| Clientes | ✅ CRUD | ✅ Ver | ✅ CRUD |
| Mascotas | ✅ CRUD | ✅ Ver | ✅ CRUD |
| Citas | ✅ CRUD | ✅ Ver/Editar | ✅ CRUD |
| Historia Clínica | ✅ CRUD | ✅ CRUD | ❌ |
| Productos | ✅ CRUD | ✅ Crear/Editar | ❌ |
| Facturas | ✅ CRUD | ❌ | ✅ CRUD |
| Dashboard | ✅ | ❌ | ❌ |

## 📊 Paginación y Filtros

Todos los endpoints de listado soportan:

```
GET /clients?search=juan&page=0&size=10&sort=fullName,asc
GET /pets?species=Perro&clientId=1&page=0&size=20
GET /appointments?status=PENDING&dateFrom=2026-06-01&dateTo=2026-06-30
GET /invoices?status=PAID&search=F1234
```

## 📝 Logs

Los logs se configuran en `application.yml`:
- Nivel DEBUG para la aplicación
- Nivel TRACE para consultas SQL
- Formato estructurado con timestamp, thread, nivel y mensaje

## 🧪 Testing

```bash
# Ejecutar tests
mvn test

# Ejecutar con cobertura
mvn jacoco:report
```

## 📄 Licencia

MIT License - 2026 VetControl
#   v e t c o n t r o l - a p i  
 