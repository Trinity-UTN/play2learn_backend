# Mejores Prácticas para Manejo de Mensajes en APIs

## Análisis del Problema Actual

### Situación Actual

Actualmente se tienen múltiples clases distribuidas con diferentes enfoques:

- `SuccessfulMessages` - Mezcla de métodos y constantes
- `ValidationMessages` - Constantes estáticas
- `BadRequestExceptionMessages` - Métodos (con sintaxis incorrecta)
- `ConflictExceptionMessages` - Métodos
- `NotFoundExceptionMesagges` - Métodos
- `UnauthorizedExceptionMessages` - Mezcla de constantes y métodos
- `EconomyMessages` - Mezcla de constantes y métodos

### Problemas Identificados

1. **Sintaxis Incorrecta**: 
   ```java
   // ❌ INCORRECTO - Los métodos no pueden ser `final String`
   public static final String invalidFormat(String atribute) {
       return "Formato invalido para el atributo: " + atribute;
   }
   ```

2. **Inconsistencias**:
   - Algunos mensajes son constantes: `SUBJECT_ADD_STUDENTS_SUCCESSFULLY`
   - Otros son métodos: `createdSuccessfully(String resource)`
   - Difícil de mantener y entender

3. **Escalabilidad**:
   - Cada nuevo tipo de mensaje requiere una nueva clase o modificar existentes
   - Archivos que crecen indefinidamente
   - Difícil encontrar mensajes existentes

4. **Mantenibilidad**:
   - Mensajes hardcodeados en el código
   - Sin centralización
   - Cambios requieren modificar código Java

5. **Sin Internacionalización**:
   - Imposible cambiar idiomas sin modificar código
   - No hay separación entre claves y valores

## Soluciones Recomendadas

### Opción 1: Sistema de Mensajes con ResourceBundle (RECOMENDADO)

**Ventajas:**
- ✅ Soporte nativo de internacionalización (i18n)
- ✅ Separación entre código y mensajes
- ✅ Fácil de mantener y actualizar
- ✅ Escalable y organizado
- ✅ Estándar de la industria

**Estructura Propuesta:**

```
src/main/resources/
  messages/
    messages.properties          # Español (default)
    messages_en.properties       # Inglés
    messages_pt.properties       # Portugués
```

**Implementación:**

1. **Archivos de Propiedades** (`messages.properties`):
```properties
# ===== MENSAJES DE ÉXITO =====
success.created=creado correctamente
success.created.resource={0} creado correctamente
success.updated=actualizado correctamente
success.updated.resource={0} actualizado correctamente
success.deleted=eliminado correctamente
success.deleted.resource={0} eliminado correctamente
success.restored=restaurado correctamente
success.restored.resource={0} restaurado correctamente
success.ok=OK
success.login=logueado correctamente
success.refresh.token=Token actualizado correctamente
success.subject.add.students=Estudiantes agregado con éxito
success.subject.remove.students=Estudiantes removidos con éxito
success.subject.assign.teacher=Profesor asignado con éxito
success.subject.unassign.teacher=Profesor desasignado con éxito

# ===== MENSAJES DE ERROR - NOT FOUND =====
error.not.found.resource.id={0} con id {1} no encontrado.
error.not.found.resource.deleted.id={0} con id {1} no encontrado o ya fue eliminado.
error.not.found.resource.email={0} con email {1} no encontrado.

# ===== MENSAJES DE ERROR - CONFLICT =====
error.conflict.resource.exists={0} ya existe.
error.conflict.resource.exists.id={0} con id {1} ya existe.
error.conflict.resource.exists.name={0} con el nombre {1} ya existe.
error.conflict.resource.exists.dni={0} con el DNI {1} ya existe.
error.conflict.resource.exists.attribute={0} con el atributo {1} con el valor {2} ya existe.
error.conflict.resource.deleted={0} con id {1} ya fue eliminado.
error.conflict.resource.deletion.not.allowed=El recurso {0} con id {1} no puede ser eliminado porque tiene asociaciones con: {2}.

# ===== MENSAJES DE ERROR - BAD REQUEST =====
error.bad.request.invalid.format=Formato inválido para el atributo: {0}
error.bad.request.event.order.repeated=El orden {0} se encuentra repetido.
error.bad.request.event.order.not.consecutive=El orden no es consecutivo, se esperaban {0} eventos consecutivos sin saltos.
error.bad.request.one.correct.option.per.question=La pregunta {0} debe tener una sola opción correcta.

# ===== MENSAJES DE ERROR - UNAUTHORIZED =====
error.unauthorized=Credenciales inválidas o acceso denegado.
error.unauthorized.password.invalid=La contraseña no es válida.
error.unauthorized.token.expired=El token ha expirado.
error.unauthorized.refresh.token.invalid=El refresh token es inválido.
error.unauthorized.access.token.invalid=El access token es inválido.
error.unauthorized.benefit.teacher=No puedes crear beneficios de esta materia porque no estás asignado a la materia.
error.unauthorized.required.roles=Se necesita alguno de estos roles: {0}

# ===== MENSAJES DE VALIDACIÓN =====
validation.not.empty.name=El nombre no puede estar vacío.
validation.max.length.name.50=El nombre no puede tener más de 50 caracteres.
validation.pattern.name=El nombre debe contener únicamente letras, números, espacios y los caracteres áéíóúÁÉÍÓÚñÑ.
# ... más mensajes de validación
```

