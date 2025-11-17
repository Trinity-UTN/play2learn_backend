# Auditoría de pruebas — Módulo Admin Student

## Resumen Ejecutivo
- Alcance: controllers, services (incluye commons) y DTOs del módulo `admin/student`, excluyendo models, repositories, mappers y specs según pauta.
- Responsabilidades principales: alta y actualización de estudiantes, gestión de alta/baja lógica, restauración, consultas (simple y paginada) y exposición de DTOs enriquecidos con perfil, usuario y wallet.
- Validaciones clave: Bean Validation sobre datos personales, unicidad de DNI, verificación de curso existente, control de soft delete, filtros y paginación vía `StudentSpects`, y resguardo de endpoints mediante `@SessionRequired`.

## Inventario de Controllers
| Controller | Endpoint(s) | Roles requeridos | Service(s) consumidos | Validaciones / Notas |
|------------|-------------|------------------|------------------------|----------------------|
| `StudentRegisterController` | `POST /admin/students` | `ADMIN` | `StudentRegisterService` | Body `StudentRequestDto` validado; crea usuario+wallet+profile y asigna materias obligatorias del curso. |
| `StudentUpdateController` | `PUT /admin/students/{id}` | `ADMIN` | `IStudentUpdateService` | Path `id`, body `StudentUpdateRequestDto`; usa `ResponseFactory.created` para respuesta 200/201 con mensaje de actualización. |
| `StudentDeleteController` | `DELETE /admin/students/{id}` | `ADMIN` | `IStudentDeleteService` | Soft delete; retorna 204 sin body y mensaje estándar. |
| `StudentRestoreController` | `PATCH /admin/students/restore/{id}` | `ADMIN` | `IStudentRestoreService` | Busca registros eliminados, restaura y devuelve DTO completo. |
| `StudentGetController` | `GET /admin/students/{id}` | `ADMIN`, `TEACHER`, `STUDENT` | `IStudentGetService` | Devuelve DTO extendido con perfil/wallet; valida sesión multi-rol. |
| `StudentListController` | `GET /admin/students` | `ADMIN`, `TEACHER`, `STUDENT` | `IStudentListService` | Lista completa sin paginar; revisar exclusión de soft deletes (usa `findAll`). |
| `StudentListPaginatedController` | `GET /admin/students/paginated` | `ADMIN`, `TEACHER`, `STUDENT` | `IStudentListPaginatedService` | Query params: `page`, `page_size`, `order_by`, `order_type`, `search`, `filters`, `filtersValues`; respuesta `PaginatedData<StudentResponseDto>`. |

## Inventario de Services (core)
| Service | Método(s) clave | Dependencias principales | Validaciones / Excepciones relevantes |
|---------|-----------------|--------------------------|---------------------------------------|
| `StudentRegisterService` | `cu4registerStudent` | `CourseGetByIdService`, `IUserCreateService`, `ISubjectAddStudentByCourseService`, `IStudentRepository`, `ProfileMapper`, `WalletMapper`, `StudentMapper` | Verifica existencia de curso; delega unicidad de usuario a `userCreateService`; crea perfil y wallet con saldo inicial 0; auto-asigna materias obligatorias del curso. |
| `StudentUpdateService` | `cu18updateStudent` | `IStudentGetByIdService`, `IStudentExistByDNIService`, `ICourseGetByIdService`, `IStudentRepository`, `StudentMapper` | Detecta conflicto si DNI nuevo ya existe; cambia curso sólo si ID difiere; usa `ConflictExceptionMessages`. |
| `StudentDeleteService` | `cu19DeleteStudent` | `IStudentGetByIdService`, `IStudentRepository` | Aplica `student.delete()` (soft delete) y persiste. |
| `StudentRestoreService` | `cu38RestoreStudent` | `IStudentGetByIdService`, `IStudentRepository`, `StudentMapper` | Usa `findDeletedById`, aplica `restore()` y retorna DTO completo. |
| `StudentGetService` | `cu22GetStudent` | `IStudentGetByIdService`, `StudentMapper` | Solo delega obtención y mapeo; excepciones gestionadas en commons. |
| `StudentsListServices` | `cu20ListStudents` | `IStudentRepository`, `StudentMapper` | Devuelve lista completa de `findAll`; no filtra `deletedAt`. |
| `StudentListPaginatedService` | `cu21ListPaginatedStudents` | `IStudentRepository`, `StudentSpects`, `PaginatorUtils`, `PaginationHelper`, `StudentMapper` | Construye `Specification` con búsqueda y filtros dinámicos; comentario indica que `notDeleted` fue removido (riesgo de incluir eliminados). |

