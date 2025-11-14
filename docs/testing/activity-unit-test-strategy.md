# Estrategia de pruebas unitarias — Módulo Activity

## 1. Objetivo
Definir un plan de pruebas unitarias coherente para services, controllers y DTOs del módulo `activity` y todos sus submódulos (`activity`, `activity/ahorcado`, `activity/arbolDeDecision`, `activity/clasificacion`, `activity/completarOracion`, `activity/memorama`, `activity/noLudica`, `activity/ordenarSecuencia`, `activity/preguntados`), garantizando cobertura de flujos críticos (generación, inicio/completado, listados paginados, conteo de estados, gestión de balance) con suites aisladas, repetibles y alineadas a los estándares del repositorio.

## 2. Alcance y exclusiones
- **Incluye**:
  - Implementaciones de servicios principales (`ActivityGetService`, `ActivityStartService`, `ActivityCompletedService`, etc.) y servicios commons bajo `activity/services`.
  - Controladores HTTP publicados bajo `/activity` y `/activities/*`.
  - DTOs de entrada/salida definidos en `activity/dtos` y submódulos con sus reglas de Bean Validation.
  - Servicios de generación de actividades lúdicas (ahorcado, arbolDeDecision, clasificacion, completarOracion, memorama, ordenarSecuencia, preguntados) y no lúdicas.
- **Excluye**:
  - Modelos JPA (`Activity`, `Ahorcado`, `ArbolDeDecisionActivity`, etc.), repositorios (`IActivityRepository`, `IAhorcadoRepository`, etc.), mappers (`ActivityMapper`, `AhorcadoMapper`, etc.) y specs (`ActivitySpecs`); se validarán mediante pruebas de integración o de otro nivel.

## 3. Métricas y criterios de calidad
- Cobertura objetivo por categoría:
  - Services principales y commons: ≥ 85 % líneas / ≥ 75 % ramas.
  - Controllers (`@WebMvcTest`): ≥ 85 % líneas / ≥ 70 % ramas.
  - DTOs Bean Validation: ≥ 95 % de restricciones ejercitadas (casos válidos e inválidos).
  - Services de generación de submódulos: ≥ 85 % líneas / ≥ 75 % ramas.
- Todas las suites deben correr limpias con `./mvnw.cmd test` sin warnings de Mockito ni logs inesperados.
- Duración total del módulo ≤ 90 s en entorno local al ejecutar `./mvnw.cmd -pl activity -am test`.
- Casos documentados con Given/When/Then mediante `@DisplayName` y asserts descriptivos (AssertJ + JSONPath).
- Suites deben validar estrategias dinámicas (mappers por tipo de actividad, estrategias de completado).

## 4. Priorización de riesgos

### Módulo Principal Activity

| Riesgo | Impacto | Probabilidad | Mitigación en pruebas |
|--------|---------|--------------|-----------------------|
| `ActivityGetService` falla al seleccionar mapper por reflexión (tipo desconocido) | Alto | Medio | Escenarios en `ActivityGetServiceTest` verificando selección correcta de mapper y lanzamiento de `IllegalArgumentException` para tipos desconocidos. |
| `ActivityStartService` no finaliza correctamente intento previo en curso | Alto | Medio | Tests en `ActivityStartServiceTest` verificando finalización automática de intento previo como desaprobado y cálculo correcto de intentos restantes tras finalización. |
| `ActivityCompletedService` calcula incorrectamente tiempo transcurrido (desaprobación automática) | Alto | Medio | Escenarios en `ActivityCompletedServiceTest` validando cálculo de tiempo en minutos y desaprobación automática cuando excede `maxTime`. |
| `ActivityNoLudicaCompletedService` no valida contenido (`plainText` o `file` requerido) | Alto | Medio | Casos en `ActivityNoLudicaCompletedServiceTest` verificando validación de contenido y manejo de archivos mediante UploadCare. |
| Servicios de listado paginado no manejan correctamente listas vacías | Medio | Bajo | Tests en `ActivityApprovedListPaginatedServiceTest` y `ActivityNotApprovedListPaginatedServiceTest` validando retorno de `Page.empty` cuando no hay actividades. |
| Filtro especial `disapproved` en `ActivityNotApprovedListPaginatedService` interfiere con otros filtros | Medio | Medio | Escenarios en `ActivityNotApprovedListPaginatedServiceTest` validando combinación de filtro `disapproved` con filtros dinámicos. |
| `ActivityAddBalanceService` y `ActivityRemoveBalanceService` permiten balances negativos | Medio | Bajo | Tests verificando que servicios validen montos y no permitan balances negativos. |
| `ActivityStudentCountService` clasifica incorrectamente actividades por estado | Medio | Medio | Casos en `ActivityStudentCountServiceTest` validando clasificación correcta por `PUBLISHED`, `EXPIRED` y conteo sin duplicados. |

