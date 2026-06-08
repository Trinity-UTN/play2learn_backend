# Arquitectura: Database Seed Service

## Objetivo

Repoblar la base de datos tras `ddl-auto=create` con datos mínimos operativos, respetando límites de volumen y generando credenciales en markdown.

## Paquete

`trinity.play2learn.backend.configs.seed`

## Componentes

| Componente | Responsabilidad |
|------------|-----------------|
| `DatabaseSeedProperties` | Límites y credenciales configurables (`app.seed.*`) |
| `IDatabaseSeedService` | Contrato: `SeedResultDto execute()` |
| `DatabaseSeedService` | Orquestación de fases 1–8 |
| `AspectSeedService` | Catálogo de aspectos desde `docs/aspects/` |
| `CredentialsMarkdownWriter` | Exporta `docs/seed/credentials.md` |
| `SeedDataCollector` | Acumula credenciales y conteos durante el seed |
| `DatabaseSeedController` | `POST /api/dev/seed` (solo si `app.seed.enabled=true`) |

## DTOs

### `SeedCredentialsDto`

| Campo | Tipo | Descripción |
|-------|------|-------------|
| email | String | Login |
| password | String | Contraseña en texto plano (solo dev) |
| role | String | `ROLE_DEV`, `ROLE_ADMIN`, etc. |
| dni | String | DNI (teachers/students) |
| name | String | Nombre completo |
| yearName | String | Solo students |
| courseName | String | Solo students |

### `SeedResultDto`

- `message`: resumen textual
- `credentials`: lista de `SeedCredentialsDto`
- `counts`: years, courses, teachers, students, subjects, aspects, reserves
- `credentialsFilePath`: ruta del MD generado

## Fases de ejecución

```
1. Reserve (singleton P0)
2. Users DEV + ADMIN
3. Years (6) + Courses (2/year)
4. Teachers (mín. 6, reutilizables)
5. Students (30/year, 15/course) → Profile + Wallet automático
6. Subjects (3/course) + SubjectRefillBalance
7. Fondeo wallets + circulationBalance
8. Aspects (9) + inventario por alumno
9. Export credentials.md
```

## Distribución de estudiantes

- `maxStudentsPerYear = 30`
- `maxCoursesPerYear = 2`
- Por curso: `15` estudiantes (`30 / 2`)

## Estrategia de aspectos

**Decisión:** persistencia directa vía `IAspectRepository` (sin ImgBB en seed).

- Imagen: URL sintética `seed://{type}/{filename}` (referencia local para dev).
- Idempotencia: `existsByName` antes de insertar.
- Inventario: `ProfileAddAspectToInventoryService` (genera transacción COMPRA).
- `walletSeedAmount = 3000` para cubrir compras (2×500 + 3×300 = 1900 máx.).

## Seguridad

- `app.seed.enabled=false` por defecto.
- `true` solo en `application-desarrollo.properties`.
- Controller con `@ConditionalOnProperty` + `@SessionRequired(ROLE_DEV)`.
- `docs/seed/credentials.md` en `.gitignore`.

## Idempotencia

| Entidad | Estrategia |
|---------|------------|
| Reserve | Skip si `findFirstByOrderByCreatedAtDesc` presente |
| DEV/ADMIN | Skip si `userExistService.validate(email)` |
| Year/Course/Teacher/Student | Pensado para BD vacía; re-ejecución parcial puede lanzar `ConflictException` |
| Aspect | Skip si `existsByName` |

## Relación con TestController

`TestController` (`POST /api/test/created`) queda obsoleto frente al nuevo servicio. Se recomienda usar `POST /api/dev/seed` con límites documentados.
