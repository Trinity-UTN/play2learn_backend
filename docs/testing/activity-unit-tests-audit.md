# Auditoría de pruebas — Módulo Activity

## Resumen Ejecutivo
- Alcance: controllers, services (incluye commons) y DTOs del módulo `activity` y sus submódulos (`activity`, `activity/ahorcado`, `activity/arbolDeDecision`, `activity/clasificacion`, `activity/completarOracion`, `activity/memorama`, `activity/noLudica`, `activity/ordenarSecuencia`, `activity/preguntados`), excluyendo models, repositories, mappers y specs según pauta.
- Responsabilidades principales: generación de actividades lúdicas y no lúdicas, gestión de inicio/completado de actividades, listados paginados por estudiante/docente, conteo de estados, gestión de balance y exposición de DTOs específicos por tipo de actividad.
- Validaciones clave: Bean Validation sobre fechas, campos requeridos, estados de actividades, validación de estado publicado, intentos restantes, tiempo máximo y resguardo de endpoints mediante `@SessionRequired` con roles específicos.

## Inventario de Controllers (Módulo Principal Activity)

| Controller | Endpoint(s) | Roles requeridos | Service(s) consumidos | Validaciones / Notas |
|------------|-------------|------------------|------------------------|----------------------|
| `ActivityGetController` | `GET /activity/{id}` | `ROLE_STUDENT` | `IActivityGetService` | Path `id`; usa estrategia de mappers dinámicos según tipo de actividad; puede lanzar `IllegalArgumentException` si no encuentra mapper. |
| `ActivityStartedController` | `POST /activity/start` | `ROLE_STUDENT` | `IActivityStartService` | Query param `activityId` validado; `@SessionUser User` para obtener estudiante; valida estado publicado, intentos restantes y actividad ya aprobada. |
| `ActivityCompletedController` | `POST /activity/completed`<br>`POST /activity/completed/no-ludica` | `ROLE_STUDENT` | `IActivityCompletedService`<br>`IActivityNoLudicaCompletedService` | Body `ActivityCompletedRequestDto` validado; endpoint `/no-ludica` consume `MULTIPART_FORM_DATA` con `activityId`, `plainText` y `file` opcionales; valida estado publicado y actividad en curso. |
| `ActivityCountByStudentStateController` | `GET /activity/student/count` | `ROLE_STUDENT` | `IActivityStudentCountService` | `@SessionUser User`; devuelve conteo de actividades por estado (disponibles, aprobadas, desaprobadas, expiradas). |
| `ActivityListByStudentController` | `GET /activity/student/not-approved`<br>`GET /activity/student/approved` | `ROLE_STUDENT` | `IActivityListNotApprovedByStudentService`<br>`IActivityListApproveByStudentService` | `@SessionUser User`; devuelve listas completas (no paginadas) de actividades aprobadas/no aprobadas del estudiante. |
| `ActivityListPaginatedByStudentController` | `GET /activity/student/paginated/not-approved`<br>`GET /activity/student/paginated/approved` | `ROLE_STUDENT` | `IActivityNotApprovedListPaginatedService`<br>`IActivityApprovedListPaginatedService` | Query params: `page`, `page_size`, `order_by`, `order_type`, `search`, `filters`, `filtersValues`; respuesta `PaginatedData<ActivityStudentNotApprovedResponseDto>` o `PaginatedData<ActivityStudentApprovedResponseDto>`. |
| `ActivityListByTeacherPaginatedController` | `GET /activity/teacher/paginated` | `ROLE_TEACHER` | `IActivityListByTeacherPaginatedService` | Query params: `page`, `page_size`, `order_by`, `order_type`, `search`, `filters`, `filtersValues`; respuesta `PaginatedData<ActivityTeacherSimpleDto>`; filtra solo actividades del docente. |

## Inventario de Controllers (Submódulos)

