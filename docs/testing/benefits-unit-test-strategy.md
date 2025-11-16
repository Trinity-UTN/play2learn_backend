# Estrategia de pruebas unitarias — Módulo Benefits

## 1. Objetivo
Definir un plan de pruebas unitarias coherente para services, controllers y DTOs del módulo `benefits`, garantizando cobertura de flujos críticos (generación, compra, solicitud/aceptación de uso, listados paginados, conteo de estados, gestión de reembolsos) con suites aisladas, repetibles y alineadas a los estándares del repositorio.

## 2. Alcance y exclusiones
- **Incluye**:
  - Implementaciones de servicios principales (`BenefitGenerateService`, `BenefitPurchaseService`, `BenefitDeleteService`, etc.) y servicios commons bajo `benefits/services/commons`.
  - Controladores HTTP publicados bajo `/benefits` y `/benefits/teacher/*`, `/benefits/student/*`.
  - DTOs de entrada/salida definidos en `benefits/dtos` con sus reglas de Bean Validation.
  - Estrategias de filtrado por estado de estudiante (`BenefitAvailableFilterService`, `BenefitPurchasedFilterService`, `BenefitUseRequestedFilterService`, `BenefitExpiredFilterService`).
- **Excluye**:
  - Modelos JPA (`Benefit`, `BenefitPurchase`), repositorios (`IBenefitRepository`, `IBenefitPurchaseRepository`, etc.), mappers (`BenefitMapper`, `BenefitPurchaseMapper`) y specs (`BenefitSpecs`, `BenefitPurchasesSpecs`); se validarán mediante pruebas de integración o de otro nivel.

## 3. Métricas y criterios de calidad
- Cobertura objetivo por categoría:
  - Services principales y commons: ≥ 85 % líneas / ≥ 75 % ramas.
  - Controllers (`@WebMvcTest`): ≥ 85 % líneas / ≥ 70 % ramas.
  - DTOs Bean Validation: ≥ 95 % de restricciones ejercitadas (casos válidos e inválidos).
  - Estrategias de filtrado: ≥ 90 % líneas / ≥ 80 % ramas.
- Todas las suites deben correr limpias con `./mvnw.cmd test` sin warnings de Mockito ni logs inesperados.
- Duración total del módulo ≤ 120 s en entorno local al ejecutar `./mvnw.cmd -pl benefits -am test`.
- Casos documentados con Given/When/Then mediante `@DisplayName` y asserts descriptivos (AssertJ + JSONPath).
- Suites deben validar estrategias de filtrado dinámicas y transacciones de economía (`COMPRA`, `REEMBOLSO`).

## 4. Priorización de riesgos