### Submódulos

| Riesgo | Impacto | Probabilidad | Mitigación en pruebas |
|--------|---------|--------------|-----------------------|
| Servicios de generación no validan asignación docente-materia | Alto | Alto | Tests en todos los `*GenerateServiceTest` verificando `ConflictException` cuando docente no está asignado a materia. |
| Servicios de validación específicos (`IClasificacionValidateCategoriesNamesService`, etc.) no detectan duplicados | Alto | Medio | Escenarios en servicios de generación validando detección de nombres duplicados y lanzamiento de excepciones apropiadas. |
| Generación de transacciones `ACTIVIDAD` falla en algún submódulo | Medio | Bajo | Tests verificando invocación correcta a `ITransactionGenerateService` con parámetros consistentes en todos los submódulos. |
| DTOs anidados (preguntas/opciones, decisiones/consecuencias) no validan correctamente | Medio | Medio | Suites de validación DTO (`*DtoValidationTest`) cubriendo restricciones anidadas y mensajes de error claros. |

## 5. Suites planificadas

### Módulo Principal Activity

1. **Servicios principales de consulta y gestión**
   - `ActivityGetServiceTest`, `ActivityStartServiceTest`, `ActivityCompletedServiceTest`, `ActivityNoLudicaCompletedServiceTest`.
   - Cobertura: selección dinámica de mappers, validación de estado publicado, manejo de intentos, finalización de intentos previos, cálculo de tiempo, estrategias de completado.

2. **Servicios de listado y conteo**
   - `ActivityStudentCountServiceTest`, `ActivityListApprovedByStudentServiceTest`, `ActivityListNotApprovedByStudentServiceTest`.
   - Incluye escenarios de clasificación por estado, filtrado de aprobadas/no aprobadas y creación de DTOs.

3. **Servicios de listado paginado**
   - `ActivityListByTeacherPaginatedServiceTest`, `ActivityApprovedListPaginatedServiceTest`, `ActivityNotApprovedListPaginatedServiceTest`.
   - Cobertura de paginación, búsqueda, filtros dinámicos (incluyendo filtro especial `disapproved`), listas vacías y DTOs resultantes.

4. **Servicios de balance**
   - `ActivityAddBalanceServiceTest`, `ActivityRemoveBalanceServiceTest`.
   - Verifica incremento/decremento de balance y validación de montos.

5. **Controladores de consulta y gestión (`MockMvc`)**
   - `ActivityGetControllerTest`, `ActivityStartedControllerTest`, `ActivityCompletedControllerTest`, `ActivityCountByStudentStateControllerTest`.
   - Validación de roles (`ROLE_STUDENT`), payloads JSON, multipart/form-data y códigos 2xx/4xx.

6. **Controladores de listado (`MockMvc`)**
   - `ActivityListByStudentControllerTest`, `ActivityListPaginatedByStudentControllerTest`, `ActivityListByTeacherPaginatedControllerTest`.
   - Validación de query params, metadatos de paginación, sesiones multi-rol y mensajes estándar.

7. **DTO Validation (Módulo Principal)**
   - `ActivityCreatedDtoValidationTest` (para `ActivityRequestDto`), `ActivityCompletedDtoValidationTest` (para `ActivityCompletedRequestDto`), `ActivityStudentDtoValidationTest` (para DTOs de respuesta estudiante).
   - Verifica restricciones de fechas, campos requeridos, estados y mensajes de validación.

### Submódulos (cada uno incluye)

1. **Servicio de generación**
   - `*GenerateServiceTest` (ej: `AhorcadoGenerateServiceTest`, `ArbolDecisionGenerateServiceTest`).
   - Cobertura: validación de asignación docente-materia, validaciones específicas (nombres únicos, opciones correctas, etc.), generación de transacción `ACTIVIDAD`, persistencia de actividad.

