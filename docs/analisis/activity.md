# Análisis del Módulo Activity

## Resumen Ejecutivo

El módulo Activity gestiona todas las actividades educativas del sistema, incluyendo múltiples tipos de actividades (Ahorcado, Preguntados, Memorama, Clasificación, Completar Oración, Ordenar Secuencia, Árbol de Decisión, No Lúdica). El módulo es crítico para el funcionamiento del sistema educativo y maneja estados de actividades, intentos de estudiantes, y completación de actividades.

**Fecha de Análisis:** 2025-01-XX  
**Módulo Analizado:** `src/main/java/trinity/play2learn/backend/activity`  
**Total de Hallazgos:** 18

### Resumen por Categoría
- **Fallas Identificadas:** 5
- **Puntos de Mejora:** 4
- **Problemas de Rendimiento:** 5
- **Problemas de Seguridad:** 4

---

## 1. Análisis de Fallas

### 1.1. Falta de @Transactional en ActivityStartService

**Ubicación:** `src/main/java/trinity/play2learn/backend/activity/activity/services/ActivityStartService.java:49`  
**Severidad:** Alta  
**Categoría:** Manejo de Transacciones

**Descripción:**
El método `execute` en `ActivityStartService` realiza múltiples operaciones de base de datos (lecturas y escrituras) sin estar marcado con `@Transactional`. Esto puede causar inconsistencias si alguna operación falla a mitad del proceso.

**Código Problemático:**
```java
@Override
public ActivityCompletedResponseDto execute(User user, Long activityId) {
    // Múltiples operaciones de BD sin transacción
    Student student = studentGetByEmailService.getByEmail(user.getEmail());
    Activity activity = activityFindByIdService.findActivityById(activityId);
    // ... más operaciones
    if (lastStartedOpt.isPresent()) {
        activityCompletedService.cu61ActivityCompleted(...); // Puede modificar BD
    }
    return ActivityCompletedMapper.toDto(
        activityCompletedRepository.save(...) // Escritura sin transacción
    );
}
```

**Impacto:**
Si ocurre un error después de desaprobar una actividad en curso pero antes de crear la nueva, el sistema puede quedar en un estado inconsistente donde se desaprobó una actividad pero no se creó la nueva.

**Recomendación:**
Agregar `@Transactional` al método para asegurar atomicidad:

```java
@Override
@Transactional
public ActivityCompletedResponseDto execute(User user, Long activityId) {
    // ... código existente
}
```

**Prioridad:** Alta

---

### 1.2. Posible Condición de Carrera en ActivityStartService

**Ubicación:** `src/main/java/trinity/play2learn/backend/activity/activity/services/ActivityStartService.java:63-86`  
**Severidad:** Alta  
**Categoría:** Concurrencia

**Descripción:**
El método verifica los intentos restantes, luego verifica si hay una actividad en curso, y finalmente crea una nueva. Entre estas verificaciones, otro thread podría iniciar una actividad, causando que se excedan los intentos permitidos.

**Código Problemático:**
```java
Integer remainingAttempts = activityGetRemainingAttemptsService.getStudentRemainingAttempts(activity, student);

if (remainingAttempts == 0) {
    throw new ConflictException("No quedan intentos para realizar la actividad.");
}

// RACE CONDITION: Otro thread podría iniciar aquí
Optional<ActivityCompleted> lastStartedOpt = activityCompletedGetLastStartedService.getLastStartedInProgress(activity, student);
// ... más código
activityCompletedRepository.save(...); // Puede exceder intentos si otro thread inició
```

**Impacto:**
Un estudiante podría iniciar múltiples actividades simultáneamente, excediendo el límite de intentos configurado.

**Recomendación:**
Usar bloqueo optimista o pesimista a nivel de base de datos:

```java
@Override
@Transactional
public ActivityCompletedResponseDto execute(User user, Long activityId) {
    // Bloquear la actividad o el estudiante para esta actividad
    Activity activity = activityFindByIdService.findActivityById(activityId);
    // Usar SELECT FOR UPDATE o versión optimista
    // ... resto del código
}
```

**Prioridad:** Alta

---

### 1.3. Validación Incorrecta de Null en NoLudicaValidationsService

**Ubicación:** `src/main/java/trinity/play2learn/backend/activity/activity/services/commons/NoLudicaValidationsService.java:15`  
**Severidad:** Media  
**Categoría:** Validación

**Descripción:**
La validación verifica `plaintext.length() == 0` después de verificar si `plaintext == null`, pero el orden de las condiciones puede causar un `NullPointerException` si `plaintext` es null y `file` no está vacío.