| Riesgo | Impacto | Probabilidad | Mitigación en pruebas |
|--------|---------|--------------|-----------------------|
| `BenefitGenerateService` falla al validar pertenencia docente-materia | Alto | Alto | Escenarios en `BenefitGenerateServiceTest` verificando `subject.hasTeacherByEmail` y lanzamiento de `UnauthorizedException` cuando docente no está asignado. |
| `BenefitDeleteService` no reembolsa correctamente compras activas al eliminar beneficio | Alto | Medio | Tests en `BenefitDeleteServiceTest` verificando reembolsos a compras activas (estado distinto de `USED`) mediante transacciones `REEMBOLSO`, y omisión de reembolsos si beneficio está expirado. |
| `BenefitPurchaseService` no valida correctamente límites de compra (total o por estudiante) | Alto | Alto | Escenarios en `BenefitPurchaseServiceTest` validando límite total de compras (`purchasesLeft > 0`), límite por estudiante, y lanzamiento de `ConflictException` cuando se alcanzan límites. |
| `BenefitPurchaseService` permite compras en beneficios expirados | Alto | Alto | Casos en `BenefitPurchaseServiceTest` verificando validación de expiración mediante `benefit.isExpired()` y lanzamiento de `ConflictException` si está expirado. |
| `BenefitPurchaseService` no valida que no haya compra activa sin usar | Alto | Medio | Tests en `BenefitPurchaseServiceTest` validando que última compra esté en estado `USED` o no exista antes de permitir nueva compra; lanza `ConflictException` si hay compra activa en `PURCHASED`. |
| `BenefitRequestUseService` permite solicitar uso de beneficio sin compra previa | Alto | Medio | Escenarios en `BenefitRequestUseServiceTest` verificando existencia de compra en estado `PURCHASED` y lanzamiento de `ConflictException` si no existe o no está en estado correcto. |
| `BenefitAcceptUseService` acepta uso de beneficio sin validar pertenencia al docente | Alto | Medio | Casos en `BenefitAcceptUseServiceTest` validando que beneficio pertenezca al docente mediante `benefit.getSubject().getTeacher().equals(teacher)` y lanzamiento de `ConflictException` si no pertenece. |
| `BenefitAcceptUseService` acepta uso de beneficio en estado incorrecto | Alto | Medio | Tests en `BenefitAcceptUseServiceTest` verificando que compra esté en estado `USE_REQUESTED` y lanzamiento de `ConflictException` si está en otro estado. |
| Servicios de listado paginado no manejan correctamente listas vacías | Medio | Bajo | Tests en `BenefitListByTeacherPaginatedServiceTest`, `BenefitListByStudentPaginatedServiceTest`, etc. validando retorno de `Page.empty` cuando no hay beneficios/compras. |
| Filtro especial por estado (`state`) en `BenefitListByStudentPaginatedService` interfiere con otros filtros | Medio | Medio | Escenarios en `BenefitListByStudentPaginatedServiceTest` validando combinación de filtro por estado usando estrategias con filtros dinámicos (`BenefitSpecs`). |
| `BenefitStudentCountService` clasifica incorrectamente beneficios por estado | Medio | Medio | Casos en `BenefitStudentCountServiceTest` validando clasificación correcta por estado (`AVAILABLE`, `PURCHASED`, `USE_REQUESTED`, `EXPIRED`, `USED`) y conteo sin duplicados. |
| `BenefitFilterByStudentStateService` no delega correctamente a estrategias | Medio | Bajo | Tests en `BenefitFilterByStudentStateServiceTest` verificando delegación a estrategias correctas según estado (`AVAILABLE`, `PURCHASED`, `USE_REQUESTED`, `EXPIRED`). |
| Estrategias de filtrado no filtran correctamente según estado | Medio | Medio | Casos en `BenefitAvailableFilterServiceTest`, `BenefitPurchasedFilterServiceTest`, etc. validando lógica de filtrado específica de cada estrategia. |
| `BenefitListPurchasesPaginatedService` no filtra correctamente por IDs de compras del beneficio | Medio | Bajo | Escenarios en `BenefitListPurchasesPaginatedServiceTest` validando construcción de `Specification` con filtro por IDs de compras y retorno de página vacía si no hay compras. |
| `BenefitListUseRequestedPaginatedService` no filtra correctamente por docente y estado | Medio | Bajo | Tests en `BenefitListUseRequestedPaginatedServiceTest` verificando filtros por docente, estado `USE_REQUESTED`, no expirados, no eliminados en `Specification`. |
| Validación de inscripción estudiante-materia falla en compra o solicitud de uso | Alto | Medio | Casos en `BenefitPurchaseServiceTest` y `BenefitRequestUseServiceTest` validando invocación correcta a `subjectHasStudentService.subjectHasStudent` y propagación de excepciones. |
| Decremento de `purchasesLeft` falla tras compra | Medio | Bajo | Tests en `BenefitPurchaseServiceTest` verificando invocación correcta a `benefit.decrementPurchasesLeft()` tras compra exitosa. |
| Cálculo de `purchasesLeftByStudent` es incorrecto | Medio | Medio | Escenarios en `BenefitGetPurchasesLeftByStudentServiceTest` validando cálculo correcto de compras restantes por estudiante (`purchaseLimitPerStudent - compras realizadas`). |

## 5. Suites planificadas

### Services Principales

