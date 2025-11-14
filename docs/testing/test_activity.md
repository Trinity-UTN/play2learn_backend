# Reporte de pruebas — módulo activity

## Cobertura de código (JaCoCo)

### Proyecto completo
- Instrucciones: 19.76 %
- Ramas: 10.98 %
- Líneas: 21.31 %
- Complejidad: 12.32 %
- Métodos: 14.77 %

### Módulo `activity`
- Instrucciones: 60.42 %
- Ramas: 33.45 %
- Líneas: 61.89 %
- Complejidad: 38.44 %
- Métodos: 48.0 %

## Escenarios (services/controllers/dtos)
Feature: POST /activity/completed
  Scenario: POST /activity/completed
  Scenario: Given valid request When completing activity Then returns ActivityCompleted with 201 Created
  Scenario: Given activity already approved When completing activity Then returns 409 Conflict
  Scenario: POST /activity/completed/no-ludica
  Scenario: Given valid plainText and file When completing no-ludica activity Then returns ActivityCompleted with 201 Created

Feature: GET /activity/student/count
  Scenario: GET /activity/student/count
  Scenario: Given student with activities When counting by state Then returns counts with 200 OK
  Scenario: Given student with no activities When counting by state Then returns zero counts with 200 OK

Feature: GET /activity/{id}
  Scenario: GET /activity/{id}
  Scenario: Given existing activity When getting by id Then returns activity with 200 OK
  Scenario: Given non-existent activity When getting by id Then returns 404

Feature: GET /activity/student/not-approved
  Scenario: GET /activity/student/not-approved
  Scenario: Given student with not approved activities When listing not approved Then returns list with 200 OK
  Scenario: Given student with no not approved activities When listing not approved Then returns empty list with 200 OK
  Scenario: GET /activity/student/approved
  Scenario: Given student with approved activities When listing approved Then returns list with 200 OK
  Scenario: Given student with no approved activities When listing approved Then returns empty list with 200 OK

Feature: GET /activity/teacher/paginated
  Scenario: GET /activity/teacher/paginated
  Scenario: Given teacher with activities When listing paginated Then returns paginated data with 200 OK
  Scenario: Given teacher with no activities When listing paginated Then returns empty paginated data with 200 OK

Feature: GET /activity/student/paginated/not-approved
  Scenario: GET /activity/student/paginated/not-approved
  Scenario: Given student with not approved activities When listing paginated Then returns paginated data with 200 OK
  Scenario: GET /activity/student/paginated/approved
  Scenario: Given student with approved activities When listing paginated Then returns paginated data with 200 OK

Feature: POST /activity/start
  Scenario: POST /activity/start
  Scenario: Given valid activity and user When starting activity Then returns ActivityCompleted with 201 Created
  Scenario: Given activity already approved When starting activity Then returns 409 Conflict

Feature: ActivityCompletedRequestDto validation
  Scenario: ActivityCompletedRequestDto validation
  Scenario: Given valid request with approved state When validating Then no violations are returned
  Scenario: Given valid request with disapproved state When validating Then no violations are returned
  Scenario: Given valid request with pending state When validating Then no violations are returned
  Scenario: Given activityId is null (required field) When validating Then reports NOT_NULL_ACTIVITY_ID
  Scenario: Given state is null (required field) When validating Then reports NOT_NULL_STATE
  Scenario: Given both required fields are null When validating Then reports violations for both fields

Feature: ActivityRequestDto validation (via AhorcadoRequestDto)
  Scenario: ActivityRequestDto validation (via AhorcadoRequestDto)
  Scenario: Given valid request with all required fields When validating Then no violations are returned
  Scenario: Given valid request with description at max length (1000 chars) When validating Then no violations are returned
  Scenario: Given description exceeds max length (1001 chars) When validating Then reports MAX_LENGTH_DESCRIPTION_1000
  Scenario: Given description is null (optional field) When validating Then no violations are returned
  Scenario: Given startDate is null (required field) When validating Then reports NOT_NULL_START_DATE
  Scenario: Given endDate is null (required field) When validating Then reports NOT_NULL_END_DATE
  Scenario: Given difficulty is null (required field) When validating Then reports NOT_NULL_DIFFICULTY
  Scenario: Given subjectId is null (required field) When validating Then reports NOT_NULL_SUBJECT
  Scenario: Given all optional fields are null When validating Then no violations are returned

