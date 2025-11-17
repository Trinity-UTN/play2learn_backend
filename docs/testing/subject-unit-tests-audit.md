# Auditoría de pruebas — Módulo Admin Subject

## Resumen Ejecutivo
- Alcance: controllers, services (incluye commons) y DTOs del módulo `admin/subject`. Se excluyen models, repositories, mappers y specs según alcance solicitado.
- Responsabilidades principales: alta/actualización de materias, gestión de docentes y estudiantes asignados, consultas (listado simple y paginado), control de balances y restauración/borrado lógico.
- Validaciones clave: existencia de curso/docente/estudiantes, unicidad de nombre por curso, control de asociaciones previas, restricciones de saldo y verificaciones de sesión/roles (`@SessionRequired`).

## Inventario de Controllers
| Controller | Endpoint(s) | Roles requeridos | Service(s) consumidos | Validaciones / Notas |
|------------|-------------|------------------|------------------------|----------------------|
| `SubjectRegisterController` | `POST /admin/subjects` | `ADMIN` | `ISubjectRegisterService` | Body `SubjectRequestDto` validado con Bean Validation. |
| `SubjectUpdateController` | `PUT /admin/subjects/{id}` | `ADMIN` | `ISubjectUpdateService` | Path param `id`, body `SubjectUpdateRequestDto`. |
| `SubjectDeleteController` | `DELETE /admin/subjects/{id}` | `ADMIN` | `ISubjectDeleteService` | Retorna 204; lanza conflicto si hay estudiantes asociados. |
| `SubjectRestoreController` | `PATCH /admin/subjects/restore/{id}` | `ADMIN` | `ISubjectRestoreService` | Restituye soft delete; respuesta 200. |
| `SubjectGetController` | `GET /admin/subjects/{id}` | `ADMIN`, `TEACHER`, `STUDENT` | `ISubjectGetService` | Usa `ResponseFactory.ok`. |
| `SubjectListController` | `GET /admin/subjects`<br>`GET /admin/subjects/teacher` | `ADMIN/TEACHER/STUDENT` (lista general)<br>`TEACHER` (lista por docente) | `ISubjectListService`, `ISubjectListByTeacherService` | El segundo endpoint obtiene `User` vía `@SessionUser`. |
| `SubjectListPaginatedController` | `GET /admin/subjects/paginated` | `ADMIN`, `TEACHER`, `STUDENT` | `ISubjectListPaginatedService` | Query params `page`, `page_size`, `order_by`, `order_type`, `search`, `filters`, `filtersValues`. |
| `SubjectAssignTeacherController` | `PATCH /admin/subjects/assign-teacher`<br>`PATCH /admin/subjects/unassign-teacher/{subjectId}` | `ADMIN` | `ISubjectAssignTeacherService`, `ISubjectUnassignTeacherService` | Primer endpoint valida body con `SubjectAssignTeacherRequestDto`. |
| `SubjectAddStudentsController` | `PATCH /admin/subjects/add-students/{subjectId}` | _Sin anotación de sesión (revisar en pruebas)_ | `ISubjectAddStudentsService` | Acepta lista de IDs en body; responde 200. |
| `SubjectRemoveStudentsController` | `PATCH admin/subjects/remove-students/{subjectId}` | _Sin anotación de sesión_ | `ISubjectRemoveStudentsService` | Devuelve DTO con lista final de alumnos. |
| `SubjectRefillBalanceController` | `POST /admin/subjects/refill-balance` | `DEV` | `ISubjectRefillBalanceService` | Devuelve listado actualizado tras recarga; restringido a rol desarrollador. |

> Observación: `SubjectAddStudentsController` y `SubjectRemoveStudentsController` carecen de `@SessionRequired`; las pruebas deben confirmar si esto es intencional o un gap de seguridad.

