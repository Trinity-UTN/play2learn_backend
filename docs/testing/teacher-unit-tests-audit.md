# Auditoría de pruebas — Módulo Admin Teacher

## Resumen Ejecutivo
- Alcance: controllers, services (incluye commons) y DTOs del módulo `admin/teacher`, excluyendo models, repositories, mappers y specs según directriz.
- Responsabilidades principales: alta y actualización de docentes, baja/restauración con soft delete sincronizado con usuarios, consultas (simple, listado completo y paginado) y exposición de DTOs enriquecidos con estado activo y usuario asociado.
- Validaciones clave: Bean Validation sobre datos personales, unicidad de DNI vía servicio commons, bloqueo de baja cuando existen materias asociadas, filtros/paginación dinámicos mediante `TeacherSpecs`, y resguardo de endpoints con `@SessionRequired`.

## Inventario de Controllers
| Controller | Endpoint(s) | Roles requeridos | Service(s) consumidos | Validaciones / Notas |
|------------|-------------|------------------|------------------------|----------------------|
| `TeacherRegisterController` | `POST /admin/teachers` | `ADMIN` | `ITeacherRegisterService` | Body `TeacherRequestDto` validado; crea usuario con rol docente y retorna 201 con mensaje de creación. |
| `TeacherUpdateController` | `PUT /admin/teachers/{id}` | `ADMIN` | `ITeacherUpdateService` | Path `id` obligatorio; body `TeacherUpdateDto` validado; responde 200 con DTO actualizado. |
| `TeacherDeleteController` | `DELETE /admin/teachers/{id}` | `ADMIN` | `ITeacherDeleteService` | Ejecuta soft delete tras validar que no existan materias asociadas; retorna 204 sin body. |
| `TeacherRestoreController` | `PATCH /admin/teachers/restore/{id}` | `ADMIN` | `ITeacherRestoreService` | Restaura docente eliminado y devuelve DTO con mensaje de restauración exitosa. |
| `TeacherGetController` | `GET /admin/teachers/{id}` | `ADMIN`, `TEACHER`, `STUDENT` | `ITeacherGetService` | Devuelve DTO completo del docente activo; utiliza `SuccessfulMessages.okSuccessfully()`. |
| `TeacherListController` | `GET /admin/teachers` | `ADMIN`, `TEACHER`, `STUDENT` | `ITeacherListService` | Lista completa; revisar exclusión de registros con `deletedAt` distinto de `null`. |
| `TeacherListPaginatedController` | `GET /admin/teachers/paginated` | `ADMIN`, `TEACHER`, `STUDENT` | `ITeacherListPaginatedService` | Query params: `page`, `page_size`, `order_by`, `order_type`, `search`, `filters`, `filtersValues`; responde `PaginatedData<TeacherResponseDto>`. |

## Inventario de Services (core)
| Service | Método(s) clave | Dependencias principales | Validaciones / Excepciones relevantes |
|---------|-----------------|--------------------------|---------------------------------------|
| `TeacherRegisterService` | `cu5RegisterTeacher` | `IUserCreateService`, `ITeacherRepository`, `ITeacherExistsByDniService`, `TeacherMapper` | Lanza `ConflictException` si el DNI ya existe; crea usuario con rol docente y persiste docente ligado al usuario. |
| `TeacherUpdateService` | `cu23UpdateTeacher` | `ITeacherRepository`, `ITeacherGetByIdService`, `ITeacherExistsByDniService`, `TeacherMapper` | Recupera docente, asegura unicidad de DNI excluyendo el propio ID y persiste cambios manteniendo el usuario existente. |
| `TeacherDeleteService` | `cu24DeleteTeacher` | `ITeacherGetByIdService`, `ISubjectExistsByTeacherService`, `ITeacherRepository` | Verifica asociación con materias antes de soft delete; `teacher.delete()` también marca al usuario relacionado. |
| `TeacherRestoreService` | `cu35RestoreTeacher` | `ITeacherGetByIdService`, `ITeacherRepository`, `TeacherMapper` | Recupera docentes eliminados, ejecuta `restore()` sincronizando usuario y devuelve DTO. |
| `TeacherGetService` | `cu28GetTeacherById` | `ITeacherGetByIdService`, `TeacherMapper` | Delegación directa; errores de `NotFound` gestionados en el servicio commons. |
| `TeacherListService` | `cu25ListTeachers` | `ITeacherRepository`, `TeacherMapper` | Retorna lista mapeada vía `findAll`; validar que registros eliminados queden excluidos. |
| `TeacherListPaginatedService` | `cu26ListTeachersPaginated` | `ITeacherPaginatedRepository`, `TeacherSpecs`, `PaginatorUtils`, `PaginationHelper`, `TeacherMapper` | Construye `Specification` con búsqueda y filtros; comentario indica que se removió restricción `notDeleted`, riesgo de incluir eliminados. |