2. **Clase de Constantes de Claves** (`MessageKeys.java`):
```java
package trinity.play2learn.backend.configs.messages;

/**
 * Claves centralizadas para mensajes de la aplicación.
 * Estas claves se resuelven mediante MessageService.
 */
public final class MessageKeys {
    
    private MessageKeys() {
        throw new UnsupportedOperationException("Clase de utilidad");
    }
    
    // ===== MENSAJES DE ÉXITO =====
    public static final String SUCCESS_CREATED = "success.created";
    public static final String SUCCESS_CREATED_RESOURCE = "success.created.resource";
    public static final String SUCCESS_UPDATED = "success.updated";
    public static final String SUCCESS_UPDATED_RESOURCE = "success.updated.resource";
    public static final String SUCCESS_DELETED = "success.deleted";
    public static final String SUCCESS_DELETED_RESOURCE = "success.deleted.resource";
    public static final String SUCCESS_RESTORED = "success.restored";
    public static final String SUCCESS_RESTORED_RESOURCE = "success.restored.resource";
    public static final String SUCCESS_OK = "success.ok";
    public static final String SUCCESS_LOGIN = "success.login";
    public static final String SUCCESS_REFRESH_TOKEN = "success.refresh.token";
    public static final String SUCCESS_SUBJECT_ADD_STUDENTS = "success.subject.add.students";
    public static final String SUCCESS_SUBJECT_REMOVE_STUDENTS = "success.subject.remove.students";
    public static final String SUCCESS_SUBJECT_ASSIGN_TEACHER = "success.subject.assign.teacher";
    public static final String SUCCESS_SUBJECT_UNASSIGN_TEACHER = "success.subject.unassign.teacher";
    
    // ===== MENSAJES DE ERROR - NOT FOUND =====
    public static final String ERROR_NOT_FOUND_RESOURCE_ID = "error.not.found.resource.id";
    public static final String ERROR_NOT_FOUND_RESOURCE_DELETED_ID = "error.not.found.resource.deleted.id";
    public static final String ERROR_NOT_FOUND_RESOURCE_EMAIL = "error.not.found.resource.email";
    
    // ===== MENSAJES DE ERROR - CONFLICT =====
    public static final String ERROR_CONFLICT_RESOURCE_EXISTS = "error.conflict.resource.exists";
    public static final String ERROR_CONFLICT_RESOURCE_EXISTS_ID = "error.conflict.resource.exists.id";
    public static final String ERROR_CONFLICT_RESOURCE_EXISTS_NAME = "error.conflict.resource.exists.name";
    public static final String ERROR_CONFLICT_RESOURCE_EXISTS_DNI = "error.conflict.resource.exists.dni";
    public static final String ERROR_CONFLICT_RESOURCE_EXISTS_ATTRIBUTE = "error.conflict.resource.exists.attribute";
    public static final String ERROR_CONFLICT_RESOURCE_DELETED = "error.conflict.resource.deleted";
    public static final String ERROR_CONFLICT_RESOURCE_DELETION_NOT_ALLOWED = "error.conflict.resource.deletion.not.allowed";
    
    // ===== MENSAJES DE ERROR - BAD REQUEST =====
    public static final String ERROR_BAD_REQUEST_INVALID_FORMAT = "error.bad.request.invalid.format";
    public static final String ERROR_BAD_REQUEST_EVENT_ORDER_REPEATED = "error.bad.request.event.order.repeated";
    public static final String ERROR_BAD_REQUEST_EVENT_ORDER_NOT_CONSECUTIVE = "error.bad.request.event.order.not.consecutive";
    public static final String ERROR_BAD_REQUEST_ONE_CORRECT_OPTION_PER_QUESTION = "error.bad.request.one.correct.option.per.question";
    
    // ===== MENSAJES DE ERROR - UNAUTHORIZED =====
    public static final String ERROR_UNAUTHORIZED = "error.unauthorized";
    public static final String ERROR_UNAUTHORIZED_PASSWORD_INVALID = "error.unauthorized.password.invalid";
    public static final String ERROR_UNAUTHORIZED_TOKEN_EXPIRED = "error.unauthorized.token.expired";
    public static final String ERROR_UNAUTHORIZED_REFRESH_TOKEN_INVALID = "error.unauthorized.refresh.token.invalid";
    public static final String ERROR_UNAUTHORIZED_ACCESS_TOKEN_INVALID = "error.unauthorized.access.token.invalid";
    public static final String ERROR_UNAUTHORIZED_BENEFIT_TEACHER = "error.unauthorized.benefit.teacher";
    public static final String ERROR_UNAUTHORIZED_REQUIRED_ROLES = "error.unauthorized.required.roles";
    
    // ===== MENSAJES DE VALIDACIÓN =====
    public static final String VALIDATION_NOT_EMPTY_NAME = "validation.not.empty.name";
    public static final String VALIDATION_MAX_LENGTH_NAME_50 = "validation.max.length.name.50";
    public static final String VALIDATION_PATTERN_NAME = "validation.pattern.name";
    // ... más claves de validación
}
```

