package trinity.play2learn.backend.configs.logs.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import trinity.play2learn.backend.configs.logs.dtos.LogRegisterRequestDto;
import trinity.play2learn.backend.configs.logs.models.LogLevel;

import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
@DisplayName("LogsClientService Tests")
class LogsClientServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private LogsClientService logsClientService;

    // ========== Fixtures y constantes compartidas ==========
    
    private static final String VALID_BASE_URL = "https://logs-api.example.com";
    private static final String VALID_PRIVATE_KEY = "test-private-key-123";
    private static final String VALID_MESSAGE = "Test error message";
    private static final String VALID_STACK_TRACE = "java.lang.Exception: Test exception\n\tat com.example.Test.main(Test.java:1)";
    private static final String EXPECTED_URL = VALID_BASE_URL + "/logs";
    private static final int MAX_MESSAGE_LENGTH = 5000;
    
    // Metadata de ejemplo para tests
    private static final Map<String, Object> EXAMPLE_METADATA = Map.of(
            "userId", 123,
            "orderId", "ORD-789",
            "amount", 99.99,
            "active", true
    );

    @BeforeEach
    void setUp() {
        // Inyectar RestTemplate mockeado usando ReflectionTestUtils
        ReflectionTestUtils.setField(logsClientService, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(logsClientService, "objectMapper", objectMapper);
        // Configurar valores de configuración válidos
        configureValidSettings();
    }

    // ========== Helper methods reutilizables ==========
    
    /**
     * Configura valores válidos de configuración para los tests.
     * Se usa para resetear la configuración después de tests que la modifican.
     */
    private void configureValidSettings() {
        ReflectionTestUtils.setField(logsClientService, "logsApiBaseUrl", VALID_BASE_URL);
        ReflectionTestUtils.setField(logsClientService, "logsApiPrivateKey", VALID_PRIVATE_KEY);
    }
    
    /**
     * Crea una respuesta HTTP exitosa (200 OK) mockeada.
     * 
     * @return ResponseEntity<Void> con status 200 OK
     */
    private ResponseEntity<Void> createSuccessResponse() {
        return ResponseEntity.status(HttpStatusCode.valueOf(200)).build();
    }
    
    /**
     * Configura el mock de RestTemplate para retornar una respuesta exitosa.
     * Utilizado para simular un envío exitoso de log al Backend Logs API.
     */
    private void mockSuccessfulRestTemplateCall() {
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Void.class)))
                .thenReturn(createSuccessResponse());
    }
    
    /**
     * Crea un mensaje de prueba con la longitud especificada.
     * 
     * @param length Longitud deseada del mensaje
     * @return String con el mensaje generado
     */
    private String createMessageOfLength(int length) {
        StringBuilder message = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            message.append("a");
        }
        return message.toString();
    }
    
    /**
     * Captura y retorna el HttpEntity enviado a RestTemplate.postForEntity.
     * Útil para verificar headers y body del request HTTP.
     * 
     * @return HttpEntity capturado con el DTO de log
     */
    @SuppressWarnings("unchecked")
    private ArgumentCaptor<HttpEntity<LogRegisterRequestDto>> captureHttpEntity() {
        ArgumentCaptor<HttpEntity<LogRegisterRequestDto>> entityCaptor = 
                ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).postForEntity(anyString(), entityCaptor.capture(), eq(Void.class));
        return entityCaptor;
    }
    
    /**
     * Captura tanto la URL como el HttpEntity enviados a RestTemplate.postForEntity.
     * Útil para verificar la URL completa y los detalles del request HTTP.
     * 
     * @return Array con [urlCaptor, entityCaptor]
     */
    @SuppressWarnings("unchecked")
    private Object[] captureUrlAndEntity() {
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpEntity<LogRegisterRequestDto>> entityCaptor = 
                ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate, times(1)).postForEntity(
                urlCaptor.capture(),
                entityCaptor.capture(),
                eq(Void.class)
        );
        return new Object[]{urlCaptor, entityCaptor};
    }
    
    /**
     * Verifica que los headers HTTP contienen los valores esperados.
     * Valida Content-Type: application/json y X-Application-Key.
     * 
     * @param headers Headers HTTP a verificar
     */
    private void verifyHttpHeaders(HttpHeaders headers) {
        assertThat(headers.getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(headers.getFirst("X-Application-Key")).isEqualTo(VALID_PRIVATE_KEY);
    }
    
    /**
     * Crea una RestClientResponseException para simular errores HTTP.
     * 
     * @param statusCode Código de estado HTTP (ej: 400, 500)
     * @param statusText Texto del status HTTP
     * @param errorBody Cuerpo del error en formato JSON
     * @return RestClientResponseException configurada
     */
    private RestClientResponseException createHttpException(int statusCode, 
                                                             String statusText, 
                                                             String errorBody) {
        return new RestClientResponseException(
                statusText,
                statusCode,
                statusText,
                HttpHeaders.EMPTY,
                errorBody.getBytes(),
                null
        );
    }

    @Nested
    @DisplayName("✅ Registro exitoso de log")
    class SuccessfulLogRegistration {

        @Test
        @DisplayName("Given valid log data When registering log Then sends POST request with correct headers and body")
        void whenValidLogData_registersLogSuccessfully() {
            // Given - Datos de log válidos con todos los campos (mensaje, nivel, stackTrace, metadata)
            // Este test verifica que el servicio envía correctamente todos los campos al Backend Logs API
            LogLevel nivel = LogLevel.ERROR;
            String mensaje = VALID_MESSAGE;
            String stackTrace = VALID_STACK_TRACE;
            Map<String, Object> metadata = EXAMPLE_METADATA;

            mockSuccessfulRestTemplateCall();

            // When - Registrar log con todos los campos
            logsClientService.registerLog(mensaje, nivel, stackTrace, metadata);

            // Then - Verificar que se envió la petición con URL, headers y body correctos
            // Validamos que la URL es correcta, los headers HTTP incluyen Content-Type y X-Application-Key,
            // y el body del DTO contiene todos los campos serializados correctamente
            Object[] captures = captureUrlAndEntity();
            @SuppressWarnings("unchecked")
            ArgumentCaptor<String> urlCaptor = (ArgumentCaptor<String>) captures[0];
            @SuppressWarnings("unchecked")
            ArgumentCaptor<HttpEntity<LogRegisterRequestDto>> entityCaptor = 
                    (ArgumentCaptor<HttpEntity<LogRegisterRequestDto>>) captures[1];

            // Verificar URL - debe ser la base URL + /logs
            assertThat(urlCaptor.getValue()).isEqualTo(EXPECTED_URL);

            // Verificar headers HTTP - deben incluir Content-Type y X-Application-Key para autenticación
            HttpHeaders headers = entityCaptor.getValue().getHeaders();
            verifyHttpHeaders(headers);

            // Verificar body del DTO - debe contener todos los campos correctamente serializados
            LogRegisterRequestDto requestDto = entityCaptor.getValue().getBody();
            assertThat(requestDto).isNotNull();
            if (requestDto != null) {
                assertThat(requestDto.getMensaje()).isEqualTo(mensaje);
                assertThat(requestDto.getNivel()).isEqualTo(nivel);
                assertThat(requestDto.getStackTrace()).isEqualTo(stackTrace);
                assertThat(requestDto.getMetadata()).isNotNull();
                // Verificar que la metadata fue serializada a JSON string
                assertThat(requestDto.getMetadata()).contains("\"userId\":123");
                assertThat(requestDto.getMetadata()).contains("\"orderId\":\"ORD-789\"");
            }
        }

        @Test
        @DisplayName("Given log with all levels When registering log Then sends request for each level")
        void whenDifferentLogLevels_registersAllLevelsSuccessfully() {
            // Given - Todos los niveles de log disponibles (ERROR, WARN, INFO, DEBUG)
            // Este test verifica que el servicio puede manejar correctamente todos los niveles de log
            LogLevel[] niveles = {LogLevel.ERROR, LogLevel.WARN, LogLevel.INFO, LogLevel.DEBUG};
            mockSuccessfulRestTemplateCall();

            // When - Registrar log para cada nivel
            // Cada nivel debe ser enviado independientemente al Backend Logs API
            for (LogLevel nivel : niveles) {
                logsClientService.registerLog("Test message for " + nivel, nivel, null, null);
            }

            // Then - Verificar que se enviaron 4 peticiones HTTP, una por cada nivel
            // Esto asegura que el servicio puede procesar todos los niveles sin problemas
            verify(restTemplate, times(4)).postForEntity(
                    anyString(),
                    any(HttpEntity.class),
                    eq(Void.class)
            );
        }

        @Test
        @DisplayName("Given log without optional fields When registering log Then sends request with null optional fields")
        void whenLogWithoutOptionalFields_registersWithNullOptionalFields() {
            // Given - Log con solo campos requeridos (mensaje y nivel), sin campos opcionales
            // Este test verifica que el servicio maneja correctamente logs sin stackTrace ni metadata
            LogLevel nivel = LogLevel.INFO;
            String mensaje = VALID_MESSAGE;

            mockSuccessfulRestTemplateCall();

            // When - Registrar log sin campos opcionales (stackTrace y metadata son null)
            logsClientService.registerLog(mensaje, nivel, null, null);

            // Then - Verificar que se envió con campos requeridos presentes y opcionales null
            // El DTO debe contener mensaje y nivel, pero stackTrace y metadata deben ser null
            ArgumentCaptor<HttpEntity<LogRegisterRequestDto>> entityCaptor = captureHttpEntity();
            LogRegisterRequestDto requestDto = entityCaptor.getValue().getBody();
            assertThat(requestDto).isNotNull();
            if (requestDto != null) {
                assertThat(requestDto.getMensaje()).isEqualTo(mensaje);
                assertThat(requestDto.getNivel()).isEqualTo(nivel);
                assertThat(requestDto.getStackTrace()).isNull();
                assertThat(requestDto.getMetadata()).isNull();
            }
        }
    }

    @Nested
    @DisplayName("⚠️ Validación de configuración")
    class ConfigurationValidation {

        @Test
        @DisplayName("Given missing base URL When registering log Then does not send request")
        void whenMissingBaseUrl_doesNotSendRequest() {
            // Given - Configuración sin base URL (null)
            // Este test verifica que el servicio valida la configuración antes de enviar logs
            // Si la base URL falta, el servicio no debe intentar enviar la petición para evitar errores
            ReflectionTestUtils.setField(logsClientService, "logsApiBaseUrl", null);
            String mensaje = VALID_MESSAGE;
            LogLevel nivel = LogLevel.ERROR;

            // When - Intentar registrar log sin base URL configurada
            logsClientService.registerLog(mensaje, nivel, null, null);

            // Then - No debe enviar petición HTTP porque la configuración es inválida
            // Esto evita errores de red y asegura que el servicio falla silenciosamente
            verify(restTemplate, never()).postForEntity(anyString(), any(), any());
        }

        @Test
        @DisplayName("Given missing private key When registering log Then does not send request")
        void whenMissingPrivateKey_doesNotSendRequest() {
            // Given - Configuración sin private key (null)
            // Este test verifica que el servicio requiere la private key para autenticación
            // Sin la key, no se puede autenticar con el Backend Logs API, por lo que no debe enviar
            ReflectionTestUtils.setField(logsClientService, "logsApiPrivateKey", null);
            String mensaje = VALID_MESSAGE;
            LogLevel nivel = LogLevel.ERROR;

            // When - Intentar registrar log sin private key configurada
            logsClientService.registerLog(mensaje, nivel, null, null);

            // Then - No debe enviar petición HTTP porque falta la autenticación requerida
            verify(restTemplate, never()).postForEntity(anyString(), any(), any());
        }

        @Test
        @DisplayName("Given blank base URL When registering log Then does not send request")
        void whenBlankBaseUrl_doesNotSendRequest() {
            // Given - Configuración con base URL vacía o solo espacios en blanco
            // Este test verifica que el servicio valida que la base URL no esté solo con espacios
            // usando isBlank() que verifica null, vacío y espacios en blanco
            ReflectionTestUtils.setField(logsClientService, "logsApiBaseUrl", "   ");
            String mensaje = VALID_MESSAGE;
            LogLevel nivel = LogLevel.ERROR;

            // When - Intentar registrar log con base URL blank
            logsClientService.registerLog(mensaje, nivel, null, null);

            // Then - No debe enviar petición HTTP porque la base URL no es válida
            verify(restTemplate, never()).postForEntity(anyString(), any(), any());
        }
    }

    @Nested
    @DisplayName("⚠️ Validación de parámetros")
    class ParameterValidation {

        @Test
        @DisplayName("Given null message When registering log Then does not send request")
        void whenNullMessage_doesNotSendRequest() {
            // Given - Mensaje null (campo requerido)
            // Este test verifica que el servicio valida que el mensaje no sea null
            // El mensaje es requerido por la API, por lo que no debe enviarse si es null
            String mensaje = null;
            LogLevel nivel = LogLevel.ERROR;

            // When - Intentar registrar log con mensaje null
            logsClientService.registerLog(mensaje, nivel, null, null);

            // Then - No debe enviar petición HTTP porque el mensaje es requerido
            // Esto evita enviar datos inválidos al Backend Logs API
            verify(restTemplate, never()).postForEntity(anyString(), any(), any());
        }

        @Test
        @DisplayName("Given null level When registering log Then does not send request")
        void whenNullLevel_doesNotSendRequest() {
            // Given - Nivel null (campo requerido)
            // Este test verifica que el servicio valida que el nivel no sea null
            // El nivel es requerido por la API para clasificar la severidad del log
            String mensaje = VALID_MESSAGE;
            LogLevel nivel = null;

            // When - Intentar registrar log con nivel null
            logsClientService.registerLog(mensaje, nivel, null, null);

            // Then - No debe enviar petición HTTP porque el nivel es requerido
            // Esto evita enviar datos inválidos al Backend Logs API
            verify(restTemplate, never()).postForEntity(anyString(), any(), any());
        }
    }

    @Nested
    @DisplayName("✂️ Truncamiento de mensajes")
    class MessageTruncation {

        @Test
        @DisplayName("Given message exceeding 5000 characters When registering log Then truncates message to 5000 characters")
        void whenMessageExceedsLimit_truncatesTo5000Characters() {
            // Given - Mensaje que excede el límite de 5000 caracteres (6000 caracteres)
            // Este test verifica que el servicio trunca automáticamente mensajes largos
            // para cumplir con el límite del Backend Logs API y evitar errores de validación
            String longMessageStr = createMessageOfLength(6000);
            LogLevel nivel = LogLevel.ERROR;

            mockSuccessfulRestTemplateCall();

            // When - Registrar log con mensaje que excede el límite
            logsClientService.registerLog(longMessageStr, nivel, null, null);

            // Then - Verificar que el mensaje fue truncado exactamente a 5000 caracteres
            // El truncamiento debe preservar los primeros 5000 caracteres y descartar el resto
            ArgumentCaptor<HttpEntity<LogRegisterRequestDto>> entityCaptor = captureHttpEntity();
            LogRegisterRequestDto requestDto = entityCaptor.getValue().getBody();
            assertThat(requestDto).isNotNull();
            if (requestDto != null) {
                assertThat(requestDto.getMensaje()).hasSize(MAX_MESSAGE_LENGTH);
                assertThat(requestDto.getMensaje()).isEqualTo(longMessageStr.substring(0, MAX_MESSAGE_LENGTH));
            }
        }

        @Test
        @DisplayName("Given message exactly 5000 characters When registering log Then does not truncate")
        void whenMessageExactly5000Characters_doesNotTruncate() {
            // Given - Mensaje exactamente en el límite de 5000 caracteres
            // Este test verifica que mensajes en el límite exacto no se truncan innecesariamente
            // Debe preservar el mensaje completo si está exactamente en el límite permitido
            String exactMessageStr = createMessageOfLength(MAX_MESSAGE_LENGTH);
            LogLevel nivel = LogLevel.ERROR;

            mockSuccessfulRestTemplateCall();

            // When - Registrar log con mensaje exactamente en el límite
            logsClientService.registerLog(exactMessageStr, nivel, null, null);

            // Then - Verificar que el mensaje no fue truncado (debe tener exactamente 5000 caracteres)
            // El mensaje debe preservarse completo ya que no excede el límite
            ArgumentCaptor<HttpEntity<LogRegisterRequestDto>> entityCaptor = captureHttpEntity();
            LogRegisterRequestDto requestDto = entityCaptor.getValue().getBody();
            assertThat(requestDto).isNotNull();
            if (requestDto != null) {
                assertThat(requestDto.getMensaje()).hasSize(MAX_MESSAGE_LENGTH);
                assertThat(requestDto.getMensaje()).isEqualTo(exactMessageStr);
            }
        }
    }

    @Nested
    @DisplayName("📦 Serialización de metadata")
    class MetadataSerialization {

        @Test
        @DisplayName("Given valid metadata map When registering log Then serializes metadata to JSON string")
        void whenValidMetadata_serializesToJsonString() {
            // Given - Metadata válida con diferentes tipos de datos (String, Integer, Double, Boolean)
            // Este test verifica que el servicio serializa correctamente la metadata a JSON string
            // usando ObjectMapper, manteniendo los tipos de datos y estructura del Map
            Map<String, Object> metadata = EXAMPLE_METADATA;

            mockSuccessfulRestTemplateCall();

            // When - Registrar log con metadata válida
            logsClientService.registerLog(VALID_MESSAGE, LogLevel.ERROR, null, metadata);

            // Then - Verificar que la metadata fue serializada correctamente a JSON string
            // El DTO debe contener la metadata como JSON string, no como Map
            ArgumentCaptor<HttpEntity<LogRegisterRequestDto>> entityCaptor = captureHttpEntity();
            LogRegisterRequestDto requestDto = entityCaptor.getValue().getBody();
            assertThat(requestDto).isNotNull();
            if (requestDto != null) {
                assertThat(requestDto.getMetadata()).isNotNull();
                // Verificar que la metadata fue serializada manteniendo los valores y tipos
                assertThat(requestDto.getMetadata()).contains("\"userId\":123");
                assertThat(requestDto.getMetadata()).contains("\"orderId\":\"ORD-789\"");
                assertThat(requestDto.getMetadata()).contains("\"amount\":99.99");
                assertThat(requestDto.getMetadata()).contains("\"active\":true");
            }
        }

        @Test
        @DisplayName("Given empty metadata map When registering log Then sets metadata to null")
        void whenEmptyMetadata_setsMetadataToNull() {
            // Given - Metadata vacía (Map sin elementos)
            // Este test verifica que el servicio maneja correctamente metadata vacía
            // Cuando la metadata es null o vacía, debe enviarse como null en el DTO
            Map<String, Object> emptyMetadata = new HashMap<>();

            mockSuccessfulRestTemplateCall();

            // When - Registrar log con metadata vacía
            logsClientService.registerLog(VALID_MESSAGE, LogLevel.ERROR, null, emptyMetadata);

            // Then - Verificar que la metadata es null (no se serializa metadata vacía)
            // El servicio debe omitir metadata vacía en lugar de enviar "{}" o null string
            ArgumentCaptor<HttpEntity<LogRegisterRequestDto>> entityCaptor = captureHttpEntity();
            LogRegisterRequestDto requestDto = entityCaptor.getValue().getBody();
            assertThat(requestDto).isNotNull();
            if (requestDto != null) {
                assertThat(requestDto.getMetadata()).isNull();
            }
        }
    }

    @Nested
    @DisplayName("❌ Manejo silencioso de errores")
    class SilentErrorHandling {

        @Test
        @DisplayName("Given network error When registering log Then handles error silently without throwing")
        void whenNetworkError_handlesErrorSilently() {
            // Given - Error de red o timeout (ResourceAccessException)
            // Este test verifica que el servicio maneja silenciosamente errores de red
            // El servicio debe capturar el error y registrarlo en logs sin propagarlo
            // para no afectar el flujo principal de la aplicación (patrón fire-and-forget)
            String mensaje = VALID_MESSAGE;
            LogLevel nivel = LogLevel.ERROR;
            when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Void.class)))
                    .thenThrow(new ResourceAccessException("Connection timeout"));

            // When - Registrar log (no debe lanzar excepción al thread principal)
            // El método debe manejar el error internamente y retornar normalmente
            logsClientService.registerLog(mensaje, nivel, null, null);

            // Then - Verificar que se intentó enviar la petición pero el error fue manejado silenciosamente
            // El error se captura en el try-catch interno y se registra en logs sin propagarse
            verify(restTemplate, times(1)).postForEntity(anyString(), any(), any());
        }

        @Test
        @DisplayName("Given HTTP 4xx error When registering log Then handles error silently without throwing")
        void whenHttp4xxError_handlesErrorSilently() {
            // Given - Error HTTP 4xx (Bad Request, Unauthorized, etc.)
            // Este test verifica que el servicio maneja silenciosamente errores HTTP del cliente
            // Los errores 4xx indican problemas con la petición (autenticación, validación, etc.)
            // El servicio debe capturar el error y registrarlo sin afectar el flujo principal
            String mensaje = VALID_MESSAGE;
            LogLevel nivel = LogLevel.ERROR;
            RestClientResponseException httpError = createHttpException(
                    400, 
                    "Bad Request", 
                    "{\"error\":\"Invalid request\"}"
            );
            when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Void.class)))
                    .thenThrow(httpError);

            // When - Registrar log (no debe lanzar excepción al thread principal)
            logsClientService.registerLog(mensaje, nivel, null, null);

            // Then - Verificar que se intentó enviar la petición pero el error HTTP fue manejado silenciosamente
            verify(restTemplate, times(1)).postForEntity(anyString(), any(), any());
        }

        @Test
        @DisplayName("Given HTTP 5xx error When registering log Then handles error silently without throwing")
        void whenHttp5xxError_handlesErrorSilently() {
            // Given - Error HTTP 5xx (Internal Server Error, Service Unavailable, etc.)
            // Este test verifica que el servicio maneja silenciosamente errores HTTP del servidor
            // Los errores 5xx indican problemas en el Backend Logs API, no en nuestra aplicación
            // El servicio debe capturar el error y registrarlo sin afectar el flujo principal
            String mensaje = VALID_MESSAGE;
            LogLevel nivel = LogLevel.ERROR;
            RestClientResponseException httpError = createHttpException(
                    500, 
                    "Internal Server Error", 
                    "{\"error\":\"Server error\"}"
            );
            when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Void.class)))
                    .thenThrow(httpError);

            // When - Registrar log (no debe lanzar excepción al thread principal)
            logsClientService.registerLog(mensaje, nivel, null, null);

            // Then - Verificar que se intentó enviar la petición pero el error HTTP fue manejado silenciosamente
            verify(restTemplate, times(1)).postForEntity(anyString(), any(), any());
        }

        @Test
        @DisplayName("Given RestClientException When registering log Then handles error silently without throwing")
        void whenRestClientException_handlesErrorSilently() {
            // Given - RestClientException genérica (otros errores de RestTemplate)
            // Este test verifica que el servicio maneja silenciosamente cualquier excepción de RestTemplate
            // que no sea específicamente ResourceAccessException o RestClientResponseException
            // El servicio debe capturar cualquier error y registrarlo sin afectar el flujo principal
            String mensaje = VALID_MESSAGE;
            LogLevel nivel = LogLevel.ERROR;
            when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Void.class)))
                    .thenThrow(new RestClientException("Unexpected error"));

            // When - Registrar log (no debe lanzar excepción al thread principal)
            logsClientService.registerLog(mensaje, nivel, null, null);

            // Then - Verificar que se intentó enviar la petición pero el error fue manejado silenciosamente
            verify(restTemplate, times(1)).postForEntity(anyString(), any(), any());
        }
    }

    @Nested
    @DisplayName("⚡ Comportamiento asíncrono")
    class AsyncBehavior {

        @Test
        @DisplayName("Given valid log When registering log Then method executes and returns")
        void whenRegisteringLog_executesAndReturns() {
            // Given - Configuración para simular respuesta exitosa
            // Este test verifica que el método puede ejecutarse sin errores
            // Nota: En tests unitarios sin Spring context, @Async no funciona, por lo que
            // el método se ejecuta de forma síncrona. En producción con Spring context,
            // el método se ejecuta en un thread separado del pool configurado en AsyncConfig
            // Este test verifica que el método puede ejecutarse correctamente
            mockSuccessfulRestTemplateCall();

            // When - Registrar log
            // El método debe ejecutarse y completar sin lanzar excepciones
            logsClientService.registerLog(VALID_MESSAGE, LogLevel.ERROR, null, null);

            // Then - Verificar que se intentó enviar la petición HTTP
            // El método debe haber llamado a RestTemplate.postForEntity para enviar el log
            // En producción con @Async, este método retorna inmediatamente sin esperar la respuesta
            verify(restTemplate, times(1)).postForEntity(anyString(), any(), any());
        }
    }
}