Feature: execute
  Scenario: execute
  Scenario: Given activity with balance When adding amount Then increments balance and persists
  Scenario: Given activity with zero balance When adding amount Then sets balance to amount

Feature: cu69ListApprovedActivitiesPaginated
  Scenario: cu69ListApprovedActivitiesPaginated
  Scenario: Given student with approved activities When listing paginated Then returns paginated data with approved activities
  Scenario: Given student with no approved activities When listing paginated Then returns empty page

Feature: cu61ActivityCompleted
  Scenario: cu61ActivityCompleted
  Scenario: Given valid request with APPROVED state When completing activity Then delegates to approved strategy
  Scenario: Given activity already approved When completing activity Then throws ConflictException
  Scenario: Given no activity in progress When completing activity Then throws ConflictException
  Scenario: Given time exceeded maxTime When completing activity Then automatically sets state to DISAPPROVED

Feature: cu64GetActivity
  Scenario: cu64GetActivity
  Scenario: Given existing Ahorcado activity When retrieving by id Then returns mapped response
  Scenario: Given activity type without mapper When retrieving by id Then throws IllegalArgumentException

Feature: cu63ListApprovedActivitiesByStudent
  Scenario: cu63ListApprovedActivitiesByStudent
  Scenario: Given student with approved activities When listing approved activities Then returns approved DTOs
  Scenario: Given student with no activities When listing approved activities Then returns empty list

Feature: cu111ListActivityByTeacherPaginated
  Scenario: cu111ListActivityByTeacherPaginated
  Scenario: Given teacher with activities When listing paginated Then returns paginated data with teacher's activities
  Scenario: Given teacher with no activities When listing paginated Then returns empty paginated data

Feature: cu62ListNotApprovedActivitiesByStudent
  Scenario: cu62ListNotApprovedActivitiesByStudent
  Scenario: Given student with not approved activities When listing not approved activities Then returns not approved DTOs
  Scenario: Given student with no not approved activities When listing not approved activities Then returns empty list

Feature: cu72ActivityNoLudicaCompleted
  Scenario: cu72ActivityNoLudicaCompleted
  Scenario: Given valid plainText When completing noLudica activity Then sets state to PENDING and creates attempt
  Scenario: Given null plainText When completing noLudica activity Then sets to empty string
  Scenario: Given activity already approved When completing noLudica activity Then throws ConflictException
  Scenario: Given activity pending review When completing noLudica activity Then throws ConflictException
  Scenario: Given no activity in progress When completing noLudica activity Then throws ConflictException

Feature: cu66listNotApprovedActivitiesPaginated
  Scenario: cu66listNotApprovedActivitiesPaginated
  Scenario: Given student with not approved activities When listing paginated Then returns paginated data with not approved activities
  Scenario: Given student with no not approved activities When listing paginated Then returns empty page
  Scenario: Given filter disapproved When listing paginated Then applies disapproved filter

Feature: execute
  Scenario: execute
  Scenario: Given activity with balance When removing amount Then decrements balance and persists
  Scenario: Given activity with balance When removing full amount Then sets balance to zero

Feature: execute
  Scenario: execute
  Scenario: Given valid activity and student When starting activity Then creates ActivityCompleted with IN_PROGRESS state
  Scenario: Given activity already approved When starting activity Then throws ConflictException
  Scenario: Given no remaining attempts When starting activity Then throws ConflictException
  Scenario: Given last started activity exists When starting new activity Then finalizes previous as DISAPPROVED
  Scenario: Given last started exists and no attempts remain after disapproval When starting activity Then throws ConflictException

Feature: cu88CountActivitiesPerState
  Scenario: cu88CountActivitiesPerState
  Scenario: Given student with activities in different states When counting Then returns correct counts
  Scenario: Given student with no activities When counting Then returns zero counts

Feature: POST /activities/ahorcado
  Scenario: POST /activities/ahorcado
  Scenario: Given valid request When generating ahorcado Then returns AhorcadoResponseDto with 201 Created
  Scenario: Given teacher not assigned to subject When generating ahorcado Then returns 409 Conflict

