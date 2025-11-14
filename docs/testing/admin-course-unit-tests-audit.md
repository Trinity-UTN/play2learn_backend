# Auditoría de Servicios, Controladores y DTOs — Admin Course

## Resumen ejecutivo
- **Cobertura actual:** inexistente en services/controllers/DTOs; riesgo de regresiones altas.
- **Dependencias críticas:** `YearGetByIdService`, `CourseExistByService`, `CourseSpecs`, `PaginationHelper`, `ResponseFactory`.
- **Riesgos detectados:** validaciones centrales sin pruebas (unicidad por año, soft delete, relaciones estudiantes/materias), respuestas HTTP sin asserts.

## Inventario de controladores

### `CourseRegisterController`
- **Endpoint:** `POST /admin/courses`
- **Casos de uso:** CU6 crear curso nuevo.
- **Validaciones externas:** `@SessionRequired ROLE_ADMIN`; bean validation `CourseRequestDto`.
- **Dependencias:** `CourseRegisterService`, `ResponseFactory`, `SuccessfulMessages`.
- **Excepciones esperadas (via servicio):** `ConflictException` (duplicado), `BadRequestException` (datos inválidos via servicio).

### `CourseUpdateController`
- **Endpoint:** `PUT /admin/courses/{id}`
- **Casos de uso:** CU14 actualizar curso existente.
- **Validaciones externas:** `@SessionRequired ROLE_ADMIN`; bean validation `CourseUpdateDto`.
- **Dependencias:** `ICourseUpdateService`, `ResponseFactory`.
- **Excepciones esperadas:** `NotFoundException` (ID inexistente), `ConflictException` (nombre duplicado en mismo año).

### `CourseDeleteController`
- **Endpoint:** `DELETE /admin/courses/{id}`
- **Casos de uso:** CU15 borrado lógico de curso.
- **Validaciones externas:** `@SessionRequired ROLE_ADMIN`.
- **Dependencias:** `ICourseDeleteService`, `ResponseFactory`.
- **Excepciones esperadas:** `NotFoundException` (curso no encontrado), `ConflictException` (estudiantes o materias asociadas).

### `CourseListController`
- **Endpoint:** `GET /admin/courses`
- **Casos de uso:** CU9 listado no paginado (sin eliminados).
- **Validaciones externas:** `@SessionRequired` (roles ADMIN/TEACHER/STUDENT).
- **Dependencias:** `CourseListService`, `ResponseFactory`.
- **Escenarios edge:** sin cursos disponibles, respuesta lista vacía.

### `CourseListPaginatedController`
- **Endpoint:** `GET /admin/courses/paginated`
- **Casos de uso:** CU16 listado paginado con filtros dinámicos.
- **Validaciones externas:** `@SessionRequired` (roles ADMIN/TEACHER/STUDENT); parámetros `page`, `page_size`, `order_*`, `search`, `filters`.
- **Dependencias:** `ICourseListPaginatedService`, `ResponseFactory`.
- **Escenarios edge:** filtros/valores de longitud desigual, parámetros omitidos (usa defaults), búsqueda vacía.

### `CourseGetController`
- **Endpoint:** `GET /admin/courses/{id}`
- **Casos de uso:** CU17 obtener curso por ID.
- **Validaciones externas:** `@SessionRequired` (roles ADMIN/TEACHER/STUDENT).
- **Dependencias:** `ICourseGetService`, `ResponseFactory`.
- **Excepciones esperadas:** `NotFoundException`.

## Inventario de servicios

### `CourseRegisterService`
- **Responsabilidad:** registrar curso (CU6).
- **Flujo clave:** obtiene `Year` mediante `YearGetByIdService`; valida duplicados por nombre+año con `CourseExistByService`; persiste via `ICourseRepository.save`.
- **Validaciones/Reglas:** conflicto si ya existe curso mismo nombre/año; depende de `CourseMapper` (excluido para testing según alcance).
- **Excepciones:** `ConflictException` (duplicado), `BadRequestException` (delegado a dependencias).
- **Dependencias externas:** `YearGetByIdService`, `CourseExistByService`, `ICourseRepository`, `CourseMapper`.

### `CourseUpdateService`
- **Responsabilidad:** actualizar curso (CU14).
- **Flujo clave:** obtiene curso existente (`ICourseGetByIdService`), valida unicidad excluyendo ID (`ICourseExistByService.validateExceptId`), actualiza usando `CourseMapper.toUpdateModel`.
- **Excepciones:** `NotFoundException`, `ConflictException`.
- **Dependencias externas:** `ICourseRepository`, `ICourseExistByService`, `ICourseGetByIdService`, `CourseMapper`.

