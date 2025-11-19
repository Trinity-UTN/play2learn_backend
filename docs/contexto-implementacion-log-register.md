# Contexto: Implementación de Registro de Logs en Sistemas Externos

## Descripción General

Este documento proporciona el contexto necesario para implementar el registro de logs en sistemas externos que se comunican con el Backend Logs API. El sistema permite centralizar el registro de errores y logs desde múltiples aplicaciones en un único punto.

## Endpoint de Registro de Logs

### Información del Endpoint

- **URL**: `POST /logs`
- **Autenticación**: Requerida mediante header `X-Application-Key`
- **Content-Type**: `application/json`

### URL Base de la API

La URL base del servidor debe configurarse como variable de entorno:
- **Variable**: `LOGS_API_BASE_URL`
- **Ejemplo**: `https://api.tudominio.com` o `http://localhost:8014`

**IMPORTANTE**: No incluir la barra final (`/`) en la URL base. El endpoint completo será: `{LOGS_API_BASE_URL}/logs`

## Configuración Requerida

### Variables de Entorno

Es **OBLIGATORIO** configurar las siguientes variables de entorno en tu sistema:

1. **`LOGS_API_PRIVATE_KEY`** (Requerido)
   - **Descripción**: Private Key de la aplicación registrada en el sistema de logs
   - **Origen**: Este valor se obtiene al registrar la aplicación mediante el endpoint `/applications` (requiere autenticación JWT con rol ROLE_DEV)
   - **Ejemplo**: `a1b2c3d4-e5f6-7890-abcd-ef1234567890`
   - **Formato**: UUID o string único generado por el sistema
   - **Ubicación**: Debe estar en el archivo `.env` de tu proyecto

2. **`LOGS_API_BASE_URL`** (Requerido)
   - **Descripción**: URL base del servidor de logs
   - **Ejemplo en producción**: `https://api.tudominio.com`
   - **Ejemplo en desarrollo**: `http://localhost:8014`
   - **Ubicación**: Debe estar en el archivo `.env` de tu proyecto

### Ejemplo de archivo `.env`

```env
# Configuración del sistema de logs centralizado
LOGS_API_BASE_URL=https://api.tudominio.com
LOGS_API_PRIVATE_KEY=a1b2c3d4-e5f6-7890-abcd-ef1234567890
```

## Estructura del Request

### Headers Requeridos

```
Content-Type: application/json
X-Application-Key: {LOGS_API_PRIVATE_KEY}
```

### Body del Request (JSON)

```json
{
  "mensaje": "string (requerido, máximo 5000 caracteres)",
  "nivel": "ERROR | WARN | INFO | DEBUG (requerido)",
  "stackTrace": "string (opcional)",
  "metadata": "string JSON (opcional)"
}
```

#### Campos Detallados

1. **`mensaje`** (requerido)
   - Tipo: `string`
   - Descripción: Mensaje descriptivo del log o error
   - Longitud máxima: 5000 caracteres
   - Ejemplo: `"Error al procesar solicitud de pago"`

2. **`nivel`** (requerido)
   - Tipo: `enum`
   - Valores permitidos: `ERROR`, `WARN`, `INFO`, `DEBUG`
   - Descripción:
     - `ERROR`: Errores críticos que requieren atención inmediata
     - `WARN`: Advertencias que pueden indicar problemas potenciales
     - `INFO`: Información general del flujo de la aplicación
     - `DEBUG`: Información detallada para depuración
   - Ejemplo: `"ERROR"`

3. **`stackTrace`** (opcional)
   - Tipo: `string`
   - Descripción: Stack trace completo del error (útil para errores)
   - Ejemplo: 
     ```
     "java.lang.NullPointerException: Cannot invoke method on null object
     at com.example.Service.process(Service.java:42)
     at com.example.Controller.handle(Controller.java:15)"
     ```

4. **`metadata`** (opcional)
   - Tipo: `string` (JSON serializado)
   - Descripción: Información adicional en formato JSON que puede ser útil para contexto
   - Ejemplo: 
     ```json
     "{\"userId\": 12345, \"requestId\": \"abc-123-def\", \"ipAddress\": \"192.168.1.1\", \"userAgent\": \"Mozilla/5.0...\"}"
     ```

### Ejemplo de Request Completo

```json
POST /logs HTTP/1.1
Host: api.tudominio.com
Content-Type: application/json
X-Application-Key: a1b2c3d4-e5f6-7890-abcd-ef1234567890

{
  "mensaje": "Error al procesar solicitud de pago para usuario 12345",
  "nivel": "ERROR",
  "stackTrace": "java.lang.RuntimeException: Payment gateway timeout\n  at com.example.PaymentService.process(PaymentService.java:89)\n  at com.example.OrderController.create(OrderController.java:42)",
  "metadata": "{\"userId\": 12345, \"orderId\": \"ORD-789\", \"amount\": 99.99, \"paymentMethod\": \"credit_card\"}"
}
```

## Respuestas del Servidor

