package trinity.play2learn.backend.configs.exceptions;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.RequiredArgsConstructor;
import trinity.play2learn.backend.configs.logs.models.LogLevel;
import trinity.play2learn.backend.configs.logs.services.LogsClientService;
import trinity.play2learn.backend.configs.response.BaseResponse;

/**
 * Manejador global de excepciones que intercepta todas las excepciones no manejadas
 * en los controladores REST y registra automáticamente los logs en el sistema externo
 * de registro de logs.
 * <p>
 * Este handler se encarga de:
 * <ul>
 *   <li>Capturar excepciones personalizadas ({@link CustomException})</li>
 *   <li>Capturar errores de validación ({@link MethodArgumentNotValidException})</li>
 *   <li>Capturar cualquier otra excepción no manejada ({@link Exception})</li>
 *   <li>Registrar automáticamente los logs en el sistema externo mediante {@link LogsClientService}</li>
 * </ul>
 * <p>
 * El registro de logs se realiza de forma asíncrona (fire-and-forget) para no afectar
 * el rendimiento de las respuestas HTTP.
 *
 * @author Trinity Play2Learn Backend Team
 * @since 1.0.0
 */
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final LogsClientService logsClientService;

    /**
     * Maneja excepciones personalizadas ({@link CustomException}).
     * Registra automáticamente el log con el nivel apropiado según el código HTTP:
     * <ul>
     *   <li>{@link LogLevel#ERROR} para códigos 5xx (errores del servidor)</li>
     *   <li>{@link LogLevel#WARN} para códigos 4xx (errores del cliente)</li>
     * </ul>
     *
     * @param ex la excepción personalizada
     * @return respuesta HTTP con formato {@link BaseResponse}
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<BaseResponse<Object>> handleCustomException(CustomException ex) {
        BaseResponse<Object> response = BaseResponse.<Object>builder()
                .data(null)
                .message(ex.getMessage())
                .errors(ex.getErrors())
                .timestamp(getCurrentTimestamp())
                .build();

        // Determinar nivel de log según el código HTTP
        LogLevel logLevel = ex.getStatus().is5xxServerError() 
                ? LogLevel.ERROR 
                : LogLevel.WARN;

        // Registrar log de forma asíncrona
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("statusCode", ex.getStatus().value());
        metadata.put("errors", ex.getErrors());
        metadata.put("exceptionType", ex.getClass().getSimpleName());

        logsClientService.registerLog(
                ex.getMessage(),
                logLevel,
                getStackTrace(ex),
                metadata
        );

        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    /**
     * Maneja errores de validación ({@link MethodArgumentNotValidException}).
     * Registra automáticamente el log con nivel {@link LogLevel#WARN} ya que son
     * errores del cliente (código 400).
     *
     * @param ex la excepción de validación
     * @return respuesta HTTP con formato {@link BaseResponse} y código 400
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Object>> handleValidationException(MethodArgumentNotValidException ex) {
        // Mapeamos los errores campo -> mensaje
        var errors = new HashMap<String, String>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        List<String> errorMessages = errors.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .toList();

        // Armamos la respuesta con BaseResponse
        BaseResponse<Object> response = BaseResponse.<Object>builder()
                .data(null)
                .message("Error de validación")
                .errors(errorMessages)
                .timestamp(getCurrentTimestamp())
                .build();

        // Registrar log de forma asíncrona
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("statusCode", HttpStatus.BAD_REQUEST.value());
        metadata.put("validationErrors", errors);
        metadata.put("exceptionType", ex.getClass().getSimpleName());

        logsClientService.registerLog(
                "Error de validación: " + String.join(", ", errorMessages),
                LogLevel.WARN,
                getStackTrace(ex),
                metadata
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Maneja cualquier excepción no manejada por los handlers específicos.
     * Este es el handler de último recurso que captura todas las excepciones no esperadas.
     * Registra automáticamente el log con nivel {@link LogLevel#ERROR}.
     *
     * @param ex la excepción no manejada
     * @return respuesta HTTP con formato {@link BaseResponse} y código 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Object>> handleGenericException(Exception ex) {
        BaseResponse<Object> response = BaseResponse.<Object>builder()
                .data(null)
                .message("Error interno del servidor")
                .errors(List.of(ex.getMessage() != null ? ex.getMessage() : "Error desconocido"))
                .timestamp(getCurrentTimestamp())
                .build();

        // Registrar log de forma asíncrona
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
        metadata.put("exceptionType", ex.getClass().getSimpleName());
        metadata.put("exceptionMessage", ex.getMessage());

        logsClientService.registerLog(
                "Error interno del servidor: " + ex.getMessage(),
                LogLevel.ERROR,
                getStackTrace(ex),
                metadata
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Obtiene el stack trace completo de una excepción como String.
     *
     * @param ex la excepción
     * @return el stack trace como String, o null si la excepción es null
     */
    private String getStackTrace(Exception ex) {
        if (ex == null) {
            return null;
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        return sw.toString();
    }

    /**
     * Obtiene el timestamp actual en formato ISO 8601.
     *
     * @return el timestamp como String en formato ISO 8601
     */
    private String getCurrentTimestamp() {
        return DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC).format(Instant.now());
    }
}