1. **Servicios de generación y eliminación**
   - `BenefitGenerateServiceTest`, `BenefitDeleteServiceTest`.
   - Cobertura: validación de pertenencia docente-materia, generación de beneficios, reembolsos en eliminación, soft delete.

2. **Servicio de compra**
   - `BenefitPurchaseServiceTest`.
   - Cobertura: validación de expiración, inscripción estudiante-materia, límites de compra (total y por estudiante), validación de compras activas, generación de transacciones `COMPRA`, decremento de `purchasesLeft`.

3. **Servicios de listado por docente**
   - `BenefitListByTeacherServiceTest`, `BenefitListByTeacherPaginatedServiceTest`.
   - Cobertura: filtrado por docente, exclusión de eliminados, búsqueda y filtros dinámicos, paginación, ordenamiento por estado (PUBLISHED primero).

4. **Servicios de listado por estudiante**
   - `BenefitListByStudentPaginatedServiceTest`, `BenefitStudentCountServiceTest`.
   - Cobertura: filtrado por materias del estudiante, filtro especial por estado usando estrategias, clasificación por estado, conteo de usados, creación de DTOs específicos para estudiante.

5. **Servicios de solicitud y aceptación de uso**
   - `BenefitRequestUseServiceTest`, `BenefitAcceptUseServiceTest`.
   - Cobertura: validación de expiración, inscripción estudiante-materia, existencia de compra en `PURCHASED`, validación de pertenencia al docente, validación de estado `USE_REQUESTED`, cambio de estado a `USE_REQUESTED`/`USED`, establecimiento de `usedAt`.

6. **Servicios de listado de compras**
   - `BenefitListPurchasesServiceTest`, `BenefitListPurchasesPaginatedServiceTest`.
   - Cobertura: validación de pertenencia del beneficio al docente, filtrado por IDs de compras, búsqueda por nombre completo del estudiante, paginación, retorno de página vacía si no hay compras.

7. **Servicios de listado de solicitudes de uso**
   - `BenefitListUseRequestedServiceTest`, `BenefitListUseRequestedPaginatedServiceTest`.
   - Cobertura: filtrado por docente, estado `USE_REQUESTED`, exclusión de eliminados y expirados, búsqueda por nombre completo del estudiante, paginación.

8. **Servicios de listado de usados por estudiante**
   - `BenefitListUsedByStudentServiceTest`, `BenefitListUsedByStudentPaginatedServiceTest`.
   - Cobertura: filtrado por estudiante, estado `USED`, exclusión de eliminados, búsqueda por nombre del beneficio, paginación.

### Services Commons

1. **Servicios de obtención**
   - `BenefitGetByIdServiceTest`, `BenefitGetByStudentServiceTest`.
   - Cobertura: búsqueda por ID excluyendo eliminados, obtención por estudiante mediante materias, propagación de `NotFoundException`.

2. **Servicios de validación**
   - `BenefitValidatePurchaseLimitServiceTest`, `BenefitValidateIfPurchasedByStudentServiceTest`.
   - Cobertura: validación de límite total de compras, validación de compras activas sin usar, lanzamiento de `ConflictException` cuando corresponde.

3. **Servicios de estado y cálculo**
   - `BenefitGetStudentStateServiceTest`, `BenefitGetPurchasesLeftByStudentServiceTest`, `BenefitGetLastPurchaseServiceTest`.
   - Cobertura: determinación de estado del beneficio para estudiante (prioridad: expirado → uso solicitado → comprado → disponible), cálculo de compras restantes por estudiante, obtención de última compra.

4. **Servicios de verificación**
   - `BenefitIsPurchasedServiceTest`, `BenefitIsUseRequestedServiceTest`, `BenefitPurchaseGetByIdServiceTest`.
   - Cobertura: verificación de compra activa, verificación de solicitud de uso, búsqueda de compra por ID.

5. **Servicios de filtrado y creación de DTOs**
   - `BenefitFilterByStudentStateServiceTest`, `BenefitCreateStudentDtosServiceTest`.
   - Cobertura: delegación a estrategias según estado, creación de DTOs específicos para estudiante con estado y compras restantes.

