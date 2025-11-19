package trinity.play2learn.backend.configs.logs.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import trinity.play2learn.backend.configs.logs.dtos.LogRegisterRequestDto;
import trinity.play2learn.backend.configs.logs.models.LogLevel;

import java.util.Map;

/**
 * Servicio cliente para registrar logs de forma asíncrona en el Backend Logs API.
 * 
 * <p>Este servicio implementa un patrón <strong>fire-and-forget</strong> para registrar logs desde
 * sistemas externos sin bloquear el flujo principal de la aplicación. Las operaciones
 * se ejecutan de forma completamente asíncrona usando {@code @Async("taskExecutor")}.</p>
 * 
 * <p><strong>Características principales:</strong>
 * <ul>
 *   <li><strong>Registro asíncrono no bloqueante:</strong> El método {@code registerLog} retorna
 *       inmediatamente sin esperar la respuesta del servidor de logs.</li>
 *   <li><strong>Manejo silencioso de errores:</strong> Todos los errores (timeout, red, servidor)
 *       se capturan y registran en logs sin propagarse al thread principal.</li>
 *   <li><strong>Timeout automático:</strong> Los timeouts se manejan mediante la configuración
 *       asíncrona y el executor, evitando bloqueos indefinidos.</li>
 *   <li><strong>Validación de configuración:</strong> Verifica que las variables de entorno
 *       estén configuradas antes de intentar enviar logs.</li>
 *   <li><strong>Truncamiento automático:</strong> Los mensajes se truncan automáticamente a
 *       5000 caracteres si exceden ese límite.</li>
 *   <li><strong>Serialización automática:</strong> La metadata se serializa automáticamente
 *       a JSON string antes de enviarla.</li>
 * </ul>
 * </p>
 * 
 * <p><strong>Comportamiento fire-and-forget:</strong></p>
 * <p>Cuando se llama a {@code registerLog}, el método retorna inmediatamente y la petición HTTP
 * se ejecuta en un thread separado del pool configurado en {@code AsyncConfig}. Si el servicio
 * de logs está caído, hay problemas de red, o ocurre cualquier error, la aplicación continúa
 * funcionando normalmente. Los errores se registran silenciosamente en los logs para troubleshooting
 * sin afectar al usuario final.</p>
 * 
 * <p><strong>Ejemplo de uso:</strong></p>
 * <pre>
 * {@code
 * logsClientService.registerLog(
 *     "Error al procesar solicitud de pago",
 *     LogLevel.ERROR,
 *     exception.getStackTrace(),
 *     Map.of("userId", 123, "orderId", "ORD-789")
 * );
 * // La aplicación continúa inmediatamente sin esperar la respuesta
 * }</pre>
 * 
 * @see trinity.play2learn.backend.configs.logs.models.LogLevel
 * @see trinity.play2learn.backend.configs.logs.dtos.LogRegisterRequestDto
 * @see trinity.play2learn.backend.configs.async.AsyncConfig
 * @see org.springframework.scheduling.annotation.Async
 */
@Service
public class LogsClientService {

    private static final Logger logger = LoggerFactory.getLogger(LogsClientService.class);
    private static final int MAX_MESSAGE_LENGTH = 5000;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${logs.api.base.url}")
    private String logsApiBaseUrl;

    @Value("${logs.api.private.key}")
    private String logsApiPrivateKey;

