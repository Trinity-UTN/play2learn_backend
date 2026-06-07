# Arquitectura: Database Simulation Seed

## Objetivo

Segunda fase del seed de desarrollo. Simula actividad académica y movimientos económicos en un rango `[fromDate, toDate]` sobre datos creados por `DatabaseSeedService`.

## Prerequisito

Ejecutar `POST /api/dev/seed` o `POST /api/dev/seed/bootstrap` antes de simular.

## Paquete

`trinity.play2learn.backend.configs.seed.simulation`

## Componentes

| Componente | Responsabilidad |
|------------|-----------------|
| `SimulationProperties` | Tasas, límites y seed aleatorio (`app.seed.simulation.*`) |
| `SimulationTimeline` | Distribución de fechas en el rango |
| `SimulationDateValidator` | Coherencia temporal |
| `SubjectActivityTemplateFactory` | Contenido temático por materia/tipo |
| `ActivitySimulationGenerateService` | Creación de actividades |
| `ActivitySimulationAttemptService` | Intentos de estudiantes |
| `SimulationActivityCompletionService` | Aprobación/desaprobación con timestamps explícitos |
| `BenefitSimulationGenerateService` | Creación de beneficios |
| `BenefitSimulationLifecycleService` | Compra, solicitud y aceptación |
| `AspectSimulationPurchaseService` | Compra de skins de pago |
| `DatabaseSimulationService` | Orquestación de fases |
| `SimulationSeedController` | `POST /api/dev/seed/simulate` |

## Orden de fases

```
1. Validar rango [from, to] y prerequisitos bootstrap
2. Generar actividades por materia (docente asignado)
3. Simular intentos de estudiantes (subconjunto aleatorio)
4. Generar beneficios por materia
5. Ciclo compra → solicitud de uso → aceptación (parcial)
6. Compra parcial de aspectos REMERA/SOMBRERO
7. Retornar SimulationResultDto con conteos
```

## Reglas de coherencia temporal

| Regla | Descripción |
|-------|-------------|
| R0 | `fromDate <= toDate` |
| R1 | `activity.createdAt >= fromDate` |
| R2 | `activity.startDate >= activity.createdAt` |
| R3 | `activity.endDate > activity.startDate` |
| R4 | `attempt.startedAt >= activity.startDate` |
| R5 | `attempt.completedAt >= attempt.startedAt` |
| R6 | `benefit.endAt > benefitCreatedAt` |
| R7 | `purchase.purchasedAt >= benefitCreatedAt` |
| R8 | `purchase.usedAt >= purchase.purchasedAt` |

## Mapeo materia → actividad

| Materia | Tipos | Ejemplos de contenido |
|---------|-------|----------------------|
| Matemática | Preguntados, Clasificación, Ahorcado | Fracciones, ecuaciones, perímetro |
| Lengua | Completar oración, Preguntados, Ahorcado | Gramática, literatura, ortografía |
| Geografía | Ordenar secuencia, Clasificación, Preguntados | Capitales, ríos, eventos históricos |

## Estrategia de timestamps históricos

Los servicios oficiales usan `LocalDateTime.now()` en `@PrePersist` y validaciones `@Future`. La simulación:

1. Persiste entidades vía repositorios/mappers (sin pasar por controllers).
2. Tras el primer `save()`, actualiza campos temporales (`createdAt`, `startedAt`, `completedAt`, `purchasedAt`, `usedAt`).
3. Ejecuta segundo `save()` — `@PrePersist` no se vuelve a ejecutar en updates.

Para transacciones y recompensas se reutiliza `ITransactionGenerateService`; el `createdAt` de la transacción queda en `now()` (aceptable en dev). Las entidades de negocio mantienen fechas simuladas.

## Servicios reutilizados vs. internos

| Reutilizar | Lógica interna dev-only |
|------------|-------------------------|
| `ITransactionGenerateService` | `ActivitySimulationGenerateService` |
| `IActivityCalculateRewardStrategyService` (map) | `SimulationActivityCompletionService` |
| `IProfileUpdateLevelService` | `ActivitySimulationAttemptService` |
| Repositorios JPA | `BenefitSimulationLifecycleService` |
| Mappers existentes (`*Mapper.toModel`) | `AspectSimulationPurchaseService` |

No se invocan servicios con `@Future`, `validatePublishedStatus` ni notificaciones en la simulación.

## Seguridad

- `@ConditionalOnProperty(app.seed.enabled=true)`
- `@SessionRequired(ROLE_DEV)` en el endpoint
- Límites de volumen en `SimulationProperties` (max actividades/beneficios por ejecución)

## Endpoint

```
POST /api/dev/seed/simulate
Content-Type: application/json

{
  "fromDate": "2025-01-01T08:00:00",
  "toDate": "2025-03-31T18:00:00"
}
```