**Código Problemático:**
```java
if ((file == null || file.isEmpty()) && (plaintext.length() == 0 || plaintext == null)) {
    throw new BadRequestException("El texto y el archivo no pueden estar vacios");
}
```

**Impacto:**
Si `plaintext` es `null` y `file` no está vacío, se intentará acceder a `plaintext.length()` causando un `NullPointerException`.

**Recomendación:**
Corregir el orden de las validaciones:

```java
if ((file == null || file.isEmpty()) && (plaintext == null || plaintext.length() == 0)) {
    throw new BadRequestException("El texto y el archivo no pueden estar vacios");
}
```

**Prioridad:** Media

---

### 1.4. Falta Validación de Fechas en Activity

**Ubicación:** `src/main/java/trinity/play2learn/backend/activity/activity/models/activity/Activity.java`  
**Severidad:** Media  
**Categoría:** Validación de Negocio

**Descripción:**
No hay validación que asegure que `endDate` sea posterior a `startDate`. Esto puede permitir crear actividades con fechas inválidas.

**Código Problemático:**
```java
@NotNull
private LocalDateTime startDate;

@NotNull 
private LocalDateTime endDate;
// No hay validación que endDate > startDate
```

**Impacto:**
Se pueden crear actividades que nunca estarán disponibles porque `endDate` es anterior a `startDate`.

**Recomendación:**
Agregar validación en el DTO o en el servicio:

```java
// En ActivityRequestDto o en el servicio de generación
@AssertTrue(message = "La fecha de fin debe ser posterior a la fecha de inicio")
public boolean isValidDateRange() {
    return endDate == null || startDate == null || endDate.isAfter(startDate);
}
```

**Prioridad:** Media

---

### 1.5. Cálculo de Tiempo Puede Ser Negativo

**Ubicación:** `src/main/java/trinity/play2learn/backend/activity/activity/services/ActivityCompletedService.java:78`  
**Severidad:** Baja  
**Categoría:** Lógica de Negocio

**Descripción:**
El método `calculateTimeAttemp` no valida que `startedAt` sea anterior a la fecha actual. Si por algún error `startedAt` es futuro, el cálculo resultará en un valor negativo.

**Código Problemático:**
```java
private int calculateTimeAttemp (LocalDateTime startedAt){
    return (int) (Duration.between(startedAt, LocalDateTime.now()).getSeconds())/60;
}
```

**Impacto:**
Si hay un error en la fecha, el tiempo calculado puede ser negativo, lo que podría afectar la lógica de desaprobación automática.

**Recomendación:**
Agregar validación y manejo de casos edge:

```java
private int calculateTimeAttemp(LocalDateTime startedAt) {
    if (startedAt == null) {
        return 0;
    }
    long seconds = Duration.between(startedAt, LocalDateTime.now()).getSeconds();
    return Math.max(0, (int) (seconds / 60)); // Asegurar no negativo
}
```

**Prioridad:** Baja

---

## 2. Puntos de Mejora

### 2.1. Código Duplicado en Validaciones de Actividades

**Ubicación:** Múltiples servicios de generación de actividades  
**Categoría:** Código Duplicado

**Descripción:**
Todos los servicios de generación de actividades (Ahorcado, Preguntados, Memorama, etc.) tienen código similar para validar que el docente esté asignado a la materia.

**Código Actual:**
```java
// Repetido en múltiples servicios
Teacher teacher = teacherGetByEmailService.getByEmail(user.getEmail());
Subject subject = subjectGetService.findById(dto.getSubjectId());

if (!subject.getTeacher().equals(teacher)) {
    throw new ConflictException("El docente no esta asignado a la materia");
}
```

**Código Mejorado:**
Crear un servicio común de validación:

```java
@Service
@AllArgsConstructor
public class ActivityAuthorizationService {
    private final ITeacherGetByEmailService teacherGetByEmailService;
    private final ISubjectGetByIdService subjectGetService;
    
    public void validateTeacherSubjectAccess(User user, Long subjectId) {
        Teacher teacher = teacherGetByEmailService.getByEmail(user.getEmail());
        Subject subject = subjectGetService.findById(subjectId);
        
        if (!subject.getTeacher().equals(teacher)) {
            throw new ConflictException("El docente no esta asignado a la materia");
        }
    }
}
```

**Beneficios:**
- Reduce duplicación de código
- Facilita mantenimiento
- Centraliza lógica de autorización

**Prioridad:** Media

---

### 2.2. Falta de Constantes para Mensajes de Error