## Servicios Commons
| Service | Responsabilidad | Dependencias | Notas de validación |
|---------|-----------------|--------------|---------------------|
| `StudentGetByIdService` | Buscar estudiante activo o eliminado | `IStudentRepository` | Métodos `findById` y `findDeletedById` lanzan `NotFoundException` con mensajes centralizados. |
| `StudentGetByEmailService` | Resolver estudiante por email | `IStudentRepository` | `@Transactional(readOnly = true)`; excepción `NotFoundException` con mensaje personalizado. |
| `StudentGetByCourseService` | Obtener estudiantes por curso | `IStudentRepository` | Devuelve lista sin filtrar `deletedAt`. |
| `StudentsGetByIdListService` | Resolver lista de IDs | `IStudentGetByIdService` | Mapea IDs usando `findById`; propaga excepciones individuales. |
| `StudentExistByDNIService` | Validar existencia de DNI | `IStudentRepository` | Devuelve booleano para verificaciones de conflicto. |
| `StudentsExistByCourseService` | Detectar estudiantes asociados a curso | `IStudentRepository` | Lanza `ConflictException` si existen registros, con `ConflictExceptionMessages`. |

## DTOs y Validaciones
| DTO | Campos relevantes | Restricciones |
|-----|-------------------|---------------|
| `StudentRequestDto` | `name`, `lastname`, `email`, `dni`, `course_id`, `emailTutor`, `birthDate` | `@NotEmpty`, `@Size`, `@Pattern` para nombre/apellido; email obligatorio con `@Email`; DNI numérico de 8 dígitos; curso obligatorio; emailTutor opcional con validación; `birthDate` sin constraint. |
| `StudentUpdateRequestDto` | Igual a request excepto email (no editable) | Misma validación para nombre/apellido/DNI/curso; emailTutor opcional con restricciones. |
| `StudentResponseDto` | Id, datos personales, DTOs de usuario, curso, perfil y wallet | Sin Bean Validation; usado como salida en controllers/services. |
| `StudentSimplificatedResponse` | Id, nombre, apellido, DNI | DTO ligero sin validaciones. |

## Mapa de Dependencias Clave
- **Servicios externos**: `CourseGetByIdService` (módulo `admin/course`), `ISubjectAddStudentByCourseService` (módulo `admin/subject`), `IUserCreateService` (módulo usuarios), `ProfileMapper` y `WalletMapper` (perfiles/economía).
- **Infraestructura compartida**: `ResponseFactory`, `SuccessfulMessages`, `PaginatorUtils`, `PaginationHelper`, `StudentSpects`, anotación `@SessionRequired`.
- **Excepciones centralizadas**: `NotFoundException` y `ConflictException` con mensajes de `NotFoundExceptionMesagges` y `ConflictExceptionMessages`.

## Riesgos y Consideraciones para Pruebas
- Verificar que `StudentsListServices` y `StudentListPaginatedService` excluyan estudiantes con `deletedAt` distinto de `null`; actualmente no se aplica filtro explícito.
- Confirmar que las asignaciones de materias en `StudentRegisterService` manejen correctamente cursos sin materias obligatorias y situaciones con estudiantes ya asociados (idempotencia).
- Asegurar que `StudentUpdateService` cubre también conflicto por email (actualmente sólo valida DNI); pruebas deben evidenciar comportamiento esperado ante emails duplicados.
- Probar restauración de estudiantes con dependencias (wallet/profile) para garantizar que `StudentMapper.toDto` incluye información consistente tras `restore()`.
- Validar manejo de filtros y parámetros inconsistentes en `StudentListPaginatedService`, especialmente cuando `filters` y `filtersValues` difieren en tamaño o contienen valores desconocidos.