    /**
     * Constructor por defecto que inicializa RestTemplate y ObjectMapper.
     * 
     * <p>RestTemplate y ObjectMapper se crean directamente siguiendo el patrón
     * del proyecto. En el futuro, estos podrían ser beans inyectados si se
     * requiere configuración compartida.</p>
     */
    public LogsClientService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Registra un log de forma asíncrona (fire-and-forget).
     * 
     * <p><strong>Comportamiento asíncrono:</strong> Este método está anotado con {@code @Async("taskExecutor")},
     * lo que significa que se ejecuta en un thread separado del pool configurado en {@code AsyncConfig}.
     * El método retorna inmediatamente sin esperar la respuesta del servidor de logs.</p>
     * 
     * <p><strong>Manejo de errores:</strong> Todos los errores (configuración faltante, timeout, errores de red,
     * errores del servidor) se capturan y registran en logs sin propagarse al thread principal. La aplicación
     * continúa funcionando normalmente incluso si el servicio de logs está caído.</p>
     * 
     * <p><strong>Validaciones automáticas:</strong>
     * <ul>
     *   <li>Valida que la configuración esté disponible antes de enviar</li>
     *   <li>Trunca el mensaje a 5000 caracteres si excede el límite</li>
     *   <li>Serializa la metadata a JSON string automáticamente</li>
     * </ul>
     * </p>
     * 
     * @param mensaje Mensaje descriptivo del log o error. Si es null, se maneja apropiadamente.
     *                Se trunca automáticamente a 5000 caracteres si excede ese límite.
     * @param nivel Nivel de severidad del log. Debe ser uno de: ERROR, WARN, INFO, DEBUG.
     *              Si es null, el registro falla silenciosamente.
     * @param stackTrace Stack trace completo del error. Puede ser null si no hay excepción.
     * @param metadata Información adicional en formato Map que se serializará automáticamente
     *                 a JSON string. Puede ser null o vacío.
     */
    @Async("taskExecutor")
    public void registerLog(String mensaje, LogLevel nivel, String stackTrace, Map<String, Object> metadata) {
        try {
            // Validar configuración antes de procesar
            if (!isConfigurationValid()) {
                logger.warn("LOGS_API no configurado. Log no enviado. Mensaje: {}", 
                    mensaje != null ? mensaje.substring(0, Math.min(100, mensaje.length())) : "null");
                return;
            }

            // Validar que mensaje y nivel no sean null (requeridos por la API)
            if (mensaje == null || nivel == null) {
                logger.warn("Log no enviado: mensaje o nivel son null. Mensaje: {}, Nivel: {}", mensaje, nivel);
                return;
            }

            // Truncar mensaje si excede el límite
            String truncatedMessage = truncateMessage(mensaje);

            // Serializar metadata a JSON string si existe
            String metadataJson = serializeMetadata(metadata);

            // Construir DTO
            LogRegisterRequestDto logDto = LogRegisterRequestDto.builder()
                    .mensaje(truncatedMessage)
                    .nivel(nivel)
                    .stackTrace(stackTrace)
                    .metadata(metadataJson)
                    .build();

            // Enviar petición HTTP de forma asíncrona (fire-and-forget)
            sendLogRequest(logDto);

        } catch (Exception e) {
            // Manejar error silenciosamente - no afectar el flujo principal
            // Los errores se registran con nivel ERROR pero no se propagan
            logger.error("Error al registrar log al Backend Logs API (no crítico). " +
                    "Mensaje: {}, Nivel: {}. Error: {}", 
                    mensaje != null ? mensaje.substring(0, Math.min(100, mensaje.length())) : "null",
                    nivel,
                    e.getMessage(),
                    e);
        }
    }

    /**
     * Valida que la configuración requerida esté disponible.
     * 
     * <p>Verifica que tanto {@code logsApiBaseUrl} como {@code logsApiPrivateKey}
     * estén configurados y no estén vacíos.</p>
     * 
     * @return true si la configuración es válida (ambas variables no null y no vacías),
     *         false en caso contrario
     */
    private boolean isConfigurationValid() {
        return logsApiBaseUrl != null && !logsApiBaseUrl.isBlank() &&
               logsApiPrivateKey != null && !logsApiPrivateKey.isBlank();
    }