6. **Estrategias de filtrado**
   - `BenefitAvailableFilterServiceTest`, `BenefitPurchasedFilterServiceTest`, `BenefitUseRequestedFilterServiceTest`, `BenefitExpiredFilterServiceTest`.
   - Cobertura: lógica de filtrado específica de cada estrategia según estado del estudiante y beneficio.

### Controllers

1. **Controllers de generación y eliminación**
   - `BenefitGenerateControllerTest`, `BenefitDeleteControllerTest`.
   - Validación de roles (`ROLE_TEACHER`), payloads JSON, códigos 2xx/204.

2. **Controllers de listado por docente**
   - `BenefitListControllerTest`, `BenefitListPaginatedControllerTest`.
   - Validación de roles (`ROLE_TEACHER`), query params, metadatos de paginación, respuestas `List<BenefitResponseDto>` y `PaginatedData<BenefitResponseDto>`.

3. **Controllers de compra y solicitud de uso**
   - `BenefitPurchaseControllerTest`, `BenefitRequestUseControllerTest`.
   - Validación de roles (`ROLE_STUDENT`), payloads JSON, path params, códigos 2xx/4xx.

4. **Controllers de aceptación y conteo**
   - `BenefitAcceptUseControllerTest`, `BenefitStudentCountControllerTest`.
   - Validación de roles (`ROLE_TEACHER`/`ROLE_STUDENT`), path params, respuestas `BenefitPurchaseSimpleResponseDto` y `BenefitStudentCountResponseDto`.

5. **Controllers de listado de compras**
   - `BenefitListPurchasesControllerTest`, `BenefitListPurchasesPaginatedControllerTest`.
   - Validación de roles (`ROLE_TEACHER`), path params (beneficio ID), query params, respuestas `List<BenefitPurchaseSimpleResponseDto>` y `PaginatedData<BenefitPurchaseSimpleResponseDto>`.

6. **Controllers de listado de solicitudes de uso**
   - `BenefitListUseRequestedByTeacherControllerTest`, `BenefitListUseRequestedPaginatedControllerTest`.
   - Validación de roles (`ROLE_TEACHER`), query params, respuestas `List<BenefitPurchaseSimpleResponseDto>` y `PaginatedData<BenefitPurchaseSimpleResponseDto>`.

7. **Controllers de listado por estudiante**
   - `BenefitListByStudentPaginatedControllerTest`, `BenefitListUsedByStudentControllerTest`, `BenefitListUsedPaginatedControllerTest`.
   - Validación de roles (`ROLE_STUDENT`), query params, respuestas `PaginatedData<BenefitStudentResponseDto>`, `List<BenefitPurchasedUsedResponseDto>` y `PaginatedData<BenefitPurchasedUsedResponseDto>`.

### DTOs Validation

1. **DTOs principales**
   - `BenefitDtoValidationTest` (para `BenefitRequestDto`).
   - `BenefitPurchaseDtoValidationTest` (para `BenefitPurchaseRequestDto`).
   - Verifica restricciones de campos requeridos (`@NotNull`, `@NotBlank`), tamaños máximos (`@Size`), validaciones de fecha (`@Future`), valores mínimos (`@Min`), mensajes de validación (`ValidationMessages`).

## 6. Dobles de prueba y herramientas
- Mockito + JUnit 5 (`@ExtendWith(MockitoExtension.class)`) con `ArgumentCaptor` para validar interacciones críticas (`IBenefitRepository`, `IBenefitPurchaseRepository`, servicios externos).
- `@WebMvcTest` con `@MockBean` para inyectar servicios simulados en controllers.
- Uso de `PageImpl` para paginación y helpers custom para construir `PaginatedData`.
- `MockedStatic` para servicios estáticos (`PaginatorUtils`, `PaginationHelper`) en tests de paginación.
- `BenefitTestMother` (a crear si no existe) para generar beneficios, compras, estudiantes, docentes y usuarios consistentes.
- Fixtures específicas por contexto (`BenefitPurchaseTestMother`, etc.) para generar datos específicos de compras y estados.
- `JsonContentHelper` o métodos estáticos para requests/responses en suites WebMvc.
- Mocks de servicios externos (`ITransactionGenerateService`, `ISubjectGetByIdService`, `ITeacherGetByEmailService`, `IStudentGetByEmailService`, `ISubjectHasStudentService`) con verificación de parámetros.

