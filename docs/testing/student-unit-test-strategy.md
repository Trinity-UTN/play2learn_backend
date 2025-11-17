# Estrategia de pruebas unitarias — Módulo Admin Student

## 1. Objetivo
Establecer un plan de pruebas unitarias consistente para servicios, controladores y DTOs del módulo `admin/student`, garantizando que los flujos críticos (alta, actualización, eliminación/restauración, consultas y paginación) se verifiquen con suites aisladas, repetibles y alineadas con los estándares de calidad del proyecto.

## 2. Alcance y exclusiones
- **Incluye**:
  - Implementaciones de servicios (`StudentRegisterService`, `StudentUpdateService`, etc.) y servicios commons bajo `admin/student/services`.
  - Controladores HTTP publicados bajo `/admin/students`.
  - DTOs de entrada/salida declarados en `admin/student/dtos` y sus reglas de Bean Validation.
- **Excluye**:
  - Entidades JPA (`Student`), repositorios (`IStudentRepository`), mappers (`StudentMapper`, `ProfileMapper`, `WalletMapper`) y specs (`StudentSpects`), los cuales se cubrirán mediante pruebas de integración existentes o futuras.

## 3. Métricas y criterios de calidad
- Cobertura objetivo por categoría:
  - Servicios y commons: ≥ 90 % líneas / ≥ 80 % ramas.
  - Controladores WebMvc: ≥ 85 % líneas / ≥ 70 % ramas.
  - DTOs con Bean Validation: ≥ 95 % de restricciones ejercitadas (casos válidos/ inválidos).
- Todas las suites deben ejecutarse sin fallos ni warnings con `./mvnw.cmd test`.
- Mantener duración total del módulo ≤ 75 s en entorno local al ejecutar `./mvnw.cmd -pl admin -am test`.
- Cada caso debe documentar Given/When/Then mediante `@DisplayName` o comentarios breves.

## 4. Priorización de riesgos
| Riesgo | Impacto | Probabilidad | Mitigación en pruebas |
|--------|---------|--------------|-----------------------|
| Soft delete sin filtrado (listas devuelven estudiantes eliminados) | Alto | Medio | Casos en `StudentsListServicesTest` y `StudentListPaginatedServiceTest` que verifiquen exclusión de `deletedAt`. |
| Conflictos por DNI/email en actualización | Alto | Alto | Escenarios negativos en `StudentUpdateServiceTest` con mocks de `IStudentExistByDNIService` y `IUserCreateService` (si aplica). |
| Asignación automática de materias al registrar curso | Medio | Medio | Tests en `StudentRegisterServiceTest` asegurando invocación a `ISubjectAddStudentByCourseService` y comportamiento idempotente. |
| Paginación con filtros inconsistentes | Medio | Medio | Suites para `StudentListPaginatedServiceTest` cubriendo combinaciones inválidas de `filters/filtersValues`. |
| Restauración de estudiantes con dependencias (wallet/profile) | Medio | Bajo | Tests en `StudentRestoreServiceTest` validando DTO completo post-restauración. |

## 5. Suites planificadas
1. **Servicios de registro y actualización**
   - `StudentRegisterServiceTest`, `StudentUpdateServiceTest`.
   - Cubre flujos felices, conflictos (DNI/email duplicado), curso inexistente y asignación de materias.
2. **Servicios de eliminación y restauración**
   - `StudentDeleteServiceTest`, `StudentRestoreServiceTest`.
   - Verifica soft delete, restauración y persistencia de estado.
3. **Servicios de consulta simples**
   - `StudentGetServiceTest`, `StudentsListServicesTest`.
   - Incluye caminos `NotFound` y filtrado de eliminados.
4. **Servicio de listado paginado**
   - `StudentListPaginatedServiceTest`.
   - Control de paginación, búsqueda, filtros dinámicos y ordenamiento.
5. **Servicios commons de resolución**
   - `StudentGetByIdServiceTest`, `StudentGetByEmailServiceTest`, `StudentGetByCourseServiceTest`.
   - Escenarios felices y excepciones (`NotFoundException`).
6. **Servicios commons de existencia/colecciones**
   - `StudentExistByDNIServiceTest`, `StudentsExistByCourseServiceTest`, `StudentsGetByIdListServiceTest`.
   - Resultados booleanos, conflictos y listas compuestas.
7. **Controladores WebMvc de escritura**
   - `StudentWriteControllersTest` (POST, PUT, DELETE, PATCH restore).
   - Verificación de roles, payloads JSON y códigos 2xx/4xx.
8. **Controladores WebMvc de lectura**
   - `StudentReadControllersTest` (GET simple y paginado).
   - Validación de query params, paginación y sesiones multi-rol.
9. **DTO Validation**
   - `StudentDtoValidationTest` para `StudentRequestDto` y `StudentUpdateRequestDto`.
   - Casos válidos, campos vacíos, formatos inválidos y límites de tamaño.

## 6. Dobles de prueba y herramientas
- Mockito + JUnit 5 (`@ExtendWith(MockitoExtension.class)`), `ArgumentCaptor` para verificar interacciones clave.
- `@TestConfiguration` con `@MockBean` en suites WebMvc (`@WebMvcTest`) para inyectar dependencias simuladas.
- Utilizar `PageImpl` y helpers de `PaginationHelper` simulados en tests de paginación.
- Fábricas (`StudentTestMother`, `UserTestMother`, etc.) para generar objetos consistentes; crear nuevas si no existen.

## 7. Fixtures y utilidades compartidas
- Builders estáticos para estudiantes, usuarios, cursos y perfiles que respeten constraints (DNI de 8 dígitos, emails válidos).
- Helpers para generar listas paginadas (`buildPage` con `PageImpl`) y colecciones simuladas.
- Métodos utilitarios para construir requests JSON en WebMvc (headers de sesión, payloads frecuentes).
- Datos de validación (mensajes de `ValidationMessages`) centralizados para evitar strings mágicos.

## 8. Secuencia de trabajo recomendada
1. Implementar suites de servicios críticos (registro/actualización) junto con sus refinamientos.
2. Cubrir eliminación/restauración y commons de obtención por ID/email.
3. Abordar listado simple y paginado, incluyendo refinamientos para fixtures compartidos.
4. Implementar controllers (escritura → lectura) con configuración centralizada de MockMvc.
5. Finalizar con pruebas de Bean Validation y cualquier ajuste detectado durante JaCoCo.
6. A cada bloque, ejecutar `/refine` para reducir duplicación y mejorar expresividad.

## 9. Integración con quality gates
- Una vez integradas las suites principales, ejecutar `./mvnw.cmd org.jacoco:jacoco-maven-plugin:prepare-agent test org.jacoco:jacoco-maven-plugin:report` para verificar métricas objetivo.
- Generar reporte consolidado con `python scripts/testing_report.py --module "admin/student" --output "docs/testing/test_admin_student.md"` al finalizar la tanda de suites.
- Registrar incidencias o deudas en `docs/testing/test_admin_student.md` para seguimiento posterior.

## 10. Riesgos residuales y seguimiento
- Confirmar con el equipo funcional si la respuesta de `StudentUpdateController` usando `ResponseFactory.created` es intencional; ajustar pruebas en consecuencia.
- Supervisar dependencias cruzadas con módulos `admin/subject` (asignación de materias) y `admin/course` al mockear servicios externos; mantener contratos actualizados.
- Si se reintroduce filtrado `notDeleted` en `StudentListPaginatedService`, actualizar suites y fixtures para reflejar el nuevo comportamiento.