Feature: AhorcadoRequestDto validation
  Scenario: AhorcadoRequestDto validation
  Scenario: Given valid request with default word and TRES errors When validating Then no violations are returned
  Scenario: Given word is empty (edge case) When validating Then reports NOT_EMPTY_WORD
  Scenario: Given word is null (edge case) When validating Then reports NOT_EMPTY_WORD
  Scenario: Given word exceeds max length (51 chars - edge case) When validating Then reports MAX_LENGTH_WORD
  Scenario: Given word at max length (50 chars - boundary) When validating Then no violations are returned
  Scenario: Given word contains invalid characters (numbers - edge case) When validating Then reports PATTERN_WORD
  Scenario: Given word contains invalid characters (special chars - edge case) When validating Then reports PATTERN_WORD
  Scenario: Given word contains valid Spanish characters (ñ, áéíóú) When validating Then no violations are returned
  Scenario: Given errorsPermited is null (required field) When validating Then reports NOT_NULL_ERRORS_PERMITED
  Scenario: Given errorsPermited is TRES When validating Then no violations are returned
  Scenario: Given errorsPermited is CINCO When validating Then no violations are returned

Feature: cu39GenerateAhorcado
  Scenario: cu39GenerateAhorcado
  Scenario: Given valid request with teacher assigned to subject When generating ahorcado Then returns AhorcadoResponseDto
  Scenario: Given subject not found When generating ahorcado Then throws NotFoundException
  Scenario: Given teacher not assigned to subject When generating ahorcado Then throws ConflictException
  Scenario: Given valid request When generating ahorcado Then saves ahorcado with correct fields

Feature: POST /activities/arbol-decision
  Scenario: POST /activities/arbol-decision
  Scenario: Given valid request with 2 decisions When generating arbol de decision Then returns ArbolDeDecisionActivityResponseDto with 201 Created
  Scenario: Given teacher not assigned to subject When generating arbol de decision Then returns 409 Conflict

Feature: ArbolDeDecisionActivityRequestDto validation
  Scenario: ArbolDeDecisionActivityRequestDto validation
  Scenario: Given valid request with introduction and 2 decisions When validating Then no violations are returned
  Scenario: Given introduction is blank (edge case) When validating Then reports NOT_BLANK
  Scenario: Given introduction is null (edge case) When validating Then reports NOT_BLANK
  Scenario: Given introduction exceeds max length (501 chars - edge case) When validating Then reports INTRODUCTION_500_MAX_LENGHT
  Scenario: Given introduction at max length (500 chars - boundary) When validating Then no violations are returned
  Scenario: Given decisionTree is null (required field - edge case) When validating Then reports NOT_NULL_DECISION_TREE
  Scenario: Given decisionTree has 1 decision (should be 2 - edge case) When validating Then reports OPTIONS_SIZE
  Scenario: Given decisionTree has 3 decisions (should be 2 - edge case) When validating Then reports OPTIONS_SIZE
  Scenario: DecisionArbolDecisionRequestDto validation
  Scenario: Given valid decision with consequence When validating Then no violations are returned
  Scenario: Given valid decision with 2 options When validating Then no violations are returned
  Scenario: Given name is blank (required field) When validating Then reports NOT_EMPTY_NAME
  Scenario: Given name exceeds max length (201 chars - edge case) When validating Then reports NAME_200_MAX_LENGHT
  Scenario: Given context exceeds max length (501 chars - edge case) When validating Then reports CONTEXT_500_MAX_LENGHT
  Scenario: Given decision has 1 option (should be 0 or 2 - edge case) When validating Then reports OPTIONS_SIZE
  Scenario: Given decision has 3 options (should be 0 or 2 - edge case) When validating Then reports OPTIONS_SIZE
  Scenario: Given decision has no options and no consequence (edge case - árbol vacío) When validating Then reports OPTIONS_AND_CONSECUENCE_NULL
  Scenario: ConsecuenceArbolDecisionRequestDto validation
  Scenario: Given valid consequence When validating Then no violations are returned
  Scenario: Given name is blank (required field) When validating Then reports NOT_EMPTY_NAME
  Scenario: Given name exceeds max length (201 chars - edge case) When validating Then reports NAME_200_MAX_LENGHT