3. **Servicio de Mensajes** (`MessageService.java`):
```java
package trinity.play2learn.backend.configs.messages;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * Servicio centralizado para resolver mensajes de la aplicación.
 * Utiliza ResourceBundle para soporte de internacionalización.
 */
@Service
@RequiredArgsConstructor
public class MessageService {
    
    private final MessageSource messageSource;
    
    /**
     * Obtiene un mensaje sin parámetros.
     *
     * @param key la clave del mensaje
     * @return el mensaje resuelto
     */
    public String getMessage(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }
    
    /**
     * Obtiene un mensaje con parámetros.
     *
     * @param key la clave del mensaje
     * @param args los argumentos para reemplazar placeholders {0}, {1}, etc.
     * @return el mensaje resuelto con parámetros reemplazados
     */
    public String getMessage(String key, Object... args) {
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }
    
    /**
     * Obtiene un mensaje con un Locale específico.
     *
     * @param key la clave del mensaje
     * @param locale el locale a usar
     * @param args los argumentos para reemplazar placeholders
     * @return el mensaje resuelto
     */
    public String getMessage(String key, Locale locale, Object... args) {
        return messageSource.getMessage(key, args, locale);
    }
}
```

4. **Clases Helper para Compatibilidad** (Opcional - para migración gradual):