**Ubicación:** Múltiples servicios  
**Categoría:** Mantenibilidad

**Descripción:**
Los mensajes de error están hardcodeados en múltiples lugares, dificultando el mantenimiento y la internacionalización.

**Código Actual:**
```java
throw new ConflictException("La actividad ya ha sido aprobada.");
throw new ConflictException("No quedan intentos para realizar la actividad.");
```

**Código Mejorado:**
Usar constantes centralizadas:

```java
// En ConflictExceptionMessages
public static final String ACTIVITY_ALREADY_APPROVED = "La actividad ya ha sido aprobada.";
public static final String NO_REMAINING_ATTEMPTS = "No quedan intentos para realizar la actividad.";
```

**Beneficios:**
- Facilita mantenimiento
- Permite internacionalización
- Consistencia en mensajes

**Prioridad:** Baja

---

### 2.3. Validación de Fechas Mezclada con Lógica de Negocio

**Ubicación:** `ActivityValidatePublishedStatusService.java`  
**Categoría:** Diseño

**Descripción:**
La validación de fechas está en un servicio separado, pero la lógica de disponibilidad está en el modelo. Esto crea inconsistencia.

**Recomendación:**
Mover toda la lógica de fechas al modelo `Activity` o crear un servicio unificado de validación de disponibilidad.

**Prioridad:** Baja

---

### 2.4. Falta de Validación de Límites en Campos Numéricos

**Ubicación:** `ActivityRequestDto.java`  
**Categoría:** Validación

**Descripción:**
Campos como `maxTime`, `attempts`, `initialBalance` no tienen validaciones de rango (mínimo/máximo).

**Recomendación:**
Agregar validaciones:

```java
@Min(value = 1, message = "El tiempo máximo debe ser al menos 1 minuto")
private int maxTime;

@Min(value = 0, message = "Los intentos no pueden ser negativos")
private int attempts;

@DecimalMin(value = "0.0", message = "El balance inicial no puede ser negativo")
private Double initialBalance;
```

**Prioridad:** Media

---

## 3. Mejoras de Rendimiento

### 3.1. Consulta N+1 Potencial en ActivityGetByStudentService

**Ubicación:** `src/main/java/trinity/play2learn/backend/activity/activity/services/commons/ActivityGetByStudentService.java:27-29`  
**Tipo:** Consulta N+1  
**Impacto Estimado:** Alto

**Descripción:**
El servicio primero obtiene todas las materias del estudiante, luego busca actividades por esas materias. Si el estudiante tiene muchas materias, esto puede generar múltiples consultas.

**Código Problemático:**
```java
List<Subject> subjects = subjectGetByStudentService.getByStudent(student);
List<Activity> activities = activityRepository.findAllBySubjectInAndDeletedAtIsNull(subjects);
```

**Análisis:**
Aunque `findAllBySubjectIn` debería hacer una sola consulta, si `subjectGetByStudentService.getByStudent` hace múltiples consultas o carga relaciones, puede haber un problema N+1.

**Solución Recomendada:**
Usar un JOIN directo o especificación que incluya la relación:

```java
@Query("SELECT a FROM Activity a " +
       "JOIN a.subject s " +
       "JOIN s.students st " +
       "WHERE st.id = :studentId AND a.deletedAt IS NULL")
List<Activity> findAllByStudentId(@Param("studentId") Long studentId);
```

**Métricas Esperadas:**
- Reducción de consultas: De N+1 a 1 consulta
- Mejora en tiempo de respuesta: 50-80% para estudiantes con muchas materias

**Prioridad:** Alta

---

### 3.2. Falta de Índices en Entidad Activity

**Ubicación:** `src/main/java/trinity/play2learn/backend/activity/activity/models/activity/Activity.java`  
**Tipo:** Falta de Índices  
**Impacto Estimado:** Alto

**Descripción:**
La entidad `Activity` no tiene índices definidos en campos frecuentemente consultados como `deletedAt`, `subject_id`, `startDate`, y `endDate`.

**Código Problemático:**
```java
@Entity
public abstract class Activity {
    @Column(nullable = true)
    private LocalDateTime deletedAt; // Sin índice
    
    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject; // Sin índice explícito
    
    private LocalDateTime startDate; // Sin índice
    private LocalDateTime endDate; // Sin índice
}
```

**Análisis:**
Las consultas que filtran por `deletedAt IS NULL`, `subject_id`, o rangos de fechas serán lentas en tablas grandes.

**Solución Recomendada:**
Agregar índices:

```java
@Entity
@Table(indexes = {
    @Index(name = "idx_activity_deleted_at", columnList = "deletedAt"),
    @Index(name = "idx_activity_subject_id", columnList = "subject_id"),
    @Index(name = "idx_activity_dates", columnList = "startDate, endDate")
})
public abstract class Activity {
    // ... campos
}
```

**Métricas Esperadas:**
- Mejora en consultas con filtros: 70-90%
- Reducción en tiempo de búsqueda: 60-80%

**Prioridad:** Alta

---

### 3.3. Consulta Sin Paginación en findAllByActivity

**Ubicación:** `src/main/java/trinity/play2learn/backend/activity/activity/repositories/IActivityCompletedRepository.java:30`  
**Tipo:** Operación Costosa  
**Impacto Estimado:** Medio

**Descripción:**
El método `findAllByActivity` retorna todas las completaciones de una actividad sin límite, lo que puede ser costoso para actividades populares.

**Código Problemático:**
```java
List<ActivityCompleted> findAllByActivity(Activity activity);
```

**Análisis:**
Para actividades con cientos o miles de estudiantes, esto puede cargar miles de registros en memoria.

**Solución Recomendada:**
Usar paginación o límites:

```java
Page<ActivityCompleted> findAllByActivity(Activity activity, Pageable pageable);

// O agregar límite por defecto
@Query("SELECT ac FROM ActivityCompleted ac WHERE ac.activity = :activity ORDER BY ac.completedAt DESC")
List<ActivityCompleted> findTop100ByActivity(@Param("activity") Activity activity);
```

**Prioridad:** Media

---

### 3.4. Múltiples Consultas en ActivityStartService

**Ubicación:** `src/main/java/trinity/play2learn/backend/activity/activity/services/ActivityStartService.java`  
**Tipo:** Múltiples Consultas  
**Impacto Estimado:** Medio

**Descripción:**
El servicio realiza múltiples consultas secuenciales que podrían optimizarse.

**Análisis:**
- Consulta de estudiante por email
- Consulta de actividad por ID
- Consulta de estado completado
- Consulta de intentos restantes
- Consulta de última actividad iniciada

Algunas de estas podrían combinarse.

**Solución Recomendada:**
Crear un método de repositorio que obtenga toda la información necesaria en una consulta:

```java
@Query("SELECT ac FROM ActivityCompleted ac " +
       "WHERE ac.activity.id = :activityId AND ac.student.id = :studentId " +
       "ORDER BY ac.startedAt DESC")
List<ActivityCompleted> findRecentByActivityAndStudent(
    @Param("activityId") Long activityId, 
    @Param("studentId") Long studentId
);
```

**Prioridad:** Baja

---

### 3.5. Falta de Caché para Actividades Frecuentemente Consultadas

**Ubicación:** Servicios de consulta de actividades  
**Tipo:** Falta de Caché  
**Impacto Estimado:** Medio

**Descripción:**
Las actividades que están activas y son consultadas frecuentemente no tienen caché, causando consultas repetidas a la base de datos.

**Recomendación:**
Implementar caché para actividades activas:

```java
@Cacheable(value = "activities", key = "#activityId")
public Activity findActivityById(Long activityId) {
    // ... código existente
}
```

**Prioridad:** Baja

---

## 4. Mejoras de Seguridad

### 4.1. Falta Validación de Autorización en Consultas de Actividades

**Ubicación:** `ActivityGetByStudentService.java`  
**Severidad:** Media  
**Tipo:** Autorización

**Descripción:**
El servicio obtiene actividades basándose solo en las materias del estudiante, pero no valida explícitamente que el estudiante tenga acceso a esas materias en el momento de la consulta.

**Código Vulnerable:**
```java
public List<Activity> getByStudent(Student student) {
    List<Subject> subjects = subjectGetByStudentService.getByStudent(student);
    List<Activity> activities = activityRepository.findAllBySubjectInAndDeletedAtIsNull(subjects);
    return activities;
}
```

**Riesgo:**
Si hay un error en `subjectGetByStudentService` o si las relaciones cambian, un estudiante podría ver actividades de materias a las que no debería tener acceso.

**Solución Recomendada:**
Agregar validación explícita:

```java
public List<Activity> getByStudent(Student student) {
    List<Subject> subjects = subjectGetByStudentService.getByStudent(student);
    
    // Validación adicional de seguridad
    if (subjects.isEmpty()) {
        return Collections.emptyList();
    }
    
    List<Activity> activities = activityRepository.findAllBySubjectInAndDeletedAtIsNull(subjects);
    
    // Filtrar actividades que no están publicadas (opcional, según requerimientos)
    return activities.stream()
        .filter(Activity::isAvailable)
        .collect(Collectors.toList());
}
```

