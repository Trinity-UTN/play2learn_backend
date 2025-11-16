# Auditoría de pruebas — Módulo Benefits

## Resumen Ejecutivo
- **Alcance:** controllers, services (incluye commons) y DTOs del módulo `benefits`, excluyendo models, repositories, mappers y specs según pauta.
- **Responsabilidades principales:** generación de beneficios, compra de beneficios por estudiantes, solicitud/aceptación de uso de beneficios, gestión de estados de beneficios, listados paginados por docente/estudiante, conteo de estados y exposición de DTOs específicos por contexto.
- **Validaciones clave:** Bean Validation sobre campos requeridos, validación de pertenencia docente a materia, validación de inscripción de estudiante a materia, validación de límites de compra, validación de expiración de beneficios, validación de estados de compra y resguardo de endpoints mediante `@SessionRequired` con roles específicos.

## Inventario de Controllers

| Controller | Endpoint(s) | Roles requeridos | Service(s) consumidos | Validaciones / Notas |
|------------|-------------|------------------|------------------------|----------------------|
| `BenefitGenerateController` | `POST /benefits` | `ROLE_TEACHER` | `IBenefitGenerateService` | Body `BenefitRequestDto` validado; verifica que docente esté asignado a la materia; genera beneficio y retorna `BenefitResponseDto`. |
| `BenefitDeleteController` | `DELETE /benefits/teacher/{id}` | `ROLE_TEACHER` | `IBenefitDeleteService` | Path `id`; verifica que beneficio pertenezca al docente; realiza reembolsos a compras activas si no está expirado; retorna 204 No Content. |
| `BenefitListController` | `GET /benefits/teacher` | `ROLE_TEACHER` | `IBenefitListByTeacherService` | `@SessionUser User`; devuelve lista completa de beneficios del docente; excluye eliminados (`deletedAtIsNull`). |
| `BenefitListPaginatedController` | `GET /benefits/teacher/paginated` | `ROLE_TEACHER` | `IBenefitListByTeacherPaginatedService` | Query params: `page`, `page_size`, `order_by`, `order_type`, `search`, `filters`, `filtersValues`; respuesta `PaginatedData<BenefitResponseDto>`; filtra solo beneficios del docente; ordena por estado (PUBLISHED primero). |
| `BenefitListByStudentPaginatedController` | `GET /benefits/student/paginated` | `ROLE_STUDENT` | `IBenefitListByStudentPaginatedService` | Query params: `page`, `page_size`, `order_by`, `order_type`, `search`, `filters`, `filtersValues`; respuesta `PaginatedData<BenefitStudentResponseDto>`; filtra por beneficios de materias del estudiante; soporta filtro por estado (`state`). |
| `BenefitPurchaseController` | `POST /benefits/student/purchase` | `ROLE_STUDENT` | `IBenefitPurchaseService` | Body `BenefitPurchaseRequestDto` validado; valida expiración, inscripción en materia, límites de compra; genera transacción `COMPRA`; retorna `BenefitPurchaseResponseDto`. |
| `BenefitStudentCountController` | `GET /benefits/student/count` | `ROLE_STUDENT` | `IBenefitStudentCountService` | `@SessionUser User`; devuelve conteo de beneficios por estado (disponibles, comprados, uso solicitado, usados, expirados). |
| `BenefitRequestUseController` | `PATCH /benefits/student/request-use/{id}` | `ROLE_STUDENT` | `IBenefitRequestUseService` | Path `id` (beneficio); valida expiración, inscripción en materia, que haya compra activa; cambia estado a `USE_REQUESTED`; retorna `BenefitPurchaseSimpleResponseDto`. |
| `BenefitAcceptUseController` | `PATCH /benefits/teacher/accept-use/{id}` | `ROLE_TEACHER` | `IBenefitAcceptUseService` | Path `id` (beneficioPurchase); valida que beneficio no esté eliminado, que pertenezca al docente, que no esté expirado, que esté en estado `USE_REQUESTED`; cambia estado a `USED`; retorna `BenefitPurchaseSimpleResponseDto`. |
| `BenefitListPurchasesController` | `GET /benefits/teacher/purchases/{id}` | `ROLE_TEACHER` | `IBenefitListPurchasesService` | Path `id` (beneficio); verifica que beneficio pertenezca al docente; devuelve lista completa de compras del beneficio; retorna `List<BenefitPurchaseSimpleResponseDto>`. |
| `BenefitListPurchasesPaginatedController` | `GET /benefits/teacher/purchases/{id}/paginated` | `ROLE_TEACHER` | `IBenefitListPurchasesPaginatedService` | Path `id` (beneficio); Query params: `page`, `page_size`, `order_by`, `order_type`, `search`, `filters`, `filtersValues`; verifica pertenencia; respuesta `PaginatedData<BenefitPurchaseSimpleResponseDto>`; filtra por nombre completo del estudiante. |
| `BenefitListUseRequestedByTeacherController` | `GET /benefits/teacher/use-requested` | `ROLE_TEACHER` | `IBenefitListUseRequestedService` | `@SessionUser User`; devuelve lista completa de solicitudes de uso de beneficios del docente; excluye eliminados y expirados; retorna `List<BenefitPurchaseSimpleResponseDto>`. |
| `BenefitListUseRequestedPaginatedController` | `GET /benefits/teacher/use-requested/paginated` | `ROLE_TEACHER` | `IBenefitListUseRequestedPaginatedService` | Query params: `page`, `page_size`, `order_by`, `order_type`, `search`, `filters`, `filtersValues`; respuesta `PaginatedData<BenefitPurchaseSimpleResponseDto>`; filtra por docente, estado `USE_REQUESTED`, no expirados; búsqueda por nombre completo del estudiante. |
| `BenefitListUsedByStudentController` | `GET /benefits/student/used` | `ROLE_STUDENT` | `IBenefitListUsedByStudentService` | `@SessionUser User`; devuelve lista completa de beneficios usados del estudiante; retorna `List<BenefitPurchasedUsedResponseDto>`. |
| `BenefitListUsedPaginatedController` | `GET /benefits/student/used/paginated` | `ROLE_STUDENT` | `IBenefitListUsedByStudentPaginatedService` | Query params: `page`, `page_size`, `order_by`, `order_type`, `search`, `filters`, `filtersValues`; respuesta `PaginatedData<BenefitPurchasedUsedResponseDto>`; filtra por estudiante, estado `USED`; búsqueda por nombre del beneficio. |