2. **Controller de generación (`MockMvc`)**
   - `*GenerateControllerTest` (ej: `GenerateAhorcadoControllerTest`, `ArbolDeDecisionGenerateControllerTest`).
   - Validación de roles (`ROLE_ADMIN`, `ROLE_TEACHER`), payloads JSON y códigos 2xx/4xx.

3. **DTO Validation**
   - `*DtoValidationTest` (ej: `AhorcadoDtoValidationTest`, `ArbolDeDecisionDtoValidationTest`).
   - Verifica restricciones específicas del tipo de actividad, campos anidados (preguntas/opciones, decisiones/consecuencias, etc.) y mensajes de validación.

## 6. Dobles de prueba y herramientas
- Mockito + JUnit 5 (`@ExtendWith(MockitoExtension.class)`) con `ArgumentCaptor` para validar interacciones críticas (`IActivityCompletedRepository`, `IActivityRepository`, servicios externos).
- `@WebMvcTest` con `@MockBean` para inyectar servicios simulados en controllers.
- Uso de `PageImpl` para paginación y helpers custom para construir `PaginatedData`.
- `MockedStatic` para servicios estáticos (`PaginatorUtils`, `PaginationHelper`) en tests de paginación.
- `ActivityTestMother` (a crear si no existe) para generar actividades, estudiantes, docentes y usuarios consistentes.
- Fixtures específicas por tipo de actividad (`AhorcadoTestMother`, `PreguntadosTestMother`, etc.) para generar datos específicos de cada submódulo.
- `JsonContentHelper` o métodos estáticos para requests/responses en suites WebMvc.
- Mocks de `MultipartFile` para pruebas de `ActivityNoLudicaCompletedService` y controllers que consumen multipart/form-data.
- Mocks de servicios externos (`ITransactionGenerateService`, UploadCare service) con verificación de parámetros.

## 7. Fixtures y utilidades compartidas

### Módulo Principal Activity
- Constantes de actividades: IDs, nombres, descripciones, fechas (`startDate`, `endDate`), dificultades (`EASY`, `MEDIUM`, `HARD`), tiempos máximos, intentos, balances iniciales.
- Builders para `Activity` (genérico), `ActivityCompleted`, `ActivityCompletedRequestDto`, `ActivityCompletedResponseDto`, `ActivityStudentNotApprovedResponseDto`, `ActivityStudentApprovedResponseDto`, `ActivityStudentCountResponseDto`, `ActivityTeacherSimpleDto`.
- Métodos auxiliares para crear `PaginatedData<ActivityStudentNotApprovedResponseDto>`, `PaginatedData<ActivityStudentApprovedResponseDto>`, `PaginatedData<ActivityTeacherSimpleDto>` y páginas (`PageImpl`) con metadatos coherentes.
- Fixtures de estudiantes, docentes y usuarios asociados con roles apropiados.
- Helpers para generar `Specification<Activity>` con filtros comunes (`notDeleted`, `filterByTeacher`, `nameContains`).
- Tabla de mensajes (`ValidationMessages`, `ConflictExceptionMessages`, `SuccessfulMessages`) referenciada mediante constantes.

### Submódulos
- **Ahorcado**: Builders para `AhorcadoRequestDto`, `AhorcadoResponseDto` con palabras y errores permitidos (`TRES`, `CINCO`).
- **ArbolDeDecision**: Builders para `ArbolDeDecisionActivityRequestDto` con decisiones y consecuencias anidadas.
- **Clasificacion**: Builders para `ClasificacionActivityRequestDto` con conceptos y categorías, validando nombres únicos.
- **CompletarOracion**: Builders para `CompletarOracionActivityRequestDto` con oraciones y palabras faltantes con posiciones.
- **Memorama**: Builders para `MemoramaRequestDto` con parejas de tarjetas (front/back).
- **NoLudica**: Builders para `NoLudicaRequestDto` con descripción opcional.
- **OrdenarSecuencia**: Builders para `OrdenarSecuenciaRequestDto` con eventos y órdenes.
- **Preguntados**: Builders para `PreguntadosRequestDto` con preguntas, opciones y respuestas correctas.

## 8. Secuencia de trabajo recomendada