Feature: cu46GenerateArbolDeDecisionActivity
  Scenario: cu46GenerateArbolDeDecisionActivity
  Scenario: Given valid request with teacher assigned to subject When generating arbol de decision Then returns ArbolDeDecisionActivityResponseDto
  Scenario: Given subject not found When generating arbol de decision Then throws NotFoundException
  Scenario: Given teacher not assigned to subject When generating arbol de decision Then throws ConflictException
  Scenario: Given valid request When generating arbol de decision Then saves activity with correct fields

Feature: POST /activities/clasificacion
  Scenario: POST /activities/clasificacion
  Scenario: Given valid request with 2 categories and concepts When generating clasificacion Then returns ClasificacionActivityResponseDto with 201 Created
  Scenario: Given teacher not assigned to subject When generating clasificacion Then returns 409 Conflict
  Scenario: Given duplicate category names When generating clasificacion Then returns 400 BadRequest
  Scenario: Given duplicate concept names When generating clasificacion Then returns 400 BadRequest

Feature: ClasificacionActivityRequestDto validation
  Scenario: ClasificacionActivityRequestDto validation
  Scenario: Given valid request with 2 categories and concepts When validating Then no violations are returned
  Scenario: Given categories is null (required field - edge case) When validating Then reports NOT_NULL_CATEGORIES
  Scenario: Given categories has 1 category (should be 2-10 - edge case) When validating Then reports LENGTH_CATEGORIES
  Scenario: Given categories has 11 categories (should be 2-10 - edge case) When validating Then reports LENGTH_CATEGORIES
  Scenario: Given categories at max length (10 categories - boundary) When validating Then no violations are returned
  Scenario: CategoryClasificacionRequestDto validation
  Scenario: Given valid category with concepts When validating Then no violations are returned
  Scenario: Given name is blank (required field) When validating Then reports NOT_EMPTY_NAME
  Scenario: Given name exceeds max length (51 chars - edge case) When validating Then reports MAX_LENGTH_NAME_50
  Scenario: Given concepts is null (required field) When validating Then reports NOT_NULL_CONCEPTS
  Scenario: Given concepts is empty (should be 1-10 - edge case - categoría sin conceptos) When validating Then reports LENGTH_CONCEPTS
  Scenario: Given concepts has 11 concepts (should be 1-10 - edge case) When validating Then reports LENGTH_CONCEPTS
  Scenario: Given concepts at max length (10 concepts - boundary) When validating Then no violations are returned
  Scenario: ConceptClasificacionRequestDto validation
  Scenario: Given valid concept When validating Then no violations are returned
  Scenario: Given name is blank (required field) When validating Then reports NOT_EMPTY_NAME
  Scenario: Given name exceeds max length (101 chars - edge case) When validating Then reports MAX_LENGTH_NAME_100
  Scenario: Given name at max length (100 chars - boundary) When validating Then no violations are returned

Feature: cu43GenerateClasificacionActivity
  Scenario: cu43GenerateClasificacionActivity
  Scenario: Given valid request with teacher assigned to subject When generating clasificacion Then returns ClasificacionActivityResponseDto
  Scenario: Given subject not found When generating clasificacion Then throws NotFoundException
  Scenario: Given teacher not assigned to subject When generating clasificacion Then throws ConflictException
  Scenario: Given duplicate category names When generating clasificacion Then throws BadRequestException
  Scenario: Given duplicate concept names When generating clasificacion Then throws BadRequestException
  Scenario: Given valid request When generating clasificacion Then saves activity with correct fields

Feature: POST /activities/completar-oracion
  Scenario: POST /activities/completar-oracion
  Scenario: Given valid request with sentences and words When generating completar oracion Then returns CompletarOracionActivityResponseDto with 201 Created
  Scenario: Given teacher not assigned to subject When generating completar oracion Then returns 409 Conflict
  Scenario: Given invalid words order When generating completar oracion Then returns 400 BadRequest
  Scenario: Given no words missing (oración sin espacios en blanco - edge case) When generating completar oracion Then returns 400 BadRequest