## Inventario de Services Principales

| Service | Método(s) clave | Dependencias principales | Validaciones / Excepciones relevantes |
|---------|-----------------|--------------------------|---------------------------------------|
| `BenefitGenerateService` | `cu51GenerateBenefit` | `IBenefitRepository`, `ISubjectGetByIdService` | Verifica que docente esté asignado a materia mediante `subject.hasTeacherByEmail`; lanza `UnauthorizedException` si no; mapea DTO a modelo; persiste beneficio; retorna `BenefitResponseDto`. |
| `BenefitDeleteService` | `cu94DeleteBenefit` | `IBenefitRepository`, `ITeacherGetByEmailService`, `IBenefitGetByIdService`, `IBenefitPurchaseRepository`, `ITransactionGenerateService` | Verifica que beneficio pertenezca al docente; lanza `ConflictException` si no; si no está expirado, reembolsa compras activas (estado distinto de `USED`) mediante transacciones `REEMBOLSO`; marca soft delete (`deletedAt`). |
| `BenefitPurchaseService` | `cu75PurchaseBenefit` | `IStudentGetByEmailService`, `ISubjectHasStudentService`, `IBenefitGetByIdService`, `IBenefitValidatePurchaseLimitService`, `IBenefitGetPurchasesLeftByStudentService`, `IBenefitValidateIfPurchasedByStudentService`, `ITransactionGenerateService`, `IBenefitPurchaseRepository` | Valida expiración del beneficio; lanza `ConflictException` si está expirado; valida inscripción del estudiante en materia; valida límite total de compras; valida límite por estudiante; valida que no haya compra activa sin usar; genera transacción `COMPRA`; decrementa `purchasesLeft`; persiste compra; retorna `BenefitPurchaseResponseDto` con compras restantes. |
| `BenefitListByTeacherService` | `cu55ListBenefitsByTeacher` | `IBenefitRepository`, `ITeacherGetByEmailService` | Obtiene docente por email; busca beneficios por docente excluyendo eliminados; mapea a `List<BenefitResponseDto>`. |
| `BenefitListByTeacherPaginatedService` | `cu56ListBenefitsPaginated` | `ITeacherGetByEmailService`, `IBenefitPaginatedRepository`, `BenefitSpecs`, `PaginatorUtils`, `PaginationHelper` | Obtiene docente por email; construye `Pageable` y `Specification`; filtra por docente y `notDeleted`; aplica búsqueda y filtros dinámicos; ordena resultados por estado (PUBLISHED primero); retorna `PaginatedData<BenefitResponseDto>`. |
| `BenefitListByStudentPaginatedService` | `cu80ListBenefitsByStudentPaginated` | `IStudentGetByEmailService`, `IBenefitGetByStudentService`, `IBenefitFilterByStudentStateService`, `IBenefitPaginatedRepository`, `IBenefitCreateStudentDtosService`, `BenefitSpecs`, `PaginatorUtils`, `PaginationHelper` | Obtiene beneficios del estudiante por materias; construye `Specification`; aplica filtro especial por estado usando estrategias; aplica búsqueda y filtros dinámicos; crea DTOs específicos para estudiante; retorna `PaginatedData<BenefitStudentResponseDto>`; retorna página vacía si no hay beneficios. |
| `BenefitStudentCountService` | `cu89CountByStudentState` | `IStudentGetByEmailService`, `IBenefitGetByStudentService`, `IBenefitGetLastPurchaseService`, `IBenefitGetPurchasesLeftByStudentService`, `IBenefitPurchaseRepository` | Obtiene beneficios del estudiante; clasifica por estado (disponibles, comprados, uso solicitado, usados, expirados); cuenta usados mediante repositorio; retorna `BenefitStudentCountResponseDto` con conteos. |
| `BenefitRequestUseService` | `cu81RequestBenefitUse` | `IStudentGetByEmailService`, `IBenefitGetByIdService`, `ISubjectHasStudentService`, `IBenefitGetLastPurchaseService`, `IBenefitPurchaseRepository` | Valida expiración del beneficio; lanza `ConflictException` si está expirado; valida inscripción del estudiante en materia; valida que exista compra en estado `PURCHASED`; cambia estado a `USE_REQUESTED`; persiste cambios; retorna `BenefitPurchaseSimpleResponseDto`. |
| `BenefitAcceptUseService` | `cu85AcceptBenefitUse` | `ITeacherGetByEmailService`, `IBenefitPurchaseGetByIdService`, `IBenefitPurchaseRepository` | Valida que beneficio no esté eliminado; lanza `ConflictException` si está eliminado; valida que beneficio pertenezca al docente; valida que no esté expirado; valida que esté en estado `USE_REQUESTED`; cambia estado a `USED`; establece `usedAt`; persiste cambios; retorna `BenefitPurchaseSimpleResponseDto`. |
| `BenefitListPurchasesService` | `cu98ListPurchasesByBenefitId` | `ITeacherGetByEmailService`, `IBenefitGetByIdService`, `IBenefitPurchaseRepository` | Verifica que beneficio pertenezca al docente; lanza `ConflictException` si no; obtiene todas las compras del beneficio; mapea a `List<BenefitPurchaseSimpleResponseDto>`. |
| `BenefitListPurchasesPaginatedService` | `cu101ListPurchasesPaginated` | `ITeacherGetByEmailService`, `IBenefitGetByIdService`, `IBenefitPurchasePaginatedRepository`, `IBenefitPurchaseRepository`, `BenefitPurchasesSpecs`, `PaginatorUtils`, `PaginationHelper` | Verifica pertenencia del beneficio; obtiene compras del beneficio; construye `Specification`; aplica búsqueda por nombre completo del estudiante y filtros dinámicos; filtra por IDs de compras; retorna `PaginatedData<BenefitPurchaseSimpleResponseDto>`; retorna página vacía si no hay compras. |
| `BenefitListUseRequestedService` | `cu82ListUseRequestedByTeacher` | `ITeacherGetByEmailService`, `IBenefitRepository`, `IBenefitPurchaseRepository` | Obtiene beneficios del docente; excluye eliminados y expirados; obtiene compras en estado `USE_REQUESTED`; mapea a `List<BenefitPurchaseSimpleResponseDto>`. |
| `BenefitListUseRequestedPaginatedService` | `cu108ListUseRequestedPaginated` | `ITeacherGetByEmailService`, `IBenefitPurchasePaginatedRepository`, `BenefitPurchasesSpecs`, `PaginatorUtils`, `PaginationHelper` | Obtiene docente por email; construye `Specification`; filtra por docente, estado `USE_REQUESTED`, no expirados, no eliminados; aplica búsqueda por nombre completo del estudiante y filtros dinámicos; retorna `PaginatedData<BenefitPurchaseSimpleResponseDto>`. |
| `BenefitListUsedByStudentService` | `cu93ListUsedByStudent` | `IBenefitPurchaseRepository`, `IStudentGetByEmailService` | Obtiene estudiante por email; busca compras en estado `USED` del estudiante; excluye eliminados; mapea a `List<BenefitPurchasedUsedResponseDto>`. |
| `BenefitListUsedByStudentPaginatedService` | `cu109ListUsedPaginated` | `IBenefitPurchasePaginatedRepository`, `IStudentGetByEmailService`, `BenefitPurchasesSpecs`, `PaginatorUtils`, `PaginationHelper` | Obtiene estudiante por email; construye `Specification`; filtra por estudiante, estado `USED`, no eliminados; aplica búsqueda por nombre del beneficio y filtros dinámicos; retorna `PaginatedData<BenefitPurchasedUsedResponseDto>`. |

