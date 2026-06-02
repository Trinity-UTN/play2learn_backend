package trinity.play2learn.backend.configs.logs.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import trinity.play2learn.backend.configs.logs.models.LogLevel;
import trinity.play2learn.backend.configs.logs.services.LogsClientService;

import java.util.HashMap;
import java.util.Map;

/**
 * Interceptor HTTP que registra automáticamente logs con nivel INFO cuando ocurren
 * peticiones HTTP exitosas (códigos de estado 2xx).
 * <p>
 * Este interceptor se ejecuta después de que el controlador procesa la petición
 * exitosamente y registra información relevante sobre la petición en el sistema
 * externo de logs mediante {@link LogsClientService}.
 * <p>
 * <strong>Características principales:</strong>
 * <ul>
 *   <li><strong>Registro automático:</strong> Intercepta todas las peticiones exitosas
 *       sin necesidad de código adicional en los controladores.</li>
 *   <li><strong>Asíncrono:</strong> El registro se realiza de forma asíncrona
 *       (fire-and-forget) para no afectar el rendimiento de las respuestas HTTP.</li>
 *   <li><strong>Información completa:</strong> Registra URL, método HTTP, código de estado,
 *       tiempo de respuesta, y metadata adicional.</li>
 *   <li><strong>Filtrado inteligente:</strong> Solo registra peticiones exitosas (2xx)
 *       y evita duplicados cuando hay errores.</li>
 * </ul>
 * <p>
 * <strong>Información registrada:</strong>
 * <ul>
 *   <li>URL completa de la petición</li>
 *   <li>Método HTTP (GET, POST, PUT, DELETE, etc.)</li>
 *   <li>Código de estado HTTP (200, 201, 204, etc.)</li>
 *   <li>Tiempo de respuesta en milisegundos</li>
 *   <li>Query parameters (si existen)</li>
 * </ul>
 * <p>
 * <strong>Ejemplo de log registrado:</strong>
 * <pre>
 * {
 *   "mensaje": "Petición exitosa: GET /api/users/123",
 *   "nivel": "INFO",
 *   "metadata": {
 *     "url": "/api/users/123",
 *     "method": "GET",
 *     "statusCode": 200,
 *     "responseTimeMs": 45,
 *     "queryParams": "?page=1"
 *   }
 * }
 * </pre>
 *
 * @see LogsClientService
 * @see LogLevel
 * @see org.springframework.web.servlet.HandlerInterceptor
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class SuccessfulRequestLoggingInterceptor implements HandlerInterceptor {

    private static final String START_TIME_ATTRIBUTE = "requestStartTime";
    private static final int SUCCESS_STATUS_CODE_MIN = 200;
    private static final int SUCCESS_STATUS_CODE_MAX = 299;

    private final LogsClientService logsClientService;

    /**
     * Guarda el tiempo de inicio de la petición para calcular el tiempo de respuesta
     * en {@code afterCompletion}.
     *
     * @param request  la petición HTTP
     * @param response la respuesta HTTP
     * @param handler  el handler que procesará la petición
     * @return siempre {@code true} para continuar con el procesamiento
     */
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        // Guardar tiempo de inicio para calcular tiempo de respuesta
        request.setAttribute(START_TIME_ATTRIBUTE, System.currentTimeMillis());
        return true;
    }

    /**
     * Registra un log con nivel INFO cuando la petición se completa exitosamente
     * (código de estado 2xx) y no hubo excepciones.
     * <p>
     * Este método se ejecuta después de que el controlador procesa la petición,
     * incluso si hubo una excepción. Por lo tanto, verifica que:
     * <ul>
     *   <li>El código de estado sea 2xx (éxito)</li>
     *   <li>No haya excepciones (el parámetro {@code ex} sea null)</li>
     * </ul>
     *
     * @param request   la petición HTTP
     * @param response  la respuesta HTTP
     * @param handler   el handler que procesó la petición
     * @param ex        la excepción que ocurrió (si hubo alguna), o null si la petición fue exitosa
     */
    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, @Nullable Exception ex) {
        // Solo registrar logs para peticiones exitosas sin excepciones
        if (ex != null) {
            return; // Las excepciones ya se registran en GlobalExceptionHandler
        }

        int statusCode = response.getStatus();
        if (!isSuccessStatusCode(statusCode)) {
            return; // Solo registrar peticiones exitosas (2xx)
        }

        // Calcular tiempo de respuesta
        Long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
        long responseTime = startTime != null 
                ? System.currentTimeMillis() - startTime 
                : 0;

        // Construir mensaje descriptivo
        String message = buildLogMessage(request, statusCode);

        // Construir metadata con información relevante
        Map<String, Object> metadata = buildMetadata(request, statusCode, responseTime);

        // Registrar log de forma asíncrona (fire-and-forget)
        logsClientService.registerLog(
                message,
                LogLevel.INFO,
                null, // No hay stack trace para peticiones exitosas
                metadata
        );
    }

    /**
     * Verifica si el código de estado HTTP indica una petición exitosa (2xx).
     *
     * @param statusCode el código de estado HTTP
     * @return {@code true} si el código está en el rango 200-299, {@code false} en caso contrario
     */
    private boolean isSuccessStatusCode(int statusCode) {
        return statusCode >= SUCCESS_STATUS_CODE_MIN && statusCode <= SUCCESS_STATUS_CODE_MAX;
    }

    /**
     * Construye el mensaje descriptivo del log basado en la petición y el código de estado.
     * <p>
     * El mensaje sigue el formato: "Petición exitosa: {METHOD} {URI}?{QUERY} (Status: {CODE})"
     * <p>
     * Ejemplos:
     * <ul>
     *   <li>"Petición exitosa: GET /api/users/123 (Status: 200)"</li>
     *   <li>"Petición exitosa: POST /api/orders?page=1 (Status: 201)"</li>
     * </ul>
     *
     * @param request   la petición HTTP (no debe ser null)
     * @param statusCode el código de estado HTTP
     * @return el mensaje descriptivo del log, nunca null
     */
    private String buildLogMessage(@NonNull HttpServletRequest request, int statusCode) {
        String method = request.getMethod();
        String requestURI = request.getRequestURI();
        String queryString = request.getQueryString();
        
        StringBuilder message = new StringBuilder("Petición exitosa: ");
        
        // Método HTTP (con validación de null)
        if (method != null && !method.isEmpty()) {
            message.append(method);
        } else {
            message.append("UNKNOWN");
        }
        
        message.append(" ");
        
        // URI (con validación de null)
        if (requestURI != null && !requestURI.isEmpty()) {
            message.append(requestURI);
        } else {
            message.append("/");
        }
        
        // Query parameters (si existen)
        if (queryString != null && !queryString.isEmpty()) {
            message.append("?").append(queryString);
        }
        
        message.append(" (Status: ").append(statusCode).append(")");
        
        return message.toString();
    }

    /**
     * Construye la metadata con información relevante de la petición.
     * <p>
     * Este método extrae información relevante de la petición HTTP y la organiza
     * en un mapa para ser incluida en el log. La información incluye:
     * <ul>
     *   <li>URL de la petición</li>
     *   <li>Método HTTP</li>
     *   <li>Código de estado</li>
     *   <li>Tiempo de respuesta</li>
     *   <li>Query parameters (si existen)</li>
     *   <li>User-Agent (si está disponible)</li>
     * </ul>
     *
     * @param request     la petición HTTP (no debe ser null)
     * @param statusCode  el código de estado HTTP
     * @param responseTime el tiempo de respuesta en milisegundos
     * @return un mapa con la metadata de la petición, nunca null
     */
    private Map<String, Object> buildMetadata(@NonNull HttpServletRequest request, int statusCode, long responseTime) {
        Map<String, Object> metadata = new HashMap<>();
        
        // Información básica de la petición (siempre presente)
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        
        metadata.put("url", requestURI != null ? requestURI : "");
        metadata.put("method", method != null ? method : "UNKNOWN");
        metadata.put("statusCode", statusCode);
        metadata.put("responseTimeMs", responseTime);
        
        // Query parameters (si existen)
        String queryString = request.getQueryString();
        if (queryString != null && !queryString.isEmpty()) {
            metadata.put("queryParams", queryString);
        }
        
        // Headers relevantes (opcional, si es necesario)
        String userAgent = request.getHeader("User-Agent");
        if (userAgent != null && !userAgent.isEmpty()) {
            metadata.put("userAgent", userAgent);
        }
        
        // Información del cliente (IP remota)
        String remoteAddr = request.getRemoteAddr();
        if (remoteAddr != null && !remoteAddr.isEmpty()) {
            metadata.put("remoteAddress", remoteAddr);
        }
        
        return metadata;
    }
}

