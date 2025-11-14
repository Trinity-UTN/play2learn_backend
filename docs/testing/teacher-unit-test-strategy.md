# Estrategia de pruebas unitarias — Módulo Admin Teacher

## 1. Objetivo
Definir un plan de pruebas unitarias coherente para services, controllers y DTOs del módulo `admin/teacher`, garantizando cobertura de los flujos críticos (alta, actualización, baja/restauración, consultas y paginación) con suites aisladas, repetibles y alineadas a los estándares del repositorio.

## 2. Alcance y exclusiones
- **Incluye**:
  - Implementaciones de servicios (`TeacherRegisterService`, `TeacherUpdateService`, etc.) y servicios commons bajo `admin/teacher/services`.
  - Controladores HTTP publicados bajo `/admin/teachers`.
  - DTOs de entrada/salida definidos en `admin/teacher/dtos` y sus reglas de Bean Validation.
- **Excluye**:
  - Modelos JPA (`Teacher`), repositorios (`ITeacherRepository`, `ITeacherPaginatedRepository`), mappers (`TeacherMapper`) y specs (`TeacherSpecs`); se validarán mediante pruebas de integración o de otro nivel.

## 3. Métricas y criterios de calidad
- Cobertura objetivo:
  - Services y commons: ≥ 90 % líneas / ≥ 80 % ramas.
  - Controllers (`@WebMvcTest`): ≥ 85 % líneas / ≥ 70 % ramas.
  - DTOs Bean Validation: ≥ 95 % de restricciones ejercitadas (casos válidos e inválidos).
- Todas las suites deben correr limpias con `./mvnw.cmd test` sin warnings de Mockito ni logs inesperados.
- Duración total del módulo ≤ 60 s en entorno local al ejecutar `./mvnw.cmd -pl admin -am test`.
- Casos documentados con Given/When/Then mediante `@DisplayName` y asserts descriptivos (AssertJ + JSONPath).

## 4. Priorización de riesgos
| Riesgo | Impacto | Probabilidad | Mitigación en pruebas |
|--------|---------|--------------|-----------------------|
| Listados exponen docentes soft-deleted (falta filtro `notDeleted`) | Alto | Medio | Escenarios en `TeacherListServiceTest` y `TeacherListPaginatedServiceTest` verificando exclusión de `deletedAt`. |
| Eliminación sin validar asignaciones a materias | Alto | Medio | Tests en `TeacherDeleteServiceTest` forzando conflicto cuando `ISubjectExistsByTeacherService` detecta asociaciones. |
| Unicidad de DNI (alta/actualización) no respetada | Alto | Alto | Escenarios negativos en `TeacherRegisterServiceTest` y `TeacherUpdateServiceTest` con mocks de `TeacherExistsByDniService`. |
| Restauración no sincroniza al usuario asociado | Medio | Medio | Casos en `TeacherRestoreServiceTest` comprobando que `teacher.restore()` y `TeacherMapper` reflejan `active = true`. |
| Paginación: filtros dinámicos inconsistentes (`filters` vs `filtersValues`) | Medio | Bajo | Pruebas en `TeacherListPaginatedServiceTest` con combinaciones inválidas y validación de respuestas. |

## 5. Suites planificadas
1. **Servicios de registro y actualización**
   - `TeacherRegisterServiceTest`, `TeacherUpdateServiceTest`.
   - Cobertura: flujos felices, conflictos por DNI, inexistencia del docente, persistencia de usuario asociado.
2. **Servicios de baja y restauración**
   - `TeacherDeleteServiceTest`, `TeacherRestoreServiceTest`.
   - Verifica soft delete/restauración, validación de materias asociadas y sincronización con usuario.
3. **Servicios de consulta simples**
   - `TeacherGetServiceTest`, `TeacherListServiceTest`.
   - Incluye escenarios `NotFound`, exclusión de eliminados y orden de resultados.