## 7. Fixtures y utilidades compartidas

### Constantes y Builders Principales
- Constantes de beneficios: IDs, nombres, descripciones, costos, límites de compra (total y por estudiante), fechas de expiración (`endAt`), categorías (`BenefitCategory`), colores (`BenefitColor`), iconos (`BenefitIcon`).
- Constantes de compras: IDs, estados (`PURCHASED`, `USE_REQUESTED`, `USED`), fechas de compra (`purchasedAt`), fechas de uso (`usedAt`).
- Builders para `Benefit` (genérico), `BenefitPurchase`, `BenefitRequestDto`, `BenefitResponseDto`, `BenefitStudentResponseDto`, `BenefitStudentCountResponseDto`, `BenefitPurchaseRequestDto`, `BenefitPurchaseResponseDto`, `BenefitPurchaseSimpleResponseDto`, `BenefitPurchasedUsedResponseDto`.
- Métodos auxiliares para crear `PaginatedData<BenefitResponseDto>`, `PaginatedData<BenefitStudentResponseDto>`, `PaginatedData<BenefitPurchaseSimpleResponseDto>`, `PaginatedData<BenefitPurchasedUsedResponseDto>` y páginas (`PageImpl`) con metadatos coherentes.
- Fixtures de estudiantes, docentes, materias y usuarios asociados con roles apropiados.
- Helpers para generar `Specification<Benefit>` y `Specification<BenefitPurchase>` con filtros comunes (`notDeleted`, `filterByTeacher`, `nameContains`, `studentFullNameContains`, `benefitNameContains`).
- Tabla de mensajes (`ValidationMessages`, `ConflictExceptionMessages`, `UnauthorizedExceptionMessages`, `SuccessfulMessages`) referenciada mediante constantes.
- Builders para beneficios expirados y no expirados, con diferentes estados de compra.
- Builders para compras en diferentes estados (`PURCHASED`, `USE_REQUESTED`, `USED`) asociadas a estudiantes y beneficios.
- Helpers para simular transacciones (`COMPRA`, `REEMBOLSO`) mediante `ITransactionGenerateService`.

## 8. Secuencia de trabajo recomendada

1. **Fase 1: Services Commons**
   - Implementar suites de servicios commons críticos (`BenefitGetByIdService`, `BenefitGetByStudentService`, `BenefitValidatePurchaseLimitService`, `BenefitValidateIfPurchasedByStudentService`, `BenefitGetStudentStateService`, `BenefitGetPurchasesLeftByStudentService`, `BenefitGetLastPurchaseService`, `BenefitIsPurchasedService`, `BenefitIsUseRequestedService`) con refinamiento inmediato.
   - Abordar estrategias de filtrado (`BenefitAvailableFilterService`, `BenefitPurchasedFilterService`, `BenefitUseRequestedFilterService`, `BenefitExpiredFilterService`) y servicios de filtrado (`BenefitFilterByStudentStateService`, `BenefitCreateStudentDtosService`).
   - Centralizar fixtures reutilizables en `BenefitTestMother`.

2. **Fase 2: Services Principales - Generación y Compra**
   - Cubrir servicios de generación y eliminación (`BenefitGenerateService`, `BenefitDeleteService`) validando pertenencia docente-materia y reembolsos.
   - Implementar servicio de compra (`BenefitPurchaseService`) con validaciones críticas de límites y expiración.

3. **Fase 3: Services Principales - Listados**
   - Implementar servicios de listado por docente (`BenefitListByTeacherService`, `BenefitListByTeacherPaginatedService`) con `MockedStatic` para utilidades.
   - Abordar servicios de listado por estudiante (`BenefitListByStudentPaginatedService`, `BenefitStudentCountService`) validando filtrado por estado.