    /**
     * Trunca el mensaje a la longitud máxima permitida por la API.
     * 
     * <p>Si el mensaje excede {@code MAX_MESSAGE_LENGTH} (5000 caracteres), se trunca
     * y se registra un warning en los logs. Si el mensaje es null, se retorna null
     * sin modificación.</p>
     * 
     * @param mensaje Mensaje original que puede exceder el límite máximo
     * @return Mensaje truncado a 5000 caracteres si excede el límite, mensaje original
     *         si está dentro del límite, o null si el mensaje original era null
     */
    private String truncateMessage(String mensaje) {
        if (mensaje == null) {
            return null;
        }
        if (mensaje.length() > MAX_MESSAGE_LENGTH) {
            logger.warn("Mensaje de log truncado de {} a {} caracteres. Primeros 100 caracteres: {}", 
                mensaje.length(), MAX_MESSAGE_LENGTH, mensaje.substring(0, Math.min(100, mensaje.length())));
            return mensaje.substring(0, MAX_MESSAGE_LENGTH);
        }
        return mensaje;
    }

    /**
     * Serializa la metadata a JSON string usando ObjectMapper.
     * 
     * <p>Si la metadata es null o vacía, retorna null. Si ocurre un error durante
     * la serialización (por ejemplo, objetos no serializables), se registra un warning
     * y se retorna null en lugar de fallar.</p>
     * 
     * @param metadata Map con información adicional que se serializará a JSON.
     *                 Puede ser null o vacío.
     * @return JSON string serializado, o null si metadata es null, vacío, o si
     *         ocurre un error durante la serialización
     */
    private String serializeMetadata(Map<String, Object> metadata) {
        if (metadata == null || metadata.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(metadata);
        } catch (Exception e) {
            logger.warn("Error al serializar metadata a JSON. Metadata será omitida. Error: {}", 
                e.getMessage(), e);
            return null;
        }
    }

    /**
     * Envía la petición HTTP POST al Backend Logs API.
     * 
     * <p>Este método construye la URL, configura los headers HTTP requeridos
     * (Content-Type y X-Application-Key) y envía la petición POST usando RestTemplate.</p>
     * 
     * <p>Los errores HTTP (4xx, 5xx) y errores de red (timeout, conexión) se capturan
     * y se propagan como excepciones para ser manejadas en el método principal.</p>
     * 
     * @param logDto DTO con la información del log a registrar. No debe ser null.
     * @throws RestClientResponseException Si el servidor responde con un código de error HTTP (4xx, 5xx)
     * @throws ResourceAccessException Si hay problemas de red o timeout durante la conexión
     * @throws RestClientException Para otros errores relacionados con RestTemplate
     */
    private void sendLogRequest(LogRegisterRequestDto logDto) {
        String url = logsApiBaseUrl + "/logs";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Application-Key", logsApiPrivateKey);

        HttpEntity<LogRegisterRequestDto> request = new HttpEntity<>(logDto, headers);

        try {
            ResponseEntity<Void> response = restTemplate.postForEntity(url, request, Void.class);
            
            // Log exitoso solo en modo DEBUG para no saturar logs
            if (logger.isDebugEnabled()) {
                logger.debug("Log registrado exitosamente. Nivel: {}, Status: {}", 
                    logDto.getNivel(), response.getStatusCode());
            }

        } catch (RestClientResponseException e) {
            // Error de respuesta HTTP (4xx, 5xx)
            HttpStatusCode statusCode = e.getStatusCode();
            logger.warn("Error HTTP al registrar log. Status: {}, Mensaje: {}, Cuerpo: {}", 
                statusCode, e.getMessage(), e.getResponseBodyAsString());
            throw e;

        } catch (ResourceAccessException e) {
            // Error de red o timeout
            logger.warn("Error de red o timeout al registrar log. URL: {}, Error: {}", 
                url, e.getMessage());
            throw e;

        } catch (RestClientException e) {
            // Otros errores de RestTemplate
            logger.warn("Error de RestTemplate al registrar log. URL: {}, Error: {}", 
                url, e.getMessage());
            throw e;
        }
    }

}