## Servicios Commons

| Service | Responsabilidad | Dependencias | Notas de validación |
|---------|-----------------|--------------|---------------------|
| `BenefitGetByIdService` | Buscar beneficio por ID | `IBenefitRepository` | Método `findByIdAndDeletedAtIsNull` lanza `NotFoundException` si no encuentra beneficio. |
| `BenefitGetByStudentService` | Obtener beneficios por estudiante | `ISubjectGetByStudentService`, `IBenefitRepository` | Obtiene materias del estudiante; busca beneficios por materias; no filtra por eliminados explícitamente. |
| `BenefitGetStudentStateService` | Determinar estado del beneficio para estudiante | `IBenefitIsPurchasedService`, `IBenefitIsUseRequestedService` | Evalúa en orden: expirado (prioridad), uso solicitado, comprado, disponible; retorna `BenefitStudentState`. |
| `BenefitGetPurchasesLeftByStudentService` | Calcular compras restantes por estudiante | `IBenefitPurchaseRepository` | Si beneficio no tiene límite por estudiante, retorna `null`; calcula diferencia entre límite y compras realizadas. |
| `BenefitGetLastPurchaseService` | Obtener última compra del estudiante | `IBenefitPurchaseRepository` | Busca última compra por beneficio y estudiante ordenada por fecha; retorna `Optional<BenefitPurchase>`. |
| `BenefitIsPurchasedService` | Verificar si estudiante compró beneficio | `IBenefitGetLastPurchaseService` | Verifica si última compra existe y está en estado `PURCHASED`; retorna `Boolean`. |
| `BenefitIsUseRequestedService` | Verificar si se solicitó uso del beneficio | `IBenefitGetLastPurchaseService` | Verifica si última compra existe y está en estado `USE_REQUESTED`; retorna `Boolean`. |
| `BenefitPurchaseGetByIdService` | Buscar compra por ID | `IBenefitPurchaseRepository` | Método `findByIdAndDeletedAtIsNull` lanza `NotFoundException` si no encuentra compra. |
| `BenefitValidatePurchaseLimitService` | Validar límite total de compras | - | Si beneficio no tiene límite, no valida; verifica que `purchasesLeft > 0`; lanza `ConflictException` si se alcanzó límite. |
| `BenefitValidateIfPurchasedByStudentService` | Validar que no haya compra activa sin usar | `IBenefitGetLastPurchaseService` | Verifica última compra; si existe y está en estado `PURCHASED`, lanza `ConflictException`; permite compras nuevas solo si última está en `USED` o no existe. |
| `BenefitFilterByStudentStateService` | Filtrar beneficios por estado del estudiante | `Map<String, IBenefitFilterStrategyService>` | Delega a estrategias según estado (`AVAILABLE`, `PURCHASED`, `USE_REQUESTED`, `EXPIRED`); retorna `List<Benefit>`. |
| `BenefitCreateStudentDtosService` | Crear DTOs específicos para estudiante | `IBenefitGetStudentStateService`, `IBenefitGetPurchasesLeftByStudentService`, `IBenefitGetLastPurchaseService` | Mapea beneficios a `BenefitStudentResponseDto` incluyendo estado calculado, compras restantes (total y por estudiante). |