| Submódulo | Controller | Endpoint(s) | Roles requeridos | Service(s) consumidos | Validaciones / Notas |
|-----------|------------|-------------|------------------|------------------------|----------------------|
| `ahorcado` | `GenerateAhorcadoController` | `POST /activities/ahorcado` | `ROLE_ADMIN`, `ROLE_TEACHER` | `IAhorcadoGenerateService` | Body `AhorcadoRequestDto` validado; verifica que docente esté asignado a la materia; genera transacción `ACTIVIDAD`. |
| `arbolDeDecision` | `ArbolDeDecisionGenerateController` | `POST /activities/arbol-decision` | `ROLE_ADMIN`, `ROLE_TEACHER` | `IArbolDecisionGenerateService` | Body `ArbolDeDecisionActivityRequestDto` validado; verifica que docente esté asignado a la materia; genera transacción `ACTIVIDAD`. |
| `clasificacion` | `ClasificacionActivityGenerateController` | `POST /activities/clasificacion` | `ROLE_ADMIN`, `ROLE_TEACHER` | `IClasificacionGenerateService` | Body `ClasificacionActivityRequestDto` validado; verifica que docente esté asignado a la materia; genera transacción `ACTIVIDAD`. |
| `completarOracion` | `CompletarOracionGenerateController` | `POST /activities/completar-oracion` | `ROLE_ADMIN`, `ROLE_TEACHER` | `ICompletarOracionGenerateService` | Body `CompletarOracionActivityRequestDto` validado; verifica que docente esté asignado a la materia; genera transacción `ACTIVIDAD`. |
| `memorama` | `MemoramaRegisterController` | `POST /activities/memorama` | `ROLE_ADMIN`, `ROLE_TEACHER` | `IMemoramaGenerateService` | Body `MemoramaRequestDto` validado; verifica que docente esté asignado a la materia; genera transacción `ACTIVIDAD`. |
| `noLudica` | `NoLudicaGenerateController` | `POST /activities/no-ludica` | `ROLE_ADMIN`, `ROLE_TEACHER` | `INoLudicaGenerateService` | Body `NoLudicaRequestDto` validado; verifica que docente esté asignado a la materia; genera transacción `ACTIVIDAD`. |
| `ordenarSecuencia` | `OrdenarSecuenciaGenerateController` | `POST /activities/ordenar-secuencia` | `ROLE_ADMIN`, `ROLE_TEACHER` | `IOrdenarSecuenciaActivityGenerateService` | Body `OrdenarSecuenciaRequestDto` validado; verifica que docente esté asignado a la materia; genera transacción `ACTIVIDAD`. |
| `preguntados` | `PreguntadosGenerateController` | `POST /activities/preguntados` | `ROLE_ADMIN`, `ROLE_TEACHER` | `IPreguntadosGenerateService` | Body `PreguntadosRequestDto` validado; verifica que docente esté asignado a la materia; genera transacción `ACTIVIDAD`. |

## Inventario de Services (Módulo Principal Activity)