Feature: CompletarOracionActivityRequestDto validation
  Scenario: CompletarOracionActivityRequestDto validation
  Scenario: Given valid request with sentences and words When validating Then no violations are returned
  Scenario: Given sentences is null (required field - edge case) When validating Then reports NOT_NULL_SENTENCES
  Scenario: Given sentences is empty (should be 1-20 - edge case - oraciones vacías) When validating Then reports LENGTH_SENTENCES
  Scenario: Given sentences has 21 sentences (should be 1-20 - edge case) When validating Then reports LENGTH_SENTENCES
  Scenario: Given sentences at max length (20 sentences - boundary) When validating Then no violations are returned
  Scenario: SentenceCompletarOracionRequestDto validation
  Scenario: Given valid sentence with words When validating Then no violations are returned
  Scenario: Given words is null (required field) When validating Then reports NOT_NULL_WORDS
  Scenario: Given words has 2 words (should be 3-300 - edge case) When validating Then reports LENGTH_SENTENCE
  Scenario: Given words has 301 words (should be 3-300 - edge case) When validating Then reports LENGTH_SENTENCE
  Scenario: Given words at max length (300 words - boundary) When validating Then no violations are returned
  Scenario: WordCompletarOracionRequestDto validation
  Scenario: Given valid word When validating Then no violations are returned
  Scenario: Given word is blank (required field) When validating Then reports NOT_EMPTY_WORD
  Scenario: Given word exceeds max length (31 chars - edge case) When validating Then reports LENGTH_WORD
  Scenario: Given word at max length (30 chars - boundary) When validating Then no violations are returned
  Scenario: Given wordOrder is null (required field) When validating Then reports NOT_NULL_WORD_ORDER
  Scenario: Given isMissing is null (required field - palabra sin indicador de faltante) When validating Then reports NOT_NULL_IS_MISSING

Feature: cu42generateCompletarOracionActivity
  Scenario: cu42generateCompletarOracionActivity
  Scenario: Given valid request with teacher assigned to subject When generating completar oracion Then returns CompletarOracionActivityResponseDto
  Scenario: Given subject not found When generating completar oracion Then throws NotFoundException
  Scenario: Given teacher not assigned to subject When generating completar oracion Then throws ConflictException
  Scenario: Given invalid words order When generating completar oracion Then throws BadRequestException
  Scenario: Given no words missing When generating completar oracion Then throws BadRequestException
  Scenario: Given valid request When generating completar oracion Then saves activity with correct fields

Feature: POST /activities/memorama
  Scenario: POST /activities/memorama
  Scenario: Given valid request with 4 couples and images When registering memorama Then returns MemoramaResponseDto with 201 Created
  Scenario: Given teacher not assigned to subject When registering memorama Then returns 409 Conflict
  Scenario: Given invalid couples size (parejas incompletas - edge case) When registering memorama Then returns 400 BadRequest

Feature: MemoramaRequestDto validation
  Scenario: MemoramaRequestDto validation
  Scenario: Given valid request with couples When validating Then no violations are returned
  Scenario: Given couples is null (required field - edge case) When validating Then reports NOT_EMPTY_COUPLES
  Scenario: Given couples is empty (should be 4-8 - edge case - parejas vacías) When validating Then reports MIN_COUPLES
  Scenario: Given couples has 3 couples (should be 4-8 - edge case - parejas incompletas) When validating Then reports LENGTH_COUPLES
  Scenario: Given couples has 9 couples (should be 4-8 - edge case) When validating Then reports LENGTH_COUPLES
  Scenario: Given couples at max length (8 couples - boundary) When validating Then no violations are returned
  Scenario: CouplesMemoramaRequestDto validation
  Scenario: Given valid couple with concept and image When validating Then no violations are returned
  Scenario: Given concept is blank (required field) When validating Then reports NOT_NULL_CONCEPT
  Scenario: Given concept exceeds max length (51 chars - edge case) When validating Then reports MAX_LENGTH_CONCEPT
  Scenario: Given concept at max length (50 chars - boundary) When validating Then no violations are returned
  Scenario: Given concept contains numbers (invalid pattern - concepto con números) When validating Then reports PATTERN_CONCEPT
  Scenario: Given concept contains special chars (invalid pattern - concepto con caracteres especiales) When validating Then reports PATTERN_CONCEPT
  Scenario: Given concept with accents (valid pattern) When validating Then no violations are returned
  Scenario: Given image is null (required field - pareja sin imagen) When validating Then reports NOT_NULL_IMAGE