## Estrategias de Filtrado (BenefitFilterStrategyService)

| Estrategia | Estado | Lógica de filtrado |
|------------|--------|-------------------|
| `BenefitAvailableFilterService` | `AVAILABLE` | Filtra beneficios no expirados, con compras disponibles y no comprados por estudiante. |
| `BenefitPurchasedFilterService` | `PURCHASED` | Filtra beneficios con última compra en estado `PURCHASED`. |
| `BenefitUseRequestedFilterService` | `USE_REQUESTED` | Filtra beneficios con última compra en estado `USE_REQUESTED`. |
| `BenefitExpiredFilterService` | `EXPIRED` | Filtra beneficios expirados (fecha `endAt` anterior a actual). |

## DTOs y Validaciones

| DTO | Campos relevantes | Restricciones |
|-----|-------------------|---------------|
| `BenefitRequestDto` | `name`, `description`, `cost`, `purchaseLimit`, `purchaseLimitPerStudent`, `endAt`, `subjectId`, `icon`, `category`, `color` | `@NotBlank`, `@Size(max=100)` para `name`; `@NotBlank`, `@Size(max=1000)` para `description`; `@NotNull`, `@Min(1)` para `cost`; `@NotNull`, `@Future` para `endAt`; `@NotNull` para `subjectId`, `icon`, `category`, `color`; `purchaseLimit` y `purchaseLimitPerStudent` son opcionales (Integer); mensajes de `ValidationMessages`. |
| `BenefitResponseDto` | `id`, `name`, `description`, `cost`, `purchaseLimit`, `purchaseLimitPerStudent`, `endAt`, `state`, `subjectDto`, `icon`, `category`, `color` | Sin Bean Validation; usado como salida; incluye estado calculado (`PUBLISHED` o `EXPIRED`). |
| `BenefitStudentResponseDto` | `id`, `name`, `description`, `cost`, `state`, `purchasesLeft`, `purchasesLeftByStudent`, `endAt`, `subjectId`, `subjectName`, `icon`, `category`, `color` | Sin Bean Validation; usado como salida para estudiantes; incluye estado del beneficio para el estudiante (`AVAILABLE`, `PURCHASED`, `USE_REQUESTED`, `EXPIRED`), compras restantes (total y por estudiante). |
| `BenefitStudentCountResponseDto` | `available`, `purchased`, `use_requested`, `used`, `expired` | Sin Bean Validation; usado como salida; conteos por estado. |
| `BenefitPurchaseRequestDto` | `benefitId` | `@NotNull` para `benefitId`; mensaje de `ValidationMessages`. |
| `BenefitPurchaseResponseDto` | `id`, `state`, `purchasedAt`, `purchasesLeft`, `purchasesLeftByStudent`, `benefitDto`, `student` | Sin Bean Validation; usado como salida; incluye compras restantes después de la compra. |
| `BenefitPurchaseSimpleResponseDto` | `id`, `state`, `benefitId`, `benefitName`, `benefitCategory`, `benefitColor`, `benefitIcon`, `subjectId`, `subjectName`, `studentId`, `studentName` | Sin Bean Validation; usado como salida para listados; información simplificada de compra, beneficio, materia y estudiante. |
| `BenefitPurchasedUsedResponseDto` | `id`, `state`, `benefitId`, `benefitName`, `benefitDescription`, `category`, `color`, `icon`, `subjectId`, `subjectName`, `usedAt` | Sin Bean Validation; usado como salida para beneficios usados; incluye fecha de uso (`usedAt`). |