4. **Fase 4: Services Principales - Solicitud y Aceptación de Uso**
   - Implementar servicios de solicitud y aceptación de uso (`BenefitRequestUseService`, `BenefitAcceptUseService`) validando estados y pertenencia.

5. **Fase 5: Services Principales - Listados de Compras y Solicitudes**
   - Cubrir servicios de listado de compras (`BenefitListPurchasesService`, `BenefitListPurchasesPaginatedService`).
   - Abordar servicios de listado de solicitudes de uso (`BenefitListUseRequestedService`, `BenefitListUseRequestedPaginatedService`).
   - Implementar servicios de listado de usados por estudiante (`BenefitListUsedByStudentService`, `BenefitListUsedByStudentPaginatedService`).

6. **Fase 6: Controllers**
   - Implementar controllers de generación y eliminación (`BenefitGenerateController`, `BenefitDeleteController`) con configuración MockMvc compartida.
   - Abordar controllers de listado por docente (`BenefitListController`, `BenefitListPaginatedController`).
   - Implementar controllers de compra y solicitud de uso (`BenefitPurchaseController`, `BenefitRequestUseController`, `BenefitAcceptUseController`).
   - Abordar controllers de listado de compras, solicitudes de uso y usados (`BenefitListPurchasesController`, `BenefitListPurchasesPaginatedController`, `BenefitListUseRequestedByTeacherController`, `BenefitListUseRequestedPaginatedController`, `BenefitListByStudentPaginatedController`, `BenefitListUsedByStudentController`, `BenefitListUsedPaginatedController`, `BenefitStudentCountController`).

7. **Fase 7: DTO Validation**
   - Finalizar con pruebas de Bean Validation para DTOs principales (`BenefitDtoValidationTest`, `BenefitPurchaseDtoValidationTest`).

8. **Fase 8: Refinamiento y optimización**
   - Aplicar `/refine` tras cada bloque para reducir duplicidad y mejorar expresividad.
   - Centralizar fixtures compartidas donde sea posible.
   - Ajustes detectados por JaCoCo.

## 9. Integración con quality gates
- Ejecutar `./mvnw.cmd org.jacoco:jacoco-maven-plugin:prepare-agent test org.jacoco:jacoco-maven-plugin:report` cuando las suites principales estén listas, garantizando los umbrales propuestos.
- Consolidar métricas y hallazgos con `python scripts/testing_report.py --module "benefits" --output "docs/testing/test_benefits.md"` al completar el ciclo de pruebas.
- Registrar cualquier deuda o hallazgo residual en el reporte para seguimiento del equipo.

## 10. Riesgos residuales y seguimiento

- Confirmar con el equipo si las validaciones de límites de compra (total y por estudiante) son suficientes; ajustar pruebas en consecuencia.
- Mantener alineados los contratos con servicios externos (`IStudentGetByEmailService`, `ITeacherGetByEmailService`, `ISubjectGetByIdService`, `ISubjectHasStudentService`, `ITransactionGenerateService`); actualizar mocks si cambian firmas o mensajes.
- Vigilar la integración con módulo `economy/transaction` para generación de transacciones (`COMPRA`, `REEMBOLSO`); incluir mocks robustos y verificación de parámetros.
- Supervisar el comportamiento de estrategias de filtrado (`IBenefitFilterStrategyService`) y ajustar pruebas si se añaden nuevos estados o estrategias.
- Validar que las validaciones de expiración (`benefit.isExpired()`) y estados de compra (`PURCHASED`, `USE_REQUESTED`, `USED`) sean consistentes en todos los servicios; ajustar pruebas si cambia la lógica de estados.
- Confirmar con el equipo funcional si las validaciones de pertenencia docente-materia y inscripción estudiante-materia son suficientes; ajustar pruebas en consecuencia.
- Si se añaden nuevos estados de beneficio o compra, replicar el patrón de pruebas establecido para estados existentes.
- Validar que los reembolsos en eliminación de beneficios sean consistentes; ajustar pruebas si cambia la lógica de reembolsos.