Feature: cu41GenerateMemorama
  Scenario: cu41GenerateMemorama
  Scenario: Given valid request with teacher assigned to subject When generating memorama Then returns MemoramaResponseDto
  Scenario: Given subject not found When generating memorama Then throws NotFoundException
  Scenario: Given teacher not assigned to subject When generating memorama Then throws ConflictException
  Scenario: Given valid request When generating memorama Then saves memorama with correct fields

Feature: POST /activities/no-ludica
  Scenario: POST /activities/no-ludica
  Scenario: Given valid request with exercise and tipoEntrega When generating no ludica Then returns NoLudicaResponseDto with 201 Created
  Scenario: Given teacher not assigned to subject When generating no ludica Then returns 409 Conflict
  Scenario: Given invalid JSON payload (contenido malformado - edge case) When generating no ludica Then returns 400 BadRequest

Feature: NoLudicaRequestDto validation
  Scenario: NoLudicaRequestDto validation
  Scenario: Given valid request with exercise and tipoEntrega When validating Then no violations are returned
  Scenario: Given exercise is null (required field - edge case - contenido vacío) When validating Then reports NOT_NULL_EXCERSICE
  Scenario: Given exercise exceeds max length (301 chars - edge case - contenido demasiado largo) When validating Then reports MAX_LENGTH_EXCERSICE
  Scenario: Given exercise at max length (300 chars - boundary) When validating Then no violations are returned
  Scenario: Given tipoEntrega is null (required field - edge case - tipo de entrega vacío) When validating Then reports NOT_NULL_TIPO_ENTREGA
  Scenario: Given tipoEntrega is ENTREGA When validating Then no violations are returned
  Scenario: Given tipoEntrega is ENLACE When validating Then no violations are returned
  Scenario: Given tipoEntrega is TEXTO When validating Then no violations are returned

Feature: cu45GenerateNoLudica
  Scenario: cu45GenerateNoLudica
  Scenario: Given valid request with teacher assigned to subject When generating no ludica Then returns NoLudicaResponseDto
  Scenario: Given subject not found When generating no ludica Then throws NotFoundException
  Scenario: Given teacher not assigned to subject When generating no ludica Then throws ConflictException
  Scenario: Given valid request When generating no ludica Then verifies saved fields with ArgumentCaptor

Feature: POST /activities/ordenar-secuencia
  Scenario: POST /activities/ordenar-secuencia
  Scenario: Given valid request with 3 events and images When generating ordenar secuencia Then returns OrdenarSecuenciaResponseDto with 201 Created
  Scenario: Given teacher not assigned to subject When generating ordenar secuencia Then returns 409 Conflict
  Scenario: Given invalid events order (eventos con orden inválido - edge case) When generating ordenar secuencia Then returns 400 BadRequest

Feature: OrdenarSecuenciaRequestDto validation
  Scenario: OrdenarSecuenciaRequestDto validation
  Scenario: Given valid request with 3 events When validating Then no violations are returned
  Scenario: Given events is null (required field - edge case - secuencia vacía) When validating Then reports LENGTH_EVENTS
  Scenario: Given events has 2 events (should be 3-10 - edge case - secuencia incompleta) When validating Then reports LENGTH_EVENTS
  Scenario: Given events has 11 events (should be 3-10 - edge case) When validating Then reports LENGTH_EVENTS
  Scenario: Given events at max length (10 events - boundary) When validating Then no violations are returned
  Scenario: EventRequestDto validation
  Scenario: Given valid event with name, description and order When validating Then no violations are returned
  Scenario: Given name is blank (required field) When validating Then reports NOT_EMPTY_NAME
  Scenario: Given name exceeds max length (51 chars - edge case) When validating Then reports MAX_LENGTH_NAME_50
  Scenario: Given name at max length (50 chars - boundary) When validating Then no violations are returned
  Scenario: Given description is blank (required field) When validating Then reports NOT_EMPTY_DESCRIPTION
  Scenario: Given description exceeds max length (101 chars - edge case) When validating Then reports MAX_LENGTH_DESCRIPTION_100
  Scenario: Given description at max length (100 chars - boundary) When validating Then no violations are returned
  Scenario: Given order is negative (should be >= 0 - edge case - orden inválido) When validating Then reports MIN_ORDER
  Scenario: Given order is zero (boundary) When validating Then no violations are returned
  Scenario: Given order is positive When validating Then no violations are returned