### Respuesta Exitosa (201 Created)

```json
{
  "data": {
    "id": 123,
    "timestamp": "2024-01-15T10:30:00",
    "nivel": "ERROR",
    "mensaje": "Error al procesar solicitud de pago para usuario 12345",
    "stackTrace": "java.lang.RuntimeException: ...",
    "metadata": "{\"userId\": 12345, ...}",
    "applicationId": 1,
    "applicationName": "mi-aplicacion"
  },
  "message": "Log creado exitosamente",
  "errors": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Respuestas de Error

- **400 Bad Request**: Error de validación (datos faltantes o inválidos)
- **401 Unauthorized**: Header `X-Application-Key` ausente o inválido
- **500 Internal Server Error**: Error interno del servidor

## Implementación Recomendada

### ⚠️ CRÍTICO: Manejo Asíncrono y No Bloqueante

**IMPORTANTE**: La implementación debe ser **completamente asíncrona** y **no bloqueante**. Esto significa:

1. **No esperar la respuesta**: El registro de logs NO debe detener el flujo normal de la aplicación
2. **No bloquear en errores**: Si falla el registro de logs, la aplicación debe continuar normalmente
3. **Implementación en background**: Usar un sistema de cola, thread pool, o función asíncrona para enviar los logs
4. **Manejo de errores silencioso**: Los errores al registrar logs deben ser manejados sin afectar al usuario

### Principios de Implementación

1. **Fire and Forget**: Enviar la petición y continuar inmediatamente, sin esperar respuesta
2. **Tolerancia a fallos**: Si el servicio de logs está caído, la aplicación debe funcionar normalmente
3. **No crítico para el negocio**: El registro de logs es una funcionalidad secundaria que no debe impactar la operación principal

### Ejemplo de Flujo

```
1. Ocurre un error en la aplicación
2. Se captura el error y se crea el objeto LogRegisterRequestDto
3. Se envía la petición HTTP de forma ASÍNCRONA (fire and forget)
4. La aplicación continúa con su flujo normal INMEDIATAMENTE
5. Si la petición falla (timeout, error de red, etc.), se registra silenciosamente y se continúa
```

## Ejemplos de Implementación por Lenguaje

### JavaScript/TypeScript (Node.js)

```javascript
// config/logs-client.js
const axios = require('axios');

const LOGS_API_BASE_URL = process.env.LOGS_API_BASE_URL;
const LOGS_API_PRIVATE_KEY = process.env.LOGS_API_PRIVATE_KEY;

/**
 * Registra un log de forma asíncrona (fire and forget)
 * No espera respuesta ni bloquea el flujo principal
 */
async function registerLog(mensaje, nivel, stackTrace = null, metadata = null) {
  // Validar configuración
  if (!LOGS_API_BASE_URL || !LOGS_API_PRIVATE_KEY) {
    console.warn('LOGS_API no configurado. Log no enviado:', mensaje);
    return;
  }

  const logData = {
    mensaje: mensaje.substring(0, 5000), // Limitar a 5000 caracteres
    nivel: nivel, // ERROR, WARN, INFO, DEBUG
    stackTrace: stackTrace,
    metadata: metadata ? JSON.stringify(metadata) : null
  };

  // Enviar de forma asíncrona sin esperar respuesta
  axios.post(`${LOGS_API_BASE_URL}/logs`, logData, {
    headers: {
      'Content-Type': 'application/json',
      'X-Application-Key': LOGS_API_PRIVATE_KEY
    },
    timeout: 5000 // Timeout de 5 segundos
  }).catch(error => {
    // Manejar error silenciosamente - no afectar el flujo principal
    console.error('Error al registrar log (no crítico):', error.message);
  });
}

// Uso en catch de errores
try {
  // Tu código aquí
  throw new Error('Error de ejemplo');
} catch (error) {
  // Registrar log de forma asíncrona
  registerLog(
    error.message,
    'ERROR',
    error.stack,
    { userId: 123, requestId: 'req-456' }
  );
  
  // Continuar con el manejo normal del error
  // NO esperar la respuesta del registro de logs
}
```

### Python

```python
import os
import json
import requests
from threading import Thread
from functools import wraps

LOGS_API_BASE_URL = os.getenv('LOGS_API_BASE_URL')
LOGS_API_PRIVATE_KEY = os.getenv('LOGS_API_PRIVATE_KEY')

