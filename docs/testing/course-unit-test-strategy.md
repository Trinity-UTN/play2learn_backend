# Estrategia de Pruebas Unitarias — Módulo Admin Course

## 1. Alcance y objetivos
- **Cobertura objetivo:** services, controllers y DTOs en `trinity.play2learn.backend.admin.course`, excluyendo models, repositories, mappers y specs.
- **Objetivo de calidad:** ≥85 % de líneas y ≥75 % de ramas en services/controllers; 100 % assertions críticas sobre validaciones de DTOs.
- **Casos críticos:** creación/actualización con validaciones de unicidad, eliminación lógica con dependencias, listados con filtros/paginación y respuestas HTTP consistentes.
- **Riesgos mitigados:** regresiones en reglas de negocio (validación de Year, soft delete), errores de serialización, exposición de mensajes incorrectos.

## 2. Matriz de riesgo y priorización
| Componente | Riesgo | Justificación | Prioridad |
|------------|--------|---------------|-----------|
| `CourseRegisterService` | Alto | Entrada principal de cursos; conflictos por duplicado afectan catálogo. | P0 |
| `CourseUpdateService` | Alto | Cambios de nombre impactan integridad; valida unicidad y existencia. | P0 |
| `CourseDeleteService` | Alto | Soft delete debe detenerse si estudiantes/materias dependen. | P0 |
| `CourseListPaginatedService` | Medio | Filtros dinámicos complejos; errores afectan dashboards. | P1 |
| `CourseListService` | Medio | Debe ocultar eliminados; impacto moderado. | P1 |
| `CourseGetService` | Medio | Propaga `NotFound`; usado por múltiples flujos. | P1 |
| Controllers de escritura | Alto | Respuestas HTTP deben reflejar validaciones y errores. | P0 |
| Controllers de lectura | Medio | Serialización de listas/paginación, parámetros query. | P1 |
| DTOs | Medio | Validaciones de entrada definen mensajes al usuario. | P1 |

## 3. Suites planificadas
1. **`CourseRegisterServiceTest` & `CourseUpdateServiceTest`** (JUnit + Mockito)  
   - Mocks: `YearGetByIdService`, `CourseExistByService`, `ICourseRepository`, `ICourseGetByIdService`.  
   - Escenarios: éxito, duplicado, year inexistente, validación exceptId.

2. **`CourseDeleteServiceTest` & commons**  
   - Mocks: `ICourseRepository`, `ICourseGetByIdService`, `IStudentsExistByCourseService`, `ISubjectsExistsByCourseService`.  
   - Escenarios: relaciones activas (409), curso inexistente (404), borrado exitoso (marca deletedAt).

3. **`CourseListServiceTest`, `CourseListPaginatedServiceTest`, `CourseGetServiceTest`**  
   - Mocks: `ICourseRepository`, `CourseSpecs` (solo verificación de invocación), `PaginatorUtils` mediante `mockStatic`, `PaginationHelper`.  
   - Escenarios: lista vacía, filtros múltiples, búsqueda vacía, curso eliminado filtrado, not found.

4. **Controllers (`CourseWriteControllersTest`, `CourseReadControllersTest`)**  
   - `@WebMvcTest` con `MockMvc`, mocks de servicios correspondientes.  
   - Validar status 201/200/204/404/409, payload JSON, roles y mensajes.

5. **DTO Validation (`CourseDtoValidationTest`)**  
   - `jakarta.validation.Validator`.  
   - Casos: nombre vacío, longitud >50, caracteres inválidos, year_id nulo, payload válido.

## 4. Fixtures y utilidades compartidas
- **Builders estáticos:** `CourseTestData.course()` para entidad, `CourseDtoFactory.request()`/`update()`, `YearTestData.year()` (simulado).
- **Constantes:** `Long COURSE_ID = 1L`, `String COURSE_NAME = "Matemática"`, `Long YEAR_ID = 2025L`.
- **Helpers MockMvc:** configuración común (`ObjectMapper`, `MockMvc`) en clase base o `@BeforeEach`.
- **Verificación de mensajes:** usar `jsonPath("$.message")` con valores de `SuccessfulMessages`/`ValidationMessages`.

## 5. Configuración de dobles y técnicas
- **Mockito:** `@Mock`, `@InjectMocks`, uso de `ArgumentCaptor` para validar persistencia (`CourseRepository.save`).
- **Static mocks:** `MockedStatic<PaginatorUtils>` y `MockedStatic<PaginationHelper>` cuando corresponda; asegurar cierre en `@AfterEach`.
- **AssertJ:** `assertThatThrownBy`, `usingRecursiveComparison`, `containsExactly`.
- **Tiempo:** sin interacción con fechas; no se requiere clock fake.

## 6. Cobertura y métricas
- **Cobertura mínima:** 85 % líneas / 75 % ramas en services/controllers (medido con JaCoCo).  
- **Revisión:** reportes almacenados en `target/site/jacoco/index.html` y referenciados en `docs/testing/test_admin_course.md`.
- **Mutación (opcional):** evaluar introducir Pitest después de suites estables (no bloqueante).

## 7. Integración con pipeline
- **Comando estándar:** `./mvnw.cmd org.jacoco:jacoco-maven-plugin:prepare-agent test org.jacoco:jacoco-maven-plugin:report`.  
- **Reporte automatizado:** `python scripts/testing_report.py --module "admin/course" --output "docs/testing/test_admin_course.md"`.
- **Seguridad:** ejecutar `dependency-check` con cache local antes de pipeline final.

## 8. Plan de refinamiento
- Tras cada generación (T03, T05, T07, T09, T11) correr `/refine` para reducir duplicados, añadir `@DisplayName`, helpers y comentarios Given-When-Then.
- Validar que los refinamientos mantengan o incrementen cobertura.

## 9. Gestión de defectos
- Cualquier hallazgo se documentará en `docs/testing/test_admin_course.md` bajo sección “Incidencias abiertas”.
- Riesgos residuales (p.ej., validaciones dependientes de repositorios reales) se reportarán junto al resumen de cobertura.