1. **Fase 1: Módulo Principal Activity - Services principales**
   - Implementar suites de servicios críticos (`ActivityGetService`, `ActivityStartService`, `ActivityCompletedService`, `ActivityNoLudicaCompletedService`) con refinamiento inmediato.
   - Abordar servicios de listado y conteo (`ActivityStudentCountService`, `ActivityListApprovedByStudentService`, `ActivityListNotApprovedByStudentService`).
   - Centralizar fixtures reutilizables en `ActivityTestMother`.

2. **Fase 2: Módulo Principal Activity - Services paginados y balance**
   - Cubrir servicios de listado paginado (`ActivityListByTeacherPaginatedService`, `ActivityApprovedListPaginatedService`, `ActivityNotApprovedListPaginatedService`) con `MockedStatic` para utilidades.
   - Implementar servicios de balance (`ActivityAddBalanceService`, `ActivityRemoveBalanceService`).

3. **Fase 3: Módulo Principal Activity - Controllers**
   - Implementar controllers de consulta y gestión (`ActivityGetController`, `ActivityStartedController`, `ActivityCompletedController`, `ActivityCountByStudentStateController`) con configuración MockMvc compartida.
   - Abordar controllers de listado (`ActivityListByStudentController`, `ActivityListPaginatedByStudentController`, `ActivityListByTeacherPaginatedController`).

4. **Fase 4: Módulo Principal Activity - DTO Validation**
   - Finalizar con pruebas de Bean Validation para DTOs principales (`ActivityCreatedDtoValidationTest`, `ActivityCompletedDtoValidationTest`, `ActivityStudentDtoValidationTest`).

5. **Fase 5: Submódulos (orden sugerido: ahorcado → arbolDeDecision → clasificacion → completarOracion → memorama → noLudica → ordenarSecuencia → preguntados)**
   - Para cada submódulo:
     - Implementar servicio de generación (`*GenerateServiceTest`).
     - Implementar controller (`*GenerateControllerTest`).
     - Implementar validación DTO (`*DtoValidationTest`).
     - Ejecutar refinamiento inmediato tras cada bloque.

6. **Fase 6: Refinamiento y optimización**
   - Aplicar `/refine` tras cada bloque para reducir duplicidad y mejorar expresividad.
   - Centralizar fixtures compartidas entre submódulos donde sea posible.
   - Ajustes detectados por JaCoCo.

## 9. Integración con quality gates
- Ejecutar `./mvnw.cmd org.jacoco:jacoco-maven-plugin:prepare-agent test org.jacoco:jacoco-maven-plugin:report` cuando las suites principales estén listas, garantizando los umbrales propuestos por módulo principal y submódulo.
- Consolidar métricas y hallazgos con `python scripts/testing_report.py --module "activity" --output "docs/testing/test_activity.md"` al completar el ciclo de pruebas.
- Registrar cualquier deuda o hallazgo residual en el reporte para seguimiento del equipo, incluyendo cobertura por submódulo.

## 10. Riesgos residuales y seguimiento

### Módulo Principal Activity
- Confirmar con el equipo si el mecanismo de selección de mapper por reflexión en `ActivityGetService` es el enfoque correcto; preparar suites para reaccionar a cambios en la estrategia de mapeo.
- Mantener alineados los contratos con servicios externos (`IStudentGetByEmailService`, `ITeacherGetByEmailService`, `ISubjectGetByIdService`, `ITransactionGenerateService`); actualizar mocks si cambian firmas o mensajes.
- Vigilar la integración con UploadCare service en `ActivityNoLudicaCompletedService`; incluir mocks robustos y manejo de errores de subida.
- Supervisar el comportamiento de estrategias de completado (`IActivityCompletedStrategyService`) y ajustar pruebas si se añaden nuevos estados o estrategias.

### Submódulos
- Confirmar con el equipo funcional si las validaciones específicas (nombres únicos, opciones correctas, etc.) son suficientes; ajustar pruebas en consecuencia.
- Supervisar dependencias cruzadas con módulos `admin/subject` (asignación de materias), `admin/teacher` (asignación docente-materia) y `economy/transaction` (generación de transacciones) al mockear servicios externos; mantener contratos actualizados.
- Si se añaden nuevos tipos de actividades lúdicas, replicar el patrón de pruebas establecido en los submódulos existentes.
- Validar que la generación de transacciones `ACTIVIDAD` sea consistente en todos los submódulos; ajustar pruebas si cambia la estructura de transacciones.