## Servicios Commons
| Service | Responsabilidad | Dependencias | Notas de validación |
|---------|-----------------|--------------|---------------------|
| `TeacherGetByIdService` | Obtener docente activo o eliminado | `ITeacherRepository` | `findById` excluye eliminados (`deletedAt` nulo); `findDeletedById` cubre restauraciones con mensajes `NotFoundExceptionMesagges`. |
| `TeacherGetByEmailService` | Resolver docente por email | `ITeacherRepository` | Consulta correo del usuario asociado; lanza `NotFoundException` con mensaje en inglés ("Teacher"). |
| `TeacherExistsByDniService` | Validar unicidad de DNI | `ITeacherRepository` | Dos variantes de `validate`: global y excluyendo ID; lanza `ConflictException` con mensajes parametrizados. |

## DTOs y Validaciones
| DTO | Campos relevantes | Restricciones |
|-----|-------------------|---------------|
| `TeacherRequestDto` | `name`, `lastname`, `email`, `dni` | `@NotEmpty`, `@Size(≤50)` y `@Pattern` alfabético para nombre/apellido; email obligatorio con `@Email` y longitud ≤ 100; DNI numérico exacto de 8 dígitos. |
| `TeacherUpdateDto` | `name`, `lastname`, `dni` | Misma validación que el request; no permite modificar email. |
| `TeacherResponseDto` | `id`, `name`, `lastname`, `dni`, `user`, `active` | Sin Bean Validation; incluye `UserResponseDto` embebido y bandera `active` derivada de `deletedAt`. |

## Mapa de Dependencias Clave
- **Servicios externos**: `IUserCreateService` (creación de usuarios con rol docente), `ISubjectExistsByTeacherService` (valida asociaciones con materias antes de eliminar).
- **Infraestructura compartida**: `ResponseFactory`, `SuccessfulMessages`, `PaginatorUtils`, `PaginationHelper`, `TeacherSpecs`, anotación `@SessionRequired`.
- **Excepciones estandarizadas**: `NotFoundExceptionMesagges` y `ConflictExceptionMessages` para respuestas coherentes ante errores.

## Riesgos y Consideraciones para Pruebas
- Validar que `TeacherListService` y `TeacherListPaginatedService` excluyan docentes con `deletedAt` distinto de `null`; actualmente no hay filtro explícito (`Specification.where(null)` tras quitar `notDeleted`).
- Asegurar que `teacherInSubjectsService.validate` en `TeacherDeleteService` impida eliminar docentes asociados a materias y que la prueba cubra el conflicto.
- Verificar en `TeacherRegisterService` la creación del usuario con rol correcto y la propagación de errores cuando `userCreateService` detecta credenciales existentes.
- Cubrir restauraciones garantizando que `teacher.restore()` también reanime al usuario relacionado y que el DTO resultante refleje `active = true`.
- Probar `TeacherGetByEmailService` tanto para resoluciones exitosas como para `NotFoundException`, incluyendo mensaje localizado (actualmente "Teacher").

