package trinity.play2learn.backend.configs.logs.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.configs.logs.models.LogLevel;
import trinity.play2learn.backend.configs.messages.ValidationMessages;

/**
 * DTO para registrar logs desde sistemas externos hacia el Backend Logs API.
 * 
 * <p>Este DTO se utiliza para enviar información de logs de forma asíncrona y no bloqueante
 * mediante una implementación fire-and-forget. El servicio que procesa este DTO no debe
 * bloquear el flujo principal de la aplicación.</p>
 * 
 * <p>Los campos requeridos son: {@code mensaje} y {@code nivel}. Los campos opcionales
 * son: {@code stackTrace} y {@code metadata}.</p>
 * 
 * @see trinity.play2learn.backend.configs.logs.models.LogLevel
 * @see trinity.play2learn.backend.configs.logs.services.LogsClientService
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogRegisterRequestDto {
    
    /**
     * Mensaje descriptivo del log o error.
     * 
     * <p>Este campo es requerido y debe contener una descripción clara y concisa
     * del evento que se está registrando. El mensaje no puede estar vacío ni
     * exceder los 5000 caracteres.</p>
     * 
     * <p>Ejemplo: "Error al procesar solicitud de pago para usuario 12345"</p>
     */
    @NotBlank(message = ValidationMessages.NOT_EMPTY_MESSAGE)
    @Size(max = 5000, message = ValidationMessages.MAX_LENGTH_MESSAGE_5000)
    private String mensaje;
    
    /**
     * Nivel de severidad del log.
     * 
     * <p>Este campo es requerido y debe ser uno de los valores del enum {@link LogLevel}:
     * <ul>
     *   <li>{@code ERROR}: Errores críticos que requieren atención inmediata</li>
     *   <li>{@code WARN}: Advertencias que pueden indicar problemas potenciales</li>
     *   <li>{@code INFO}: Información general del flujo de la aplicación</li>
     *   <li>{@code DEBUG}: Información detallada para depuración</li>
     * </ul>
     * </p>
     */
    @NotNull(message = ValidationMessages.NOT_NULL_LOG_LEVEL)
    private LogLevel nivel;
    
    /**
     * Stack trace completo del error.
     * 
     * <p>Este campo es opcional y se utiliza principalmente cuando el nivel del log
     * es {@code ERROR}. Debe contener el stack trace completo de la excepción que
     * generó el error, incluyendo las trazas de todas las llamadas de métodos.</p>
     * 
     * <p>Ejemplo:
     * <pre>
     * "java.lang.RuntimeException: Payment gateway timeout
     *   at com.example.PaymentService.process(PaymentService.java:89)
     *   at com.example.OrderController.create(OrderController.java:42)"
     * </pre>
     * </p>
     */
    private String stackTrace;
    
    /**
     * Información adicional en formato JSON serializado.
     * 
     * <p>Este campo es opcional y permite incluir información contextual adicional
     * relevante para el log. El valor debe ser una cadena JSON válida si se proporciona.</p>
     * 
     * <p>Ejemplo:
     * <pre>
     * "{\"userId\": 12345, \"orderId\": \"ORD-789\", \"amount\": 99.99, \"paymentMethod\": \"credit_card\"}"
     * </pre>
     * </p>
     */
    private String metadata;
}