## Dependencias Externas Principales

### Módulo Admin
- `ISubjectGetByIdService`: Obtener materia por ID
- `ISubjectGetByStudentService`: Obtener materias del estudiante
- `ISubjectHasStudentService`: Validar inscripción de estudiante en materia
- `ITeacherGetByEmailService`: Obtener docente por email
- `IStudentGetByEmailService`: Obtener estudiante por email

### Módulo Economy
- `ITransactionGenerateService`: Generar transacciones (`COMPRA`, `REEMBOLSO`)

### Utils y Config
- `PaginatorUtils`: Construir `Pageable` con parámetros de paginación
- `PaginationHelper`: Crear `PaginatedData` desde `Page`
- `BenefitSpecs`: Especificaciones JPA para filtrado dinámico
- `BenefitPurchasesSpecs`: Especificaciones JPA para filtrado de compras

## Riesgos de Negocio Sin Cobertura de Pruebas Actual

1. **Validación de pertenencia docente-materia en generación:** Riesgo de creación de beneficios sin autorización si falla `subject.hasTeacherByEmail`.
2. **Reembolsos en eliminación de beneficios:** Riesgo de pérdida de dinero si transacciones de reembolso fallan o si se omiten compras activas.
3. **Validación de límites de compra:** Riesgo de ventas excesivas si validaciones de límite total o por estudiante fallan.
4. **Estado de expiración:** Riesgo de permitir compras/acciones en beneficios expirados si validación `isExpired()` falla.
5. **Validación de compras activas:** Riesgo de múltiples compras sin usar si `BenefitValidateIfPurchasedByStudentService` falla.
6. **Transiciones de estado en solicitud/aceptación de uso:** Riesgo de estados inconsistentes si transiciones `PURCHASED → USE_REQUESTED → USED` no se validan correctamente.
7. **Filtrado por estado de estudiante:** Riesgo de mostrar beneficios incorrectos si estrategias de filtrado fallan.
8. **Paginación con filtros complejos:** Riesgo de resultados incorrectos si especificaciones JPA o filtros dinámicos fallan.
9. **Validación de inscripción estudiante-materia:** Riesgo de permitir compras de estudiantes no inscritos si `subjectHasStudentService` falla.
10. **Decremento de compras restantes:** Riesgo de contadores incorrectos si `decrementPurchasesLeft()` o cálculo de compras restantes por estudiante fallan.