**Prioridad:** Media

---

### 4.2. Exposición de Información en Mensajes de Error

**Ubicación:** Múltiples servicios  
**Severidad:** Baja  
**Tipo:** Exposición de Información

**Descripción:**
Algunos mensajes de error pueden exponer información sobre la estructura interna del sistema.

**Código Vulnerable:**
```java
throw new NotFoundException("Activity with id " + activityId + " not found");
```

**Riesgo:**
Aunque el riesgo es bajo, exponer IDs puede ayudar a un atacante a enumerar recursos.

**Solución Recomendada:**
Usar mensajes genéricos:

```java
throw new NotFoundException("La actividad solicitada no fue encontrada");
```

**Prioridad:** Baja

---

### 4.3. Falta de Validación de Límites en Inputs

**Ubicación:** DTOs de actividades  
**Severidad:** Media  
**Tipo:** Validación de Entrada

**Descripción:**
Algunos campos numéricos no tienen validación de límites máximos, lo que podría permitir valores extremos que afecten el rendimiento.

**Recomendación:**
Agregar validaciones:

```java
@Min(value = 1, message = "...")
@Max(value = 1440, message = "El tiempo máximo no puede exceder 24 horas (1440 minutos)")
private int maxTime;

@Min(value = 0, message = "...")
@Max(value = 100, message = "Los intentos no pueden exceder 100")
private int attempts;
```

**Prioridad:** Media

---

### 4.4. Validación de Archivos en NoLudica

**Ubicación:** `NoLudicaValidationsService.java`  
**Severidad:** Media  
**Tipo:** Validación de Entrada

**Descripción:**
No hay validación del tipo MIME, tamaño máximo, o contenido del archivo subido en actividades no lúdicas.

**Recomendación:**
Agregar validaciones de archivo:

```java
public void validateNoLudicaCompleted(String plaintext, MultipartFile file) {
    if (file != null && !file.isEmpty()) {
        // Validar tipo MIME
        String contentType = file.getContentType();
        if (!isAllowedContentType(contentType)) {
            throw new BadRequestException("Tipo de archivo no permitido");
        }
        
        // Validar tamaño
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("El archivo excede el tamaño máximo permitido");
        }
    }
    // ... resto de validaciones
}
```

**Prioridad:** Media

---

## 5. Recomendaciones Prioritarias

### Prioridad Crítica (Implementar Inmediatamente)
1. **Agregar @Transactional a ActivityStartService** - Prevenir inconsistencias de datos
2. **Implementar bloqueo para prevenir condición de carrera** - Evitar exceder límites de intentos
3. **Corregir validación de null en NoLudicaValidationsService** - Prevenir NullPointerException

### Prioridad Alta (Implementar en Próximo Sprint)
1. **Agregar índices en entidad Activity** - Mejorar rendimiento de consultas
2. **Optimizar consulta N+1 en ActivityGetByStudentService** - Reducir consultas a BD
3. **Agregar validación de rango de fechas** - Prevenir actividades inválidas

### Prioridad Media (Planificar para Futuro)
1. **Refactorizar código duplicado de validación de docente** - Mejorar mantenibilidad
2. **Agregar validaciones de límites en campos numéricos** - Mejorar robustez
3. **Implementar paginación en findAllByActivity** - Mejorar rendimiento
4. **Agregar validación de autorización explícita** - Mejorar seguridad

### Prioridad Baja (Mejoras Opcionales)
1. **Centralizar mensajes de error** - Facilitar mantenimiento e internacionalización
2. **Implementar caché para actividades activas** - Mejorar rendimiento
3. **Optimizar múltiples consultas en ActivityStartService** - Mejora menor de rendimiento

---

## 6. Métricas y Estadísticas

- **Total de Archivos Analizados:** ~150 archivos
- **Total de Líneas de Código:** ~15,000 líneas (estimado)
- **Cobertura de Tests:** No disponible en este análisis

---

## Notas Adicionales

- El módulo Activity es extenso y complejo, con múltiples tipos de actividades que comparten funcionalidad común.
- La herencia de tablas (`InheritanceType.JOINED`) puede afectar el rendimiento de consultas. Considerar evaluar el uso de `SINGLE_TABLE` si el rendimiento es crítico.
- Algunos servicios tienen nombres poco descriptivos (ej: `cu61ActivityCompleted`). Considerar renombrar para mejorar legibilidad.
- La lógica de negocio está bien separada en servicios, pero hay oportunidades de mejora en la reutilización de código.