4. **Servicio de listado paginado**
   - `TeacherListPaginatedServiceTest`.
   - Cobertura de paginación, búsqueda, filtros dinámicos, parámetros inválidos y DTOs resultantes.
5. **Servicios commons**
   - `TeacherGetByIdServiceTest`, `TeacherGetByEmailServiceTest`, `TeacherExistsByDniServiceTest`.
   - Escenarios felices y excepciones (`NotFoundException`, `ConflictException`).
6. **Controladores de escritura (`MockMvc`)**
   - `TeacherWriteControllersTest` (POST, PUT, DELETE, PATCH restore).
   - Validación de roles, payloads y códigos 2xx/4xx (409, 404, 401).
7. **Controladores de lectura (`MockMvc`)**
   - `TeacherReadControllersTest` (GET/{id}, GET, GET/paginated).
   - Validación de query params, metadatos de paginación y mensajes estándar.
8. **DTO Validation**
   - `TeacherDtoValidationTest` para `TeacherRequestDto` y `TeacherUpdateDto`.
   - Verifica combinaciones válidas, campos vacíos, patrones alfabéticos y longitud máxima.

## 6. Dobles de prueba y herramientas
- Mockito + JUnit 5 (`@ExtendWith(MockitoExtension.class)`) con `ArgumentCaptor` para validar interacciones críticas (`userCreateService`, `teacherRepository`).
- `@WebMvcTest` con `@MockBean` para inyectar servicios simulados en controllers.
- Uso de `PageImpl` para paginación y helpers custom para construir `PaginatedData`.
- `TeacherTestMother` (a crear si no existe) para generar docentes y usuarios consistentes.
- `JsonContentHelper` o métodos estáticos para requests/responses en suites WebMvc.

## 7. Fixtures y utilidades compartidas
- Constantes de DNI válidos (`"12345678"`), nombres/apellidos con caracteres acentuados permitidos, correos docentes.
- Builders para `Teacher`, `User`, `TeacherResponseDto` y DTOs de request/update.
- Métodos auxiliares para crear `PaginatedData<TeacherResponseDto>` y páginas (`PageImpl`) con metadatos coherentes.
- Tabla de mensajes (`ValidationMessages`, `ConflictExceptionMessages`, `SuccessfulMessages`) referenciada mediante constantes para evitar strings mágicos.

## 8. Secuencia de trabajo recomendada
1. Implementar suites de servicios críticos (registro/actualización) y ejecutar refinamiento inmediato.
2. Abordar baja/restauración y commons (`GetById`, `ExistsByDni`, `GetByEmail`).
3. Cubrir listados (simple y paginado) antes de pasar a controllers; centralizar fixtures reutilizables.
4. Implementar controllers de escritura → lectura con configuración MockMvc compartida y validación de roles.
5. Finalizar con Bean Validation y ajustes detectados por JaCoCo.
6. Tras cada bloque, aplicar `/refine` para reducir duplicidad y mejorar expresividad.

## 9. Integración con quality gates
- Ejecutar `./mvnw.cmd org.jacoco:jacoco-maven-plugin:prepare-agent test org.jacoco:jacoco-maven-plugin:report` cuando las suites principales estén listas, garantizando los umbrales propuestos.
- Consolidar métricas y hallazgos con `python scripts/testing_report.py --module "admin/teacher" --output "docs/testing/test_admin_teacher.md"` al completar el ciclo de pruebas.
- Registrar cualquier deuda o hallazgo residual en el reporte para seguimiento del equipo.

## 10. Riesgos residuales y seguimiento
- Confirmar con el equipo si `TeacherListPaginatedService` debe reinstaurar `notDeleted`; preparar suites para reaccionar a ese cambio.
- Mantener alineados los contratos con `IUserCreateService` y `ISubjectExistsByTeacherService`; actualizar mocks si cambian firmas o mensajes.
- Vigilar mensajes en inglés (`"Teacher"`) en excepciones para asegurar consistencia; incluir cobertura en pruebas si se homologa a español.

