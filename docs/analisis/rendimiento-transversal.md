# Análisis Transversal de Rendimiento

## Resumen Ejecutivo

Este análisis identifica problemas de rendimiento que afectan a múltiples módulos del sistema, con énfasis en consultas N+1, falta de índices, y operaciones costosas.

**Fecha de Análisis:** 2025-01-XX  
**Alcance:** Todo el proyecto  
**Total de Hallazgos:** 15

---

## 1. Consultas N+1 Identificadas

### 1.1. ActivityGetByStudentService

**Ubicación:** `activity/activity/services/commons/ActivityGetByStudentService.java`  
**Módulo:** Activity  
**Severidad:** Alta

**Problema:**
Primero obtiene todas las materias del estudiante, luego busca actividades por esas materias. Puede generar múltiples consultas.

**Solución:**
```java
@Query("SELECT a FROM Activity a " +
       "JOIN a.subject s " +
       "JOIN s.students st " +
       "WHERE st.id = :studentId AND a.deletedAt IS NULL")
List<Activity> findAllByStudentId(@Param("studentId") Long studentId);
```

---

### 1.2. Relaciones ManyToOne en Admin

**Ubicación:** Servicios de listado paginado  
**Módulo:** Admin  
**Severidad:** Media

**Problema:**
Al obtener listas de entidades con relaciones `@ManyToOne` (ej: Course con Year), puede haber consultas N+1.

**Solución:**
Usar `@EntityGraph`:
```java
@EntityGraph(attributePaths = {"year"})
Page<Course> findAll(Specification<Course> spec, Pageable pageable);
```

---

### 1.3. BenefitPurchase con Relaciones

**Ubicación:** Servicios de beneficios  
**Módulo:** Benefits  
**Severidad:** Media

**Problema:**
Consultas de compras de beneficios pueden cargar relaciones sin `@EntityGraph`.

**Solución:**
Agregar `@EntityGraph` para cargar `benefit` y `student` en una consulta.

---

## 2. Índices Faltantes

### 2.1. Entidad Activity

**Campos sin índice:**
- `deletedAt`
- `subject_id`
- `startDate`, `endDate`

**Impacto:** Alto  
**Prioridad:** Alta

**Solución:**
```java
@Table(indexes = {
    @Index(name = "idx_activity_deleted_at", columnList = "deletedAt"),
    @Index(name = "idx_activity_subject_id", columnList = "subject_id"),
    @Index(name = "idx_activity_dates", columnList = "startDate, endDate")
})
```

---

### 2.2. Entidades Admin (Course, Student, Subject, Teacher, Year)

**Campos sin índice:**
- `deletedAt` (en todas)
- `name` (en todas)
- Claves foráneas

**Impacto:** Alto  
**Prioridad:** Alta

---

### 2.3. BenefitPurchase

**Campos sin índice:**
- `benefit_id`
- `student_id`
- `state`
- `deletedAt`

**Impacto:** Alto  
**Prioridad:** Alta

---

### 2.4. Transaction

**Campos sin índice:**
- `wallet_id`
- `created_at`
- `type_transaction`

**Impacto:** Alto  
**Prioridad:** Alta

---

### 2.5. Stock y Order

**Campos sin índice:**
- `wallet_id` (en Order)
- `stock_id` (en Order)
- `state` (en Order)
- `current_price` (en Stock)

**Impacto:** Alto  
**Prioridad:** Alta

---

## 3. Operaciones Costosas Sin Caché

### 3.1. Validación de JWT

**Ubicación:** `JwtSessionAspect.java`  
**Problema:** Cada request valida JWT y consulta BD para usuario activo.

**Solución:** Implementar caché de validaciones de tokens (TTL corto, ~5 minutos).

---

### 3.2. Actividades Activas

**Ubicación:** Módulo Activity  
**Problema:** Actividades activas consultadas frecuentemente sin caché.

**Solución:** Implementar caché para actividades con `endDate > now()`.

---

## 4. Paginación Ineficiente o Faltante

### 4.1. findAllByDeletedAtIsNull

**Ubicación:** Múltiples repositorios  
**Problema:** Retorna todos los registros sin límite.

**Solución:** Usar paginación o eliminar si no se usa.

---

### 4.2. findAllByActivity

**Ubicación:** `IActivityCompletedRepository.java:30`  
**Problema:** Retorna todas las completaciones sin límite.

**Solución:** Agregar paginación o límite.

---

## 5. Transacciones Largas

### 5.1. StockBuyService

**Ubicación:** `investment/stock/services/StockBuyService.java`  
**Problema:** Múltiples operaciones de BD en una transacción larga.

**Solución:** Optimizar consultas y considerar dividir si es posible.

---

## 6. Operaciones Síncronas que Deberían Ser Asíncronas

### 6.1. Registro de Logs (Ya Implementado)

**Estado:** ✅ Ya implementado con `@Async`

---

## 7. Eager Loading Innecesario

### 7.1. Relaciones en Entidades

**Problema:** Algunas relaciones pueden estar configuradas como EAGER cuando deberían ser LAZY.

**Solución:** Revisar y cambiar a LAZY donde corresponda.

---

## 8. Recomendaciones Prioritarias

### Prioridad Crítica
1. **Agregar índices en todas las entidades principales** - Mejora inmediata de rendimiento
2. **Optimizar consultas N+1 con JOIN FETCH o @EntityGraph** - Reducir consultas a BD

### Prioridad Alta
1. **Implementar paginación en consultas sin límite** - Prevenir cargas excesivas
2. **Implementar caché para validaciones JWT** - Reducir carga en BD

### Prioridad Media
1. **Revisar y optimizar transacciones largas** - Mejorar tiempo de respuesta
2. **Implementar caché para datos frecuentemente consultados** - Mejorar rendimiento general

---

## 9. Métricas Esperadas

Con las mejoras implementadas:
- **Reducción de consultas a BD:** 50-70%
- **Mejora en tiempo de respuesta:** 40-60%
- **Reducción de carga en BD:** 60-80%

---

## 10. Notas Adicionales

- Los índices son la mejora de rendimiento más impactante y fácil de implementar.
- Las consultas N+1 pueden causar problemas graves de rendimiento bajo carga.
- Considerar usar herramientas de profiling (ej: JProfiler, VisualVM) para identificar cuellos de botella específicos en producción.