## Inventario de Services
| Service | Método(s) clave | Dependencias principales | Validaciones / Excepciones relevantes |
|---------|-----------------|--------------------------|---------------------------------------|
| `SubjectRegisterService` | `cu28RegisterSubject` | `ICourseGetByIdService`, `ITeacherGetByIdService`, `IStudentGetByCourseService`, `ISubjectExistsByNameAndCourseService`, `ISubjectRepository` | NotFound al buscar curso/docente, Conflicto por nombre duplicado, auto-asigna estudiantes si no es opcional. |
| `SubjectUpdateService` | `cu29UpdateSubject` | `ISubjectGetByIdService`, `ICourseGetByIdService`, `ITeacherGetByIdService`, `ISubjectExistsByNameAndCourseService`, `ISubjectRepository` | Mantiene estudiantes existentes; valida unicidad excluyendo ID actual. |
| `SubjectDeleteService` | `cu30DeleteSubject` | `ISubjectGetByIdService`, `ISubjectRepository` | Conflicto si la materia tiene estudiantes asociados; aplica soft delete. |
| `SubjectRestoreService` | `cu34RestoreSubject` | `ISubjectGetByIdService`, `ISubjectRepository` | Busca materias eliminadas con `findDeletedById`, restaura y devuelve DTO. |
| `SubjectGetService` | `cu33GetSubjectById` | `ISubjectGetByIdService` | Solo mapeo a DTO, delega validaciones a commons. |
| `SubjectListService` | `cu31ListSubjects` | `ISubjectRepository` | Lista materias activas (`deletedAt` nulo). |
| `SubjectListPaginatedService` | `cu32ListSubjectsPaginated` | `ISubjectPaginatedRepository`, `PaginatorUtils`, `PaginationHelper`, `SubjectSpecs` | Aplica filtros dinámicos, búsqueda, paginación; requiere specs para `notDeleted`, `nameContains`, `genericFilter`. |
| `SubjectAddStudentsService` | `cu36AddStudentsToSubject` | `ISubjectGetByIdService`, `IStudentsGetByIdListService`, `ISubjectRepository` | NotFound si materia/estudiante falta; evita duplicados con `contains`. |
| `SubjectRemoveStudentsService` | `cu37RemoveStudentsFromSubject` | `ISubjectGetByIdService`, `IStudentsGetByIdListService`, `ISubjectRepository` | Usa `subject.removeStudents`; valida existencia previa. |
| `SubjectAssignTeacherService` | `cu49AssignTeacher` | `ISubjectGetByIdService`, `ITeacherGetByIdService`, `ISubjectRepository` | Transaccional; NotFound si docente/materia no existen. |
| `SubjectUnassignTeacherService` | `cu50UnassignTeacher` | `ISubjectGetByIdService`, `ISubjectRepository` | Transaccional; limpia referencia de docente. |
| `SubjectListTeacherService` | `cu57ListSubjectsByTeacher` | `ITeacherGetByEmailService`, `ISubjectRepository`, `SubjectMapper` | Recupera `User` desde sesión, busca docente por email y filtra por `deletedAt`. |
| `SubjectRefillBalanceService` | `cu58RefillBalance`, `calculateInitialBalance` | `ISubjectRepository`, `ITransactionGenerateService`, `SubjectMapper` | Recorre materias activas, recalcula `initialBalance`, genera transacciones `ASIGNACION` para diferencias positivas. |
| `SubjectRefillBalanceCronService` | `execute` (sched.) | `ISubjectRefillBalanceService` | Cron `0 0 0 1 * *`; integra con scheduler. |
| `SubjectAddBalanceService` | `execute` | `ISubjectRepository` | Valida monto > 0; actualiza `actualBalance` e `initialBalance`. |
| `SubjectRemoveBalanceService` | `execute` | `ISubjectRepository` | Valida monto > 0; decrementa `actualBalance`. |
| `SubjectAddStudentByCourseService` | `addStudentByCourse` | `ISubjectRepository` | Añade estudiante a todas las materias obligatorias del curso; transaccional. |
| `SubjectExistsByCourseService` | `validate` | `ISubjectRepository` | Lanza `ConflictException` si ya existen materias para el curso. |
| `SubjectExistsByService` | `validate` | `ISubjectRepository` | Impide eliminar docente asociado a materias (conflicto). |
| `SubjectGetByStudentService` | `getByStudent` | `ISubjectRepository` | Devuelve materias activas vinculadas a estudiante. |
| `SubjectHasStudentService` | `subjectHasStudent` | _N/A_ | Conflicto si el estudiante no está asociado a la materia (mensaje personalizado). |

## DTOs y Validaciones
| DTO | Campos relevantes | Restricciones |
|-----|-------------------|---------------|
| `SubjectRequestDto` | `name`, `courseId`, `teacherId` (opcional), `optional` | `@NotEmpty`, `@Size(max=50)`, `@Pattern` alfanumérico/acentos; `@NotNull` para curso y flag `optional`. |
| `SubjectUpdateRequestDto` | Igual que request | Misma validación que la creación. |
| `SubjectAssignTeacherRequestDto` | `subjectId`, `teacherId` | Ambos `@NotNull`. |
| `SubjectResponseDto` | Id, nombre, DTO de curso, DTO de docente, flags, balances, lista de estudiantes simplificados | Sin Bean Validation (solo salida). |
| `SubjectAddResponseDto` | Id, `subjectName`, `courseName`, lista de estudiantes simplificados | Uso en add/remove de alumnos. |
| `SubjectSimplifiedResponseDto` | Id, nombre, curso/docente, `optional`, balances | Salida resumida (sin validación). |

## Mapa de Dependencias Clave
- **Servicios externos al módulo**: `ICourseGetByIdService`, `IStudentGetByCourseService`, `IStudentsGetByIdListService`, `ITeacherGetByIdService`, `ITeacherGetByEmailService`, `ITransactionGenerateService`.
- **Commons/Helpers**: `SubjectMapper`, `ResponseFactory`, `SuccessfulMessages`, `PaginatorUtils`, `PaginationHelper`, `SubjectSpecs`, `@SessionRequired`, `@SessionUser`.
- **Excepciones centralizadas**: `NotFoundException`, `ConflictException` con mensajes de `ConflictExceptionMessages` y `NotFoundExceptionMesagges`.
- **Programación**: uso de `@Scheduled` en `SubjectRefillBalanceCronService`, dependiente de configuración de Spring Scheduler.

## Riesgos y Consideraciones para Pruebas
- Confirmar comportamiento cuando `SubjectRequestDto.optional` es `false`: se deben auto-asignar alumnos del curso; cubrir escenarios con múltiples estudiantes y duplicados.
- Verificar que los endpoints sin `@SessionRequired` (`add/remove students`) estén protegidos por filtros globales o requieren cobertura de seguridad adicional.
- Asegurar que `SubjectRefillBalanceService` calcule `initialBalance` correctamente según tamaño de `students` y que genere transacciones solo cuando haga falta recargar.
- Validar el flujo de restauración tras borrado lógico (materia con `deletedAt` no nulo) y que no se pueda eliminar materias con alumnos asociados.
- Comprender impactos cruzados con módulos `admin/course`, `admin/student`, `admin/teacher` y economía (`transaction`), especialmente para mocks/dobles necesarios en pruebas.