| Service | Método(s) clave | Dependencias principales | Validaciones / Excepciones relevantes |
|---------|-----------------|--------------------------|---------------------------------------|
| `ActivityGetService` | `cu64GetActivity` | `Map<String, IActivityMapper>`, `IActivityGetByIdService` | Obtiene actividad por ID; determina tipo mediante reflexión y selecciona mapper dinámico; lanza `IllegalArgumentException` si no encuentra mapper. |
| `ActivityStartService` | `execute` | `IStudentGetByEmailService`, `IActivityGetByIdService`, `IActivityValidatePublishedStatusService`, `IActivityGetCompletedStateService`, `IActivityGetRemainingAttemptsService`, `IActivityCompletedGetLastStartedService`, `IActivityCompletedRepository`, `IActivityCompletedService` | Valida estado publicado; verifica que actividad no esté aprobada; valida intentos restantes; finaliza intento previo en curso como desaprobado si existe; lanza `ConflictException` si ya está aprobada o sin intentos. |
| `ActivityCompletedService` | `cu61ActivityCompleted` | `IActivityGetByIdService`, `Map<String, IActivityCompletedStrategyService>`, `IStudentGetByEmailService`, `IActivityValidatePublishedStatusService`, `IActivityGetCompletedStateService`, `IActivityCompletedGetLastStartedService` | Valida estado publicado; verifica que actividad no esté aprobada; valida que exista intento en curso; calcula tiempo transcurrido y desaprueba automáticamente si excede `maxTime`; delega a estrategia según estado. |
| `ActivityNoLudicaCompletedService` | `cu72ActivityNoLudicaCompleted` | `IActivityGetByIdService`, `IStudentGetByEmailService`, `IActivityValidatePublishedStatusService`, `IActivityGetCompletedStateService`, `IActivityCompletedGetLastStartedService`, `INoLudicaCreateAttemptService`, `IActivityCompletedRepository`, `INoLudicaValidationsService`, `IActivityCompletedService` | Valida contenido (`plainText` o `file`); valida estado publicado; verifica que no esté aprobada ni pendiente; valida que exista intento en curso; valida tiempo máximo; cambia estado a `PENDING` y crea `NoLudicaAttempt`. |
| `ActivityStudentCountService` | `cu88CountActivitiesPerState` | `IStudentGetByEmailService`, `IActivityGetByStudentService`, `IActivityFilterApprovedService`, `IActivityFilterByDisapprovedService`, `IActivityGetStatusService` | Obtiene actividades del estudiante; filtra por aprobadas y desaprobadas; clasifica restantes por estado (`PUBLISHED`, `EXPIRED`); retorna conteo por estado. |
| `ActivityListApprovedByStudentService` | `cu63ListApprovedActivitiesByStudent` | `IStudentGetByEmailService`, `IActivityGetByStudentService`, `IActivityCreateApprovedDtosService` | Obtiene actividades del estudiante; crea DTOs aprobadas. |
| `ActivityListNotApprovedByStudentService` | `cu62ListNotApprovedActivitiesByStudent` | `IStudentGetByEmailService`, `IActivityGetByStudentService`, `IActivityCreateNotApprovedDtosService`, `IActivityFilterNotApprovedService` | Obtiene actividades del estudiante; filtra no aprobadas; crea DTOs. |
| `ActivityListByTeacherPaginatedService` | `cu111ListActivityByTeacherPaginated` | `ITeacherGetByEmailService`, `IActivityPaginatedRepository`, `IActivityCreateTeacherSimpleDtosService`, `ActivitySpecs`, `PaginatorUtils`, `PaginationHelper` | Obtiene docente por email; construye `Pageable` y `Specification`; filtra por docente y `notDeleted`; aplica búsqueda y filtros dinámicos; retorna `PaginatedData<ActivityTeacherSimpleDto>`. |
| `ActivityApprovedListPaginatedService` | `cu69ListApprovedActivitiesPaginated` | `IStudentGetByEmailService`, `IActivityGetByStudentService`, `IActivityFilterApprovedService`, `IActivityPaginatedRepository`, `IActivityCreateApprovedDtosService`, `ActivitySpecs`, `PaginatorUtils`, `PaginationHelper` | Obtiene actividades del estudiante; filtra aprobadas; obtiene IDs; construye `Specification` con filtros; retorna página vacía si no hay actividades; retorna `PaginatedData<ActivityStudentApprovedResponseDto>`. |
| `ActivityNotApprovedListPaginatedService` | `cu66listNotApprovedActivitiesPaginated` | `IActivityPaginatedRepository`, `IActivityCreateNotApprovedDtosService`, `IStudentGetByEmailService`, `IActivityGetByStudentService`, `IActivityFilterNotApprovedService`, `IActivityFilterByDisapprovedService`, `ActivitySpecs`, `PaginatorUtils`, `PaginationHelper` | Obtiene actividades del estudiante; filtra no aprobadas; maneja filtro especial `disapproved`; construye `Specification` con filtros; retorna página vacía si no hay actividades; retorna `PaginatedData<ActivityStudentNotApprovedResponseDto>`. |
| `ActivityAddBalanceService` | `execute` | `IActivityRepository` | Incrementa `actualBalance` de actividad; persiste cambios. |
| `ActivityRemoveBalanceService` | `execute` | `IActivityRepository` | Decrementa `actualBalance` de actividad; persiste cambios. |

## Servicios Commons (Módulo Principal Activity)