def register_log_async(mensaje, nivel, stack_trace=None, metadata=None):
    """
    Registra un log de forma asíncrona (fire and forget)
    No espera respuesta ni bloquea el flujo principal
    """
    if not LOGS_API_BASE_URL or not LOGS_API_PRIVATE_KEY:
        print(f'LOGS_API no configurado. Log no enviado: {mensaje}')
        return
    
    log_data = {
        'mensaje': mensaje[:5000],  # Limitar a 5000 caracteres
        'nivel': nivel,  # ERROR, WARN, INFO, DEBUG
        'stackTrace': stack_trace,
        'metadata': json.dumps(metadata) if metadata else None
    }
    
    headers = {
        'Content-Type': 'application/json',
        'X-Application-Key': LOGS_API_PRIVATE_KEY
    }
    
    def send_log():
        try:
            requests.post(
                f'{LOGS_API_BASE_URL}/logs',
                json=log_data,
                headers=headers,
                timeout=5  # Timeout de 5 segundos
            )
        except Exception as e:
            # Manejar error silenciosamente - no afectar el flujo principal
            print(f'Error al registrar log (no crítico): {str(e)}')
    
    # Ejecutar en thread separado (fire and forget)
    thread = Thread(target=send_log)
    thread.daemon = True
    thread.start()

# Uso en manejo de excepciones
try:
    # Tu código aquí
    raise ValueError('Error de ejemplo')
except Exception as e:
    # Registrar log de forma asíncrona
    import traceback
    register_log_async(
        str(e),
        'ERROR',
        traceback.format_exc(),
        {'userId': 123, 'requestId': 'req-456'}
    )
    
    # Continuar con el manejo normal del error
    # NO esperar la respuesta del registro de logs
```

### Java (Spring Boot)

```java
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class LogsClient {
    
    @Value("${logs.api.base.url}")
    private String logsApiBaseUrl;
    
    @Value("${logs.api.private.key}")
    private String logsApiPrivateKey;
    
    private final RestTemplate restTemplate;
    
    public LogsClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    /**
     * Registra un log de forma asíncrona (fire and forget)
     * No espera respuesta ni bloquea el flujo principal
     */
    @Async
    public void registerLog(String mensaje, String nivel, String stackTrace, Map<String, Object> metadata) {
        if (logsApiBaseUrl == null || logsApiPrivateKey == null) {
            System.out.println("LOGS_API no configurado. Log no enviado: " + mensaje);
            return;
        }
        
        try {
            Map<String, Object> logData = new HashMap<>();
            logData.put("mensaje", mensaje.length() > 5000 ? mensaje.substring(0, 5000) : mensaje);
            logData.put("nivel", nivel); // ERROR, WARN, INFO, DEBUG
            logData.put("stackTrace", stackTrace);
            logData.put("metadata", metadata != null ? new ObjectMapper().writeValueAsString(metadata) : null);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Application-Key", logsApiPrivateKey);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(logData, headers);
            
            restTemplate.postForObject(
                logsApiBaseUrl + "/logs",
                request,
                Object.class
            );
        } catch (Exception e) {
            // Manejar error silenciosamente - no afectar el flujo principal
            System.err.println("Error al registrar log (no crítico): " + e.getMessage());
        }
    }
}

// Uso en manejo de excepciones
@Autowired
private LogsClient logsClient;

try {
    // Tu código aquí
    throw new RuntimeException("Error de ejemplo");
} catch (Exception e) {
    // Registrar log de forma asíncrona
    Map<String, Object> metadata = new HashMap<>();
    metadata.put("userId", 123);
    metadata.put("requestId", "req-456");
    
    logsClient.registerLog(
        e.getMessage(),
        "ERROR",
        getStackTrace(e),
        metadata
    );
    
    // Continuar con el manejo normal del error
    // NO esperar la respuesta del registro de logs
}
```

## Checklist de Implementación

- [ ] Variables de entorno configuradas (`LOGS_API_BASE_URL` y `LOGS_API_PRIVATE_KEY`)
- [ ] Implementación asíncrona (fire and forget)
- [ ] Manejo de errores silencioso (no afecta flujo principal)
- [ ] Timeout configurado (máximo 5 segundos)
- [ ] Validación de mensaje (máximo 5000 caracteres)
- [ ] Validación de nivel (ERROR, WARN, INFO, DEBUG)
- [ ] Metadata serializada como JSON string
- [ ] Headers correctos (`Content-Type` y `X-Application-Key`)
- [ ] No bloquear en caso de error de red o timeout
- [ ] Logs de depuración opcionales (solo para troubleshooting)

## Notas Importantes

1. **Private Key**: Es sensible y debe mantenerse secreta. Nunca exponerla en código, logs, o repositorios públicos.

2. **Registro de aplicación**: Antes de usar el endpoint, la aplicación debe estar registrada en el sistema mediante `/applications` (requiere rol ROLE_DEV).

3. **Rate Limiting**: Considerar implementar rate limiting local para evitar sobrecargar el servidor de logs.

4. **Límites de tamaño**: 
   - Mensaje: máximo 5000 caracteres
   - Stack trace: sin límite técnico, pero se recomienda truncar si es muy largo
   - Metadata: JSON string, usar con moderación

5. **Ambiente**: Asegurarse de que `LOGS_API_BASE_URL` apunte al ambiente correcto (desarrollo, producción).

## Soporte

Para obtener el `privateKey` de tu aplicación o resolver dudas sobre la implementación, contactar al administrador del sistema o revisar la documentación en Swagger UI: `{LOGS_API_BASE_URL}/swagger-ui.html`