### `CourseDeleteService`
- **Responsabilidad:** borrado lógico (CU15).
- **Flujo clave:** obtiene curso (soft delete), valida inexistencia de estudiantes/materias asociados (`IStudentsExistByCourseService`, `ISubjectsExistsByCourseService`), marca `delete()` y guarda.
- **Excepciones:** `NotFoundException`, `ConflictException` (relaciones activas).
- **Dependencias externas:** `ICourseRepository`, `ICourseGetByIdService`, `IStudentsExistByCourseService`, `ISubjectsExistsByCourseService`.

### `CourseListService`
- **Responsabilidad:** listado simple (CU9).
- **Flujo clave:** `ICourseRepository.findAllByDeletedAtIsNull`, mapea a DTO.
- **Escenarios:** lista vacía, cursos retornados sin eliminados.
- **Dependencias externas:** `ICourseRepository`, `CourseMapper`.

### `CourseListPaginatedService`
- **Responsabilidad:** listado paginado (CU16).
- **Flujo clave:** construye `Pageable` con `PaginatorUtils`, compone `Specification` (`CourseSpecs.notDeleted`, `nameContains`, `genericFilter`), ejecuta `courseRepository.findAll`, convierte con `CourseMapper`, arma `PaginatedData` con `PaginationHelper`.
- **Validaciones:** filtra eliminados, soporta filtros dinámicos y búsqueda textual.
- **Dependencias externas:** `ICourseRepository`, `CourseSpecs`, `PaginatorUtils`, `PaginationHelper`, `CourseMapper`.

### `CourseGetService`
- **Responsabilidad:** obtener curso por ID (CU17).
- **Flujo clave:** delega a `ICourseGetByIdService` y mapea a DTO.
- **Excepciones:** `NotFoundException`.
- **Dependencias externas:** `ICourseGetByIdService`, `CourseMapper`.

### Servicios comunes
- **`CourseExistByService`:** validaciones de existencia por nombre/año/ID; lanza `ConflictException` en `validateExceptId`.
- **`CourseExistByYearService`:** consulta soft-delete por año (`existsByYearIdAndDeletedAtIsNull`).
- **`CourseGetByIdService`:** obtiene curso no eliminado; lanza `NotFoundException`.
- **Interfaces:** definen contratos usados por controladores y otros servicios.

## Inventario de DTOs

### `CourseRequestDto`
- **Campos:** `name`, `year_id`.
- **Validaciones:** `@NotEmpty`, `@Size(max=50)`, `@Pattern` (solo letras y espacios con acentos), `@NotNull` para `year_id`.
- **Mensajes:** usa `ValidationMessages` estáticos.

### `CourseUpdateDto`
- **Campos:** `name`.
- **Validaciones:** mismas restricciones que `CourseRequestDto` excepto `year_id`.

### `CourseResponseDto`
- **Campos:** `id`, `name`, `year` (`YearResponseDto`).
- **Uso:** respuesta para controladores y servicios de lectura.

## Dependencias externas y puntos de integración
- **Persistencia:** `ICourseRepository` (consulta por nombre, ID, estado lógico, paginación).
- **Servicios auxiliares:** `YearGetByIdService`, `CourseExistByService`, `CourseGetByIdService`, `CourseExistByYearService`.
- **Otros módulos:** validación de estudiantes (`IStudentsExistByCourseService`), materias (`ISubjectsExistsByCourseService`), `Year` DTO/Model.
- **Utilidades:** `CourseMapper`, `CourseSpecs`, `PaginatorUtils`, `PaginationHelper`, `ResponseFactory`, `SuccessfulMessages`.
- **Seguridad:** `@SessionRequired` con roles (ADMIN/TEACHER/STUDENT).

## Oportunidades de pruebas
- **Services (unitarias con mocks):** validar flujos felices, conflictos, excepciones esperadas, persistencia de soft delete.
- **Controllers (WebMvc):** verificar códigos 201/200/204/409/404, validación de payloads, roles.
- **DTOs (Bean Validation):** asegurar mensajes configurados y límites.
- **Escenarios edge prioritarios:** filtros paginados vacíos/mismatch, cursos ya eliminados, nombre con tildes, año inexistente, relaciones activas.

## Riesgos pendientes / Huecos identificados
- No hay aseguramiento de que `CourseExistByService.validate(name)` distinga mayúsculas/minúsculas; se requiere prueba.
- `CourseListPaginatedService` confía en alineación `filters` vs `filterValues`; falta manejo explícito cuando difieren → agregar prueba para garantizar resultado sin NPE.
- Dependencia con servicios de estudiantes/materias: los mocks deben cubrir ambos caminos (con y sin asociaciones).
- Validaciones de DTO aceptan solo letras; falta prueba de rechazo a guiones/números usados en catálogos reales (confirmar con negocio).

## Próximos pasos recomendados
1. Ejecutar T02 para definir estrategia y fixtures compartidos.
2. Configurar dobles para repositorios y servicios externos (`IStudentsExistByCourseService`, `ISubjectsExistsByCourseService`).
3. Priorizar suites de servicios de registro/actualización/borrado antes de controladores.