| Service | Responsabilidad | Dependencias | Notas de validación |
|---------|-----------------|--------------|---------------------|
| `ActivityGetByIdService` | Buscar actividad por ID | `IActivityRepository` | Método `findById` lanza `NotFoundException` con mensaje centralizado si no encuentra actividad. |
| `ActivityGetByStudentService` | Obtener actividades por estudiante | `ISubjectGetByStudentService`, `IActivityRepository` | Obtiene materias del estudiante; busca actividades por materias excluyendo eliminadas (`deletedAtIsNull`). |
| `ActivityValidatePublishedStatusService` | Validar estado publicado de actividad | - | Verifica que fecha actual esté entre `startDate` y `endDate`; lanza `ConflictException` si no está publicada. |
| `ActivityGetCompletedStateService` | Obtener estado de completado | `IActivityCompletedRepository` | Consulta última realización del estudiante; retorna estado o `null` si no existe. |
| `ActivityGetRemainingAttemptsService` | Calcular intentos restantes | `IActivityCompletedRepository` | Calcula intentos usados vs `attempts` de actividad; retorna intentos restantes. |
| `ActivityCompletedGetLastStartedService` | Obtener último intento iniciado | `IActivityCompletedRepository` | Busca último `ActivityCompleted` con estado `IN_PROGRESS`; retorna `Optional`. |
| `ActivityGetStatusService` | Determinar estado de actividad | - | Compara fechas con fecha actual; retorna `CREATED`, `PUBLISHED` o `EXPIRED`. |
| `ActivityFilterApprovedService` | Filtrar actividades aprobadas | `IActivityCompletedRepository` | Filtra actividades con última realización en estado `APPROVED`. |
| `ActivityFilterNotApprovedService` | Filtrar actividades no aprobadas | `IActivityCompletedRepository` | Filtra actividades sin realización o con estado distinto de `APPROVED`. |
| `ActivityFilterByDisapprovedService` | Filtrar actividades desaprobadas | `IActivityCompletedRepository` | Filtra actividades con última realización en estado `DISAPPROVED`; puede incluir solo últimas o todas. |
| `ActivityCreateApprovedDtosService` | Crear DTOs aprobadas | `IActivityCompletedRepository` | Mapea actividades aprobadas a `ActivityStudentApprovedResponseDto` con información de realización. |
| `ActivityCreateNotApprovedDtosService` | Crear DTOs no aprobadas | `IActivityCompletedRepository` | Mapea actividades no aprobadas a `ActivityStudentNotApprovedResponseDto` con intentos restantes y estado. |
| `ActivityCreateTeacherSimpleDtosService` | Crear DTOs simples para docente | `IActivityGetStatusService` | Mapea actividades a `ActivityTeacherSimpleDto` con estado calculado. |
| `NoLudicaCreateAttemptService` | Crear intento no lúdico | UploadCare service | Sube archivo a UploadCare si existe; crea `NoLudicaAttempt` con `plainText` y URL de archivo. |
| `NoLudicaValidationsService` | Validar contenido no lúdico | - | Valida que exista `plainText` o `file`; lanza `ConflictException` si ambos están vacíos. |

## Inventario de Services (Submódulos)

| Submódulo | Service | Método(s) clave | Dependencias principales | Validaciones / Excepciones relevantes |
|-----------|---------|-----------------|--------------------------|---------------------------------------|
| `ahorcado` | `AhorcadoGenerateService` | `cu39GenerateAhorcado` | `IAhorcadoRepository`, `ISubjectGetByIdService`, `ITransactionGenerateService`, `ITeacherGetByEmailService` | Verifica que docente esté asignado a materia; lanza `ConflictException` si no; genera transacción `ACTIVIDAD`; persiste actividad. |
| `arbolDeDecision` | `ArbolDecisionGenerateService` | `cu46GenerateArbolDeDecisionActivity` | `IArbolDeDecisionRepository`, `ISubjectGetByIdService`, `ITransactionGenerateService`, `ITeacherGetByEmailService` | Verifica que docente esté asignado a materia; lanza `ConflictException` si no; genera transacción `ACTIVIDAD`; persiste actividad con decisiones y consecuencias. |
| `clasificacion` | `ClasificacionActivityGenerateService` | `cu43GenerateClasificacionActivity` | `IClasificacionActivityRepository`, `ISubjectGetByIdService`, `ITransactionGenerateService`, `ITeacherGetByEmailService`, `IClasificacionValidateCategoriesNamesService`, `IClasificacionValidateConceptsNamesService` | Verifica que docente esté asignado a materia; valida nombres únicos de categorías y conceptos; genera transacción `ACTIVIDAD`; persiste actividad con conceptos y categorías. |
| `completarOracion` | `CompletarOracionGenerateService` | `cu44GenerateCompletarOracionActivity` | `ICompletarOracionRepository`, `ISubjectGetByIdService`, `ITransactionGenerateService`, `ITeacherGetByEmailService`, `ICompletarOracionValidateWordMissingService`, `ICompletarOracionValidateWordsOrderService` | Verifica que docente esté asignado a materia; valida palabras faltantes y orden; genera transacción `ACTIVIDAD`; persiste actividad con oraciones y palabras. |
| `memorama` | `MemoramaGenerateService` | `cu41GenerateMemorama` | `IMemoramaRepository`, `ISubjectGetByIdService`, `ITransactionGenerateService`, `ITeacherGetByEmailService`, `ICouplesMemoramaGenerateService` | Verifica que docente esté asignado a materia; genera parejas de tarjetas; genera transacción `ACTIVIDAD`; persiste actividad con parejas. |
| `noLudica` | `NoLudicaGenerateService` | `cu45GenerateNoLudicaActivity` | `INoLudicaRepository`, `ISubjectGetByIdService`, `ITransactionGenerateService`, `ITeacherGetByEmailService` | Verifica que docente esté asignado a materia; genera transacción `ACTIVIDAD`; persiste actividad no lúdica. |
| `ordenarSecuencia` | `OrdenarSecuenciaActivityGenerateService` | `cu42GenerateOrdenarSecuenciaActivity` | `IOrdenarSecuenciaRepository`, `ISubjectGetByIdService`, `ITransactionGenerateService`, `ITeacherGetByEmailService`, `IEventsGenerateService` | Verifica que docente esté asignado a materia; genera eventos en orden aleatorio; genera transacción `ACTIVIDAD`; persiste actividad con eventos. |
| `preguntados` | `PreguntadosGenerateService` | `cu40GeneratePreguntados` | `IPreguntadosRepository`, `ISubjectGetByIdService`, `ITransactionGenerateService`, `ITeacherGetByEmailService`, `IPreguntadosValidateCorrectOptionService` | Verifica que docente esté asignado a materia; valida que cada pregunta tenga opción correcta; genera transacción `ACTIVIDAD`; persiste actividad con preguntas y opciones. |

