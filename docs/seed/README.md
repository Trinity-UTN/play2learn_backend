# Database Seed — Guía de uso

## Cuándo usar

Después de cambiar `spring.jpa.hibernate.ddl-auto=create` y reiniciar la aplicación con una base de datos vacía.

## Pasos

1. Configurar `ddl-auto=create` (temporalmente, en desarrollo).
2. Arrancar la app con perfil `desarrollo` (`app.seed.enabled=true` ya está en `application-desarrollo.properties`).
3. **BD vacía:** llamar `POST /api/dev/seed/bootstrap` (sin autenticación, solo si no hay usuarios).
4. **BD con DEV existente:** llamar `POST /api/dev/seed` con token JWT de `ROLE_DEV`.

## Qué genera

| Entidad | Cantidad |
|---------|----------|
| Reserve | 1 |
| Years | 6 (Primero–Sexto) |
| Courses | 12 (2 por año: A, B) |
| Teachers | 6 mínimo |
| Students | 180 (30 por año, 15 por curso) |
| Subjects | 36 (3 por curso) |
| Aspects | 9 (desde `docs/aspects/`) |

## Credenciales

Tras ejecutar el seed se genera `docs/seed/credentials.md` (ignorado por git).

| Rol | Email | Contraseña |
|-----|-------|------------|
| DEV | dev@gmail.com | 12345678 |
| ADMIN | admin@gmail.com | 12345678 |
| Teacher / Student | ver MD | DNI |

## Seguridad

- Deshabilitado por defecto (`app.seed.enabled=false`).
- Solo activo en perfil desarrollo.
- Endpoint protegido con `@SessionRequired(ROLE_DEV)`.
- No disponible en producción.

## Configuración

Propiedades bajo `app.seed.*` en `DatabaseSeedProperties`. Ver `docs/seed/architecture.md`.

---

## Simulación de actividad académica

Segunda fase post-bootstrap. Simula actividades, intentos de estudiantes, beneficios y compra de skins en un rango de fechas.

### Prerequisito

Ejecutar `POST /api/dev/seed` (o bootstrap) antes de simular.

### Endpoint

```
POST /api/dev/seed/simulate
Authorization: Bearer <token DEV>
Content-Type: application/json

{
  "fromDate": "2025-01-01T08:00:00",
  "toDate": "2025-03-31T18:00:00"
}
```

### Qué simula

| Fase | Descripción |
|------|-------------|
| 1 | Docentes crean actividades temáticas por materia |
| 2 | Estudiantes las realizan (distribución aleatoria de intentos) |
| 3 | Docentes crean beneficios |
| 4 | Compra parcial → solicitud de uso → aceptación |
| 5 | Compra parcial de skins REMERA/SOMBRERO |

### Respuesta (`SimulationResultDto`)

- `message`: resumen
- `fromDate` / `toDate`: rango aplicado
- `counts`: actividades, intentos, aprobados, beneficios, compras, usos, skins

### Configuración adicional

Propiedades bajo `app.seed.simulation.*` (`SimulationProperties`): tasas de participación, distribución de intentos, límites de volumen. Ver `docs/seed/simulation-architecture.md`.