Feature: cu44GenerateOrdenarSecuencia
  Scenario: cu44GenerateOrdenarSecuencia
  Scenario: Given valid request with teacher assigned to subject When generating ordenar secuencia Then returns OrdenarSecuenciaResponseDto
  Scenario: Given subject not found When generating ordenar secuencia Then throws NotFoundException
  Scenario: Given teacher not assigned to subject When generating ordenar secuencia Then throws ConflictException
  Scenario: Given invalid events order When generating ordenar secuencia Then throws BadRequestException
  Scenario: Given valid request When generating ordenar secuencia Then verifies saved fields with ArgumentCaptor

Feature: POST /activities/preguntados
  Scenario: POST /activities/preguntados
  Scenario: Given valid request with 5 questions and options When generating preguntados Then returns PreguntadosResponseDto with 201 Created
  Scenario: Given teacher not assigned to subject When generating preguntados Then returns 409 Conflict
  Scenario: Given question without correct option (pregunta sin opción correcta - edge case) When generating preguntados Then returns 400 BadRequest
  Scenario: Given invalid JSON payload (contenido malformado - edge case) When generating preguntados Then returns 400 BadRequest

Feature: PreguntadosRequestDto validation
  Scenario: PreguntadosRequestDto validation
  Scenario: Given valid request with 5 questions When validating Then no violations are returned
  Scenario: Given maxTimePerQuestionInSeconds is 9 (should be >= 10 - edge case - tiempo por pregunta inválido) When validating Then reports MIN_TIME_PER_QUESTION
  Scenario: Given maxTimePerQuestionInSeconds is 10 (boundary) When validating Then no violations are returned
  Scenario: Given questions has 4 questions (should be >= 5 - edge case - preguntas incompletas) When validating Then reports MIN_LENGTH_QUESTIONS
  Scenario: Given questions is null (required field - edge case - preguntas vacías) When validating Then reports MIN_LENGTH_QUESTIONS
  Scenario: QuestionRequestDto validation
  Scenario: Given valid question with 4 options When validating Then no violations are returned
  Scenario: Given question is blank (required field) When validating Then reports NOT_EMPTY_QUESTION
  Scenario: Given question exceeds max length (201 chars - edge case) When validating Then reports MAX_LENGTH_QUESTION
  Scenario: Given question at max length (200 chars - boundary) When validating Then no violations are returned
  Scenario: Given options has 3 options (should be exactly 4 - edge case - opciones incompletas) When validating Then reports LENGTH_OPTIONS
  Scenario: Given options has 5 options (should be exactly 4 - edge case - múltiples opciones) When validating Then reports LENGTH_OPTIONS
  Scenario: Given options is null (required field - edge case - opciones vacías) When validating Then reports LENGTH_OPTIONS
  Scenario: OptionRequestDto validation
  Scenario: Given valid option with text and isCorrect When validating Then no violations are returned
  Scenario: Given option is blank (required field) When validating Then reports NOT_EMPTY_OPTION
  Scenario: Given option exceeds max length (101 chars - edge case) When validating Then reports MAX_LENGTH_OPTION
  Scenario: Given option at max length (100 chars - boundary) When validating Then no violations are returned

Feature: cu40GeneratePreguntados
  Scenario: cu40GeneratePreguntados
  Scenario: Given valid request with teacher assigned to subject When generating preguntados Then returns PreguntadosResponseDto
  Scenario: Given subject not found When generating preguntados Then throws NotFoundException
  Scenario: Given teacher not assigned to subject When generating preguntados Then throws ConflictException
  Scenario: Given question without correct option (pregunta sin opción correcta - edge case) When generating preguntados Then throws BadRequestException
  Scenario: Given valid request When generating preguntados Then verifies saved fields with ArgumentCaptor