## DTOs y Validaciones (Módulo Principal Activity)

| DTO | Campos relevantes | Restricciones |
|-----|-------------------|---------------|
| `ActivityRequestDto` (abstracto) | `description`, `startDate`, `endDate`, `difficulty`, `maxTime`, `subjectId`, `attempts`, `initialBalance`, `typeReward` | `@Size(max=1000)` para descripción; `@NotNull` para `startDate`, `endDate`, `difficulty`, `subjectId`; mensajes de `ValidationMessages`. |
| `ActivityCompletedRequestDto` | `activityId`, `state` | `@NotNull` para ambos campos; `state` debe ser `APPROVED`, `DISAPPROVED` o `PENDING`; mensajes de `ValidationMessages`. |
| `ActivityStudentNotApprovedResponseDto` | `id`, `name`, `description`, `startDate`, `endDate`, `difficulty`, `maxTime`, `subjectId`, `subjectName`, `attempts`, `remainingAttempts`, `status`, `minReward`, `maxReward`, `pending` | Sin Bean Validation; usado como salida; incluye estado calculado y recompensas estimadas. |
| `ActivityStudentApprovedResponseDto` | `id`, `name`, `description`, `difficulty`, `subjectId`, `subjectName`, `attempts`, `remainingAttempts`, `completedAt`, `reward`, `state` | Sin Bean Validation; usado como salida; incluye información de realización. |
| `ActivityStudentCountResponseDto` | `available`, `approved`, `disapproved`, `expired` | Sin Bean Validation; usado como salida; conteos por estado. |
| `ActivityTeacherSimpleDto` | `id`, `name`, `description`, `subjectId`, `subjectName`, `courseId`, `course`, `yearId`, `status`, `date` | Sin Bean Validation; usado como salida; incluye información de materia y curso. |
| `ActivityResponseDto` | Depende del tipo de actividad (polimórfico) | Sin Bean Validation; mapeado dinámicamente según tipo de actividad mediante estrategia de mappers. |

## DTOs y Validaciones (Submódulos)

| Submódulo | DTO Request | Campos relevantes | Restricciones |
|-----------|-------------|-------------------|---------------|
| `ahorcado` | `AhorcadoRequestDto` | Hereda `ActivityRequestDto` + `word`, `errorsPermited` | `@NotEmpty`, `@Size(max=50)`, `@Pattern` para `word` (solo letras y espacios); `@NotNull` para `errorsPermited` (enum `TRES` o `CINCO`). |
| `arbolDeDecision` | `ArbolDeDecisionActivityRequestDto` | Hereda `ActivityRequestDto` + `decisions` (lista de `DecisionArbolDecisionRequestDto`) | `decisions` contiene `decision` (String), `consecuences` (lista de `ConsecuenceArbolDecisionRequestDto`); validaciones de campos anidados. |
| `clasificacion` | `ClasificacionActivityRequestDto` | Hereda `ActivityRequestDto` + `concepts` (lista de `ConceptClasificacionRequestDto`), `categories` (lista de `CategoryClasificacionRequestDto`) | Validaciones de nombres únicos en conceptos y categorías; relaciones concepto-categoría. |
| `completarOracion` | `CompletarOracionActivityRequestDto` | Hereda `ActivityRequestDto` + `sentences` (lista de `SentenceCompletarOracionRequestDto`) | `sentences` contiene `sentence` (String), `words` (lista de `WordCompletarOracionRequestDto` con posición); validación de palabras faltantes y orden. |
| `memorama` | `MemoramaRequestDto` | Hereda `ActivityRequestDto` + `couples` (lista de `CouplesMemoramaRequestDto`) | `couples` contiene `front` y `back` (Strings); validación de parejas únicas. |
| `noLudica` | `NoLudicaRequestDto` | Hereda `ActivityRequestDto` + `description` (opcional) | Sin campos adicionales específicos; descripción opcional. |
| `ordenarSecuencia` | `OrdenarSecuenciaRequestDto` | Hereda `ActivityRequestDto` + `events` (lista de `EventRequestDto`) | `events` contiene `event` (String) y `order` (Integer); validación de orden único. |
| `preguntados` | `PreguntadosRequestDto` | Hereda `ActivityRequestDto` + `questions` (lista de `QuestionRequestDto`) | `questions` contiene `question` (String), `options` (lista de `OptionRequestDto`); validación de opción correcta por pregunta. |