```java
package trinity.play2learn.backend.configs.messages;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Clase helper para mantener compatibilidad con código existente.
 * Internamente usa MessageService.
 * 
 * @deprecated Usar MessageService directamente en lugar de estas clases.
 * Esta clase se mantiene solo para compatibilidad durante la migración.
 */
@Component
@RequiredArgsConstructor
@Deprecated
public class SuccessfulMessages {
    
    private final MessageService messageService;
    
    public String deletedSuccessfully() {
        return messageService.getMessage(MessageKeys.SUCCESS_DELETED);
    }
    
    public String deletedSuccessfully(String resource) {
        return messageService.getMessage(MessageKeys.SUCCESS_DELETED_RESOURCE, resource);
    }
    
    public String createdSuccessfully(String resource) {
        return messageService.getMessage(MessageKeys.SUCCESS_CREATED_RESOURCE, resource);
    }
    
    public String createdSuccessfully() {
        return messageService.getMessage(MessageKeys.SUCCESS_CREATED);
    }
    
    public String updatedSuccessfully(String resource) {
        return messageService.getMessage(MessageKeys.SUCCESS_UPDATED_RESOURCE, resource);
    }
    
    public String updatedSuccessfully() {
        return messageService.getMessage(MessageKeys.SUCCESS_UPDATED);
    }
    
    public String okSuccessfully() {
        return messageService.getMessage(MessageKeys.SUCCESS_OK);
    }
    
    public String restoredSuccessfully(String resource) {
        return messageService.getMessage(MessageKeys.SUCCESS_RESTORED_RESOURCE, resource);
    }
    
    public String restoredSuccessfully() {
        return messageService.getMessage(MessageKeys.SUCCESS_RESTORED);
    }
    
    public String loginSuccessfully() {
        return messageService.getMessage(MessageKeys.SUCCESS_LOGIN);
    }
    
    public String refreshTokenSuccessfully() {
        return messageService.getMessage(MessageKeys.SUCCESS_REFRESH_TOKEN);
    }
    
    // Constantes que ahora usan MessageService
    public String getSubjectAddStudentsSuccessfully() {
        return messageService.getMessage(MessageKeys.SUCCESS_SUBJECT_ADD_STUDENTS);
    }
    
    public String getSubjectRemoveStudentsSuccessfully() {
        return messageService.getMessage(MessageKeys.SUCCESS_SUBJECT_REMOVE_STUDENTS);
    }
    
    public String getSubjectAssignTeacherSuccessfully() {
        return messageService.getMessage(MessageKeys.SUCCESS_SUBJECT_ASSIGN_TEACHER);
    }
    
    public String getSubjectUnassignTeacherSuccessfully() {
        return messageService.getMessage(MessageKeys.SUCCESS_SUBJECT_UNASSIGN_TEACHER);
    }
}
```

### Opción 2: Sistema Simplificado con Enum (Alternativa)

Si no necesitas internacionalización inmediatamente, puedes usar un sistema más simple:

```java
public enum MessageType {
    SUCCESS_CREATED("creado correctamente"),
    SUCCESS_UPDATED("actualizado correctamente"),
    SUCCESS_DELETED("eliminado correctamente"),
    ERROR_NOT_FOUND("no encontrado"),
    // ... más mensajes
    
    private final String template;
    
    MessageType(String template) {
        this.template = template;
    }
    
    public String format(Object... args) {
        return MessageFormat.format(template, args);
    }
}
```

**Desventajas:** No soporta internacionalización fácilmente.

## Recomendación Final

**Usar la Opción 1 (ResourceBundle)** porque:

1. ✅ Estándar de la industria
2. ✅ Soporte nativo de i18n
3. ✅ Separación entre código y mensajes
4. ✅ Fácil de mantener y actualizar
5. ✅ Escalable
6. ✅ Permite migración gradual

## Plan de Migración

1. **Fase 1**: Crear `MessageService` y archivos de propiedades
2. **Fase 2**: Crear `MessageKeys` con todas las claves
3. **Fase 3**: Migrar código nuevo a usar `MessageService`
4. **Fase 4**: Crear clases helper para compatibilidad (opcional)
5. **Fase 5**: Migrar código existente gradualmente
6. **Fase 6**: Eliminar clases antiguas una vez migrado todo

## Ejemplos de Uso

### Antes (Actual):
```java
throw new NotFoundException(NotFoundExceptionMesagges.resourceNotFoundById("Usuario", "123"));
return ResponseFactory.ok(data, SuccessfulMessages.createdSuccessfully("Usuario"));
```

### Después (Recomendado):
```java
// En servicios/controladores
@RequiredArgsConstructor
public class UserService {
    private final MessageService messageService;
    
    public void findUser(String id) {
        if (user == null) {
            throw new NotFoundException(
                messageService.getMessage(
                    MessageKeys.ERROR_NOT_FOUND_RESOURCE_ID, 
                    "Usuario", 
                    id
                )
            );
        }
    }
    
    public ResponseEntity<BaseResponse<User>> createUser(User user) {
        // ... lógica de creación
        return ResponseFactory.ok(
            user, 
            messageService.getMessage(MessageKeys.SUCCESS_CREATED_RESOURCE, "Usuario")
        );
    }
}
```

## Ventajas del Enfoque Recomendado

1. **Centralización**: Todos los mensajes en un solo lugar
2. **Mantenibilidad**: Cambios sin tocar código Java
3. **Internacionalización**: Fácil agregar nuevos idiomas
4. **Type Safety**: Claves definidas en `MessageKeys`
5. **Testeable**: Fácil mockear `MessageService`
6. **Escalable**: Fácil agregar nuevos mensajes
7. **Estándar**: Patrón reconocido en la industria

