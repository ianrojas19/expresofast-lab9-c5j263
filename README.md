# ExpresoFast - Laboratorio 9

**Curso:** IF0009 - Desarrollo de Software IV  
**Ciclo:** II-2026  
**Laboratorio:** 9 - Consola Logística "ExpresoFast" - Procedimientos Almacenados, Paginación Relacional y Vistas HTML5 Paginadas  
**Estudiante:** Ian Rojas Sequeira  
**Carnet:** C5J263  

---

## Descripción

Quinta parte de la plataforma logística ExpresoFast. Implementación enfocada en la migración a una base de datos en memoria (H2), el diseño de consultas eficientes mediante paginación del lado del servidor, y la invocación de Procedimientos Almacenados mapeados con Java JDBC. Todo ello consumido asíncronamente desde una interfaz HTML5 paginada de aspecto profesional.

---

## Requisitos de Entorno

| Herramienta | Versión |
|---|---|
| Java | 21 |
| Maven | 3.9+ |
| Navegador | Chrome / Edge (con DevTools) |
| IDE | VS Code / IntelliJ IDEA |

---

## Estructura del Repositorio

```
expresofast-lab9-c5j263/
├── backend/          → Proyecto Spring Boot
│   ├── src/
│   │   ├── main/java/.../procedure/   → Mapeo de Procedimientos Almacenados (H2/JDBC)
│   │   └── main/resources/
│   │       ├── application.properties → Config de BD en Memoria
│   │       ├── schema.sql             → Esquemas y Alias (H2) para Procedimientos Almacenados
│   │       └── data.sql               → Inserción de 32 envíos de prueba generados
│   └── pom.xml
├── frontend/
│   ├── dashboard_paginado.html → Nuevo Dashboard Paginado
│   ├── login.html              → Pantalla de inicio de sesión
│   ├── styles.css              → Estilos sobrios y responsivos
│   └── app.js                  → Lógica JS con JWT y paginación
└── README.md
```

---

## Configuración de Base de Datos

En este laboratorio, se migró la arquitectura a usar **H2 Database en memoria**.
**NO es necesario ejecutar ningún script SQL manual.**

Al iniciar el backend, Spring Boot lee automáticamente:
1. `schema.sql`: Para crear las tablas de base y registrar los ALIAS de funciones en Java que simulan los Stored Procedures.
2. `data.sql`: Para insertar los usuarios y generar 32 envíos distribuidos para las pruebas de paginación.

> **Nota:** La información se reinicia cada vez que se reinicia el servidor.

---

## Usuarios de Prueba

| Usuario | Contraseña | Rol | Permisos |
|---|---|---|---|
| admin | Password123! | ROLE_ADMIN | Todo: crear envíos, cambiar estado, ver bitácora, gestionar flota |
| operador1 | Password123! | ROLE_OPERADOR | Crear envíos, ver bitácora |
| conductor1 | Password123! | ROLE_CONDUCTOR | Ver envíos, cambiar estado |

---

## Instrucciones de Ejecución

### Backend (Spring Boot)

1. **Asegurar propiedades de la BD** en `backend/src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:h2:mem:expresofastdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
   spring.datasource.driverClassName=org.h2.Driver
   spring.datasource.username=sa
   spring.datasource.password=
   ```

2. **Compilar y ejecutar:**
   ```bash
   cd backend
   mvn spring-boot:run
   ```
   El servidor arranca en `http://localhost:8080`

### Frontend (HTML/JS)

Servir los archivos con un servidor HTTP local (python instalado requerido previamente):
```bash
cd frontend
python -m http.server 5500
# Alternativa: Usar la extensión "Live Server" de VS Code
```

Acceder en: `http://localhost:5500/dashboard_paginado.html`

---

## Endpoints de la API

| Método | Endpoint | Roles | Descripción |
|---|---|---|---|
| GET | `/api/v1/envios?page={N}&size={S}` | ADMIN, OPERADOR, CONDUCTOR | Paginación con Sort (Pageable) |
| GET | `/api/v1/envios/procedimiento/resumen` | ADMIN, OPERADOR, CONDUCTOR | Ejecuta SP (`SP_GenerarResumenMetricas`) vía JDBC |
| POST | `/api/auth/login` | Público | Autenticación JWT |
| PATCH | `/api/envios/{id}/estado` | ADMIN, CONDUCTOR | Cambio de estado |

*(Más endpoints heredados del Lab 8)*

---

## Funcionalidades Implementadas (Lab 9)

- **Procedimientos Almacenados con H2 y Java:** Implementación estática de métodos Java en `StoredProcedures.java`, mapeados vía ALIAS en `schema.sql` y consumidos mediante `CallableStatement` / `PreparedStatement` usando `Connection` en la capa de servicios.
- **Paginación Relacional:** Uso de `Pageable` e interfaz `Page<T>` con JpaRepository para delegar la carga al manejador de base de datos (`OFFSET` y `FETCH NEXT`).
- **Dashboard Paginado:** Refactorización a un frontend moderno (`dashboard_paginado.html`), diseño minimalista "Dashboard Admin" sobrio, con controles de navegación integrados y carga asíncrona de datos desde el SP y endpoints paginados.
- **Métricas Reales:** Los KPI en el dashboard ya no son ficticios, provienen del cálculo del SP `SP_GenerarResumenMetricas` que devuelve un ResumenMetricasDTO.