## Mapa de Dependencias Clave
- **Servicios externos**: `IStudentGetByEmailService` (módulo `admin/student`), `ITeacherGetByEmailService` (módulo `admin/teacher`), `ISubjectGetByIdService` (módulo `admin/subject`), `ISubjectGetByStudentService` (módulo `admin/subject`), `ITransactionGenerateService` (módulo `economy/transaction`), UploadCare service (subida de archivos).
- **Infraestructura compartida**: `ResponseFactory`, `SuccessfulMessages`, `PaginatorUtils`, `PaginationHelper`, `ActivitySpecs`, anotaciones `@SessionRequired`, `@SessionUser`.
- **Excepciones centralizadas**: `NotFoundException`, `ConflictException` con mensajes personalizados; `IllegalArgumentException` para mappers no encontrados.
- **Strategy Pattern**: `IActivityMapper` (mapeo por tipo de actividad), `IActivityCompletedStrategyService` (estrategias de completado según estado).

## Riesgos y Consideraciones para Pruebas

### Módulo Principal Activity
- Verificar que `ActivityGetService` maneje correctamente todos los tipos de actividad y que el mecanismo de selección de mapper por reflexión no falle ante nuevos tipos; pruebas deben cubrir caso de mapper no encontrado.
- Confirmar que `ActivityStartService` maneje correctamente el escenario de finalización de intento previo en curso (desaprobado automático) y que el cálculo de intentos restantes después de finalizar sea correcto.
- Asegurar que `ActivityCompletedService` calcule correctamente el tiempo transcurrido en minutos y que la desaprobación automática por tiempo excedido funcione correctamente; validar estrategias de completado para cada estado.
- Probar que `ActivityNoLudicaCompletedService` valide correctamente la existencia de `plainText` o `file` y que el servicio de UploadCare se integre correctamente; validar manejo de errores de subida.
- Validar que servicios de listado paginado (`ActivityApprovedListPaginatedService`, `ActivityNotApprovedListPaginatedService`) manejen correctamente el caso de listas vacías y que retornen `Page.empty` cuando corresponda.
- Verificar que el filtro especial `disapproved` en `ActivityNotApprovedListPaginatedService` funcione correctamente y que no interfiera con otros filtros dinámicos.
- Confirmar que `ActivityAddBalanceService` y `ActivityRemoveBalanceService` no permitan balances negativos; validar manejo de montos nulos o inválidos.
- Probar que `ActivityStudentCountService` clasifique correctamente actividades por estado (`PUBLISHED`, `EXPIRED`) y que no duplique conteos.

### Submódulos
- Validar que todos los servicios de generación (`*GenerateService`) verifiquen correctamente la asignación del docente a la materia y lancen `ConflictException` apropiadamente.
- Confirmar que servicios de validación específicos (`IClasificacionValidateCategoriesNamesService`, `ICompletarOracionValidateWordMissingService`, etc.) cubran casos edge (nombres duplicados, palabras faltantes inválidas, órdenes incorrectos).
- Probar que la generación de transacciones `ACTIVIDAD` se ejecute correctamente en todos los submódulos y que los parámetros sean consistentes.
- Validar que DTOs anidados (preguntas/opciones, decisiones/consecuencias, etc.) se validen correctamente mediante Bean Validation y que mensajes de error sean claros.
- Asegurar que servicios de generación manejen correctamente listas vacías o nulas en campos opcionales y que no fallen ante datos incompletos.

