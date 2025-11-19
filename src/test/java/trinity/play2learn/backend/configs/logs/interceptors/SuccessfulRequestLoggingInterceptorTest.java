package trinity.play2learn.backend.configs.logs.interceptors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import trinity.play2learn.backend.configs.logs.models.LogLevel;
import trinity.play2learn.backend.configs.logs.services.LogsClientService;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
@DisplayName("SuccessfulRequestLoggingInterceptor Tests")
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings("unchecked")
class SuccessfulRequestLoggingInterceptorTest {

    @Mock
    private LogsClientService logsClientService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Object handler;

    @InjectMocks
    private SuccessfulRequestLoggingInterceptor interceptor;

    // ========== Fixtures y constantes compartidas ==========

    private static final String TEST_URI = "/api/users/123";
    private static final String TEST_METHOD = "GET";
    private static final String TEST_QUERY_STRING = "page=1&limit=10";
    private static final String TEST_USER_AGENT = "Mozilla/5.0";
    private static final String TEST_REMOTE_ADDR = "192.168.1.1";
    private static final int SUCCESS_STATUS_CODE = 200;
    private static final int CREATED_STATUS_CODE = 201;
    private static final int NO_CONTENT_STATUS_CODE = 204;
    private static final int BAD_REQUEST_STATUS_CODE = 400;
    private static final int NOT_FOUND_STATUS_CODE = 404;
    private static final int INTERNAL_SERVER_ERROR_STATUS_CODE = 500;

    @BeforeEach
    void setUp() {
        // Configuración base para request mock
        when(request.getRequestURI()).thenReturn(TEST_URI);
        when(request.getMethod()).thenReturn(TEST_METHOD);
        when(request.getQueryString()).thenReturn(null);
        when(request.getHeader("User-Agent")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn(null);
    }

    // ========== Helper methods reutilizables ==========

    /**
     * Configura el mock de request con valores por defecto para una petición exitosa.
     */
    private void configureSuccessfulRequest() {
        when(request.getRequestURI()).thenReturn(TEST_URI);
        when(request.getMethod()).thenReturn(TEST_METHOD);
        when(response.getStatus()).thenReturn(SUCCESS_STATUS_CODE);
    }

    /**
     * Simula el preHandle para guardar el tiempo de inicio.
     */
    private void simulatePreHandle() {
        interceptor.preHandle(request, response, handler);
    }

    /**
     * Captura los argumentos pasados a LogsClientService.registerLog.
     *
     * @return ArgumentCaptor con los argumentos capturados
     */
    private ArgumentCaptor<Map<String, Object>> captureMetadata() {
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<LogLevel> levelCaptor = ArgumentCaptor.forClass(LogLevel.class);
        ArgumentCaptor<String> stackTraceCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Map<String, Object>> metadataCaptor = ArgumentCaptor.forClass(Map.class);

        verify(logsClientService).registerLog(
                messageCaptor.capture(),
                levelCaptor.capture(),
                stackTraceCaptor.capture(),
                metadataCaptor.capture()
        );

        return metadataCaptor;
    }

    // ========== Tests para preHandle ==========

    @Nested
    @DisplayName("📋 preHandle - Guardado de tiempo de inicio")
    class PreHandleTests {

        @Test
        @DisplayName("Given valid request When preHandle is called Then saves start time and returns true")
        void whenPreHandleCalled_savesStartTimeAndReturnsTrue() {
            // Given - Request configurado
            configureSuccessfulRequest();

            // When - Llamar a preHandle
            boolean result = interceptor.preHandle(request, response, handler);

            // Then - Verificar que retorna true y guarda el tiempo de inicio
            assertThat(result).isTrue();
            // Verificar que se guardó el atributo (no podemos verificar el valor exacto por el tiempo)
            verify(request).setAttribute(eq("requestStartTime"), any(Long.class));
        }
    }

    // ========== Tests para afterCompletion - Peticiones exitosas ==========

    @Nested
    @DisplayName("✅ afterCompletion - Peticiones exitosas (2xx)")
    class SuccessfulRequestsTests {

        @Test
        @DisplayName("Given successful GET request When afterCompletion is called Then registers log with INFO level")
        void whenSuccessfulGetRequest_registersLogWithInfoLevel() {
            // Given - Petición GET exitosa
            configureSuccessfulRequest();
            simulatePreHandle();

            // When - Llamar a afterCompletion sin excepciones
            interceptor.afterCompletion(request, response, handler, null);

            // Then - Verificar que se llamó a registerLog con nivel INFO
            verify(logsClientService, times(1)).registerLog(
                    anyString(),
                    eq(LogLevel.INFO),
                    isNull(),
                    any(Map.class)
            );
        }

        @Test
        @DisplayName("Given successful POST request When afterCompletion is called Then registers log with correct metadata")
        void whenSuccessfulPostRequest_registersLogWithCorrectMetadata() {
            // Given - Petición POST exitosa
            when(request.getMethod()).thenReturn("POST");
            when(request.getRequestURI()).thenReturn("/api/orders");
            when(response.getStatus()).thenReturn(CREATED_STATUS_CODE);
            simulatePreHandle();

            // When - Llamar a afterCompletion
            interceptor.afterCompletion(request, response, handler, null);

            // Then - Verificar metadata enviada
            ArgumentCaptor<Map<String, Object>> metadataCaptor = captureMetadata();
            Map<String, Object> metadata = metadataCaptor.getValue();

            assertThat(metadata).isNotNull();
            assertThat(metadata.get("url")).isEqualTo("/api/orders");
            assertThat(metadata.get("method")).isEqualTo("POST");
            assertThat(metadata.get("statusCode")).isEqualTo(CREATED_STATUS_CODE);
            assertThat(metadata.get("responseTimeMs")).isInstanceOf(Long.class);
        }

        @Test
        @DisplayName("Given successful PUT request When afterCompletion is called Then registers log with correct message")
        void whenSuccessfulPutRequest_registersLogWithCorrectMessage() {
            // Given - Petición PUT exitosa
            when(request.getMethod()).thenReturn("PUT");
            when(request.getRequestURI()).thenReturn("/api/users/456");
            when(response.getStatus()).thenReturn(SUCCESS_STATUS_CODE);
            simulatePreHandle();

            // When - Llamar a afterCompletion
            interceptor.afterCompletion(request, response, handler, null);

            // Then - Verificar mensaje
            ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
            verify(logsClientService).registerLog(
                    messageCaptor.capture(),
                    any(LogLevel.class),
                    any(),
                    any(Map.class)
            );

            String message = messageCaptor.getValue();
            assertThat(message).contains("Petición exitosa");
            assertThat(message).contains("PUT");
            assertThat(message).contains("/api/users/456");
            assertThat(message).contains("Status: 200");
        }

        @Test
        @DisplayName("Given successful DELETE request When afterCompletion is called Then registers log with NO_CONTENT status")
        void whenSuccessfulDeleteRequest_registersLogWithNoContentStatus() {
            // Given - Petición DELETE exitosa (204 No Content)
            when(request.getMethod()).thenReturn("DELETE");
            when(request.getRequestURI()).thenReturn("/api/users/789");
            when(response.getStatus()).thenReturn(NO_CONTENT_STATUS_CODE);
            simulatePreHandle();

            // When - Llamar a afterCompletion
            interceptor.afterCompletion(request, response, handler, null);

            // Then - Verificar que se registró el log
            ArgumentCaptor<Map<String, Object>> metadataCaptor = captureMetadata();
            Map<String, Object> metadata = metadataCaptor.getValue();

            assertThat(metadata.get("statusCode")).isEqualTo(NO_CONTENT_STATUS_CODE);
        }

        @Test
        @DisplayName("Given successful request with query params When afterCompletion is called Then includes query params in metadata")
        void whenSuccessfulRequestWithQueryParams_includesQueryParamsInMetadata() {
            // Given - Petición con query parameters
            configureSuccessfulRequest();
            when(request.getQueryString()).thenReturn(TEST_QUERY_STRING);
            simulatePreHandle();

            // When - Llamar a afterCompletion
            interceptor.afterCompletion(request, response, handler, null);

            // Then - Verificar que se incluyeron los query params
            ArgumentCaptor<Map<String, Object>> metadataCaptor = captureMetadata();
            Map<String, Object> metadata = metadataCaptor.getValue();

            assertThat(metadata.get("queryParams")).isEqualTo(TEST_QUERY_STRING);
        }

        @Test
        @DisplayName("Given successful request When afterCompletion is called Then includes response time in metadata")
        void whenSuccessfulRequest_includesResponseTimeInMetadata() {
            // Given - Petición exitosa con tiempo de inicio guardado
            configureSuccessfulRequest();
            
            // Simular que preHandle guardó el tiempo de inicio
            long startTime = System.currentTimeMillis() - 100; // Simular que pasaron 100ms
            when(request.getAttribute("requestStartTime")).thenReturn(startTime);
            
            // When - Llamar a afterCompletion
            interceptor.afterCompletion(request, response, handler, null);

            // Then - Verificar que el tiempo de respuesta está incluido en metadata
            ArgumentCaptor<Map<String, Object>> metadataCaptor = captureMetadata();
            Map<String, Object> metadata = metadataCaptor.getValue();

            Long responseTime = (Long) metadata.get("responseTimeMs");
            assertThat(responseTime).isNotNull();
            // El tiempo debe ser aproximadamente 100ms (con margen de error)
            assertThat(responseTime).isGreaterThanOrEqualTo(90L);
            assertThat(responseTime).isLessThanOrEqualTo(150L);
        }
    }

    // ========== Tests para afterCompletion - Peticiones con errores ==========

    @Nested
    @DisplayName("❌ afterCompletion - Peticiones con errores (4xx, 5xx)")
    class ErrorRequestsTests {

        @Test
        @DisplayName("Given 4xx error request When afterCompletion is called Then does not register log")
        void when4xxErrorRequest_doesNotRegisterLog() {
            // Given - Petición con error 4xx
            configureSuccessfulRequest();
            when(response.getStatus()).thenReturn(BAD_REQUEST_STATUS_CODE);
            simulatePreHandle();

            // When - Llamar a afterCompletion
            interceptor.afterCompletion(request, response, handler, null);

            // Then - Verificar que NO se llamó a registerLog
            verify(logsClientService, never()).registerLog(
                    anyString(),
                    any(LogLevel.class),
                    any(),
                    any(Map.class)
            );
        }

        @Test
        @DisplayName("Given 404 error request When afterCompletion is called Then does not register log")
        void when404ErrorRequest_doesNotRegisterLog() {
            // Given - Petición con error 404
            configureSuccessfulRequest();
            when(response.getStatus()).thenReturn(NOT_FOUND_STATUS_CODE);
            simulatePreHandle();

            // When - Llamar a afterCompletion
            interceptor.afterCompletion(request, response, handler, null);

            // Then - Verificar que NO se llamó a registerLog
            verify(logsClientService, never()).registerLog(
                    anyString(),
                    any(LogLevel.class),
                    any(),
                    any(Map.class)
            );
        }

        @Test
        @DisplayName("Given 5xx error request When afterCompletion is called Then does not register log")
        void when5xxErrorRequest_doesNotRegisterLog() {
            // Given - Petición con error 5xx
            configureSuccessfulRequest();
            when(response.getStatus()).thenReturn(INTERNAL_SERVER_ERROR_STATUS_CODE);
            simulatePreHandle();

            // When - Llamar a afterCompletion
            interceptor.afterCompletion(request, response, handler, null);

            // Then - Verificar que NO se llamó a registerLog
            verify(logsClientService, never()).registerLog(
                    anyString(),
                    any(LogLevel.class),
                    any(),
                    any(Map.class)
            );
        }
    }

    // ========== Tests para afterCompletion - Peticiones con excepciones ==========

    @Nested
    @DisplayName("⚠️ afterCompletion - Peticiones con excepciones")
    class ExceptionRequestsTests {

        @Test
        @DisplayName("Given request with exception When afterCompletion is called Then does not register log")
        void whenRequestWithException_doesNotRegisterLog() {
            // Given - Petición con excepción
            configureSuccessfulRequest();
            when(response.getStatus()).thenReturn(SUCCESS_STATUS_CODE);
            Exception exception = new RuntimeException("Test exception");
            simulatePreHandle();

            // When - Llamar a afterCompletion con excepción
            interceptor.afterCompletion(request, response, handler, exception);

            // Then - Verificar que NO se llamó a registerLog (las excepciones se registran en GlobalExceptionHandler)
            verify(logsClientService, never()).registerLog(
                    anyString(),
                    any(LogLevel.class),
                    any(),
                    any(Map.class)
            );
        }
    }

    // ========== Tests para edge cases ==========

    @Nested
    @DisplayName("🔍 Edge Cases")
    class EdgeCasesTests {

        @Test
        @DisplayName("Given request with null method When afterCompletion is called Then handles gracefully")
        void whenRequestWithNullMethod_handlesGracefully() {
            // Given - Request con método null
            when(request.getMethod()).thenReturn(null);
            when(request.getRequestURI()).thenReturn(TEST_URI);
            when(response.getStatus()).thenReturn(SUCCESS_STATUS_CODE);
            simulatePreHandle();

            // When - Llamar a afterCompletion
            interceptor.afterCompletion(request, response, handler, null);

            // Then - Verificar que se registró el log con "UNKNOWN" como método
            ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
            verify(logsClientService).registerLog(
                    messageCaptor.capture(),
                    any(LogLevel.class),
                    any(),
                    any(Map.class)
            );

            String message = messageCaptor.getValue();
            assertThat(message).contains("UNKNOWN");
        }

        @Test
        @DisplayName("Given request with null URI When afterCompletion is called Then handles gracefully")
        void whenRequestWithNullURI_handlesGracefully() {
            // Given - Request con URI null
            when(request.getMethod()).thenReturn(TEST_METHOD);
            when(request.getRequestURI()).thenReturn(null);
            when(response.getStatus()).thenReturn(SUCCESS_STATUS_CODE);
            simulatePreHandle();

            // When - Llamar a afterCompletion
            interceptor.afterCompletion(request, response, handler, null);

            // Then - Verificar que se registró el log con "/" como URI por defecto
            ArgumentCaptor<Map<String, Object>> metadataCaptor = captureMetadata();
            Map<String, Object> metadata = metadataCaptor.getValue();

            assertThat(metadata.get("url")).isEqualTo("");
        }

        @Test
        @DisplayName("Given request without preHandle When afterCompletion is called Then calculates response time as 0")
        void whenRequestWithoutPreHandle_calculatesResponseTimeAsZero() {
            // Given - Request exitoso pero sin llamar a preHandle
            configureSuccessfulRequest();
            // NO llamar a simulatePreHandle()

            // When - Llamar a afterCompletion
            interceptor.afterCompletion(request, response, handler, null);

            // Then - Verificar que el tiempo de respuesta es 0
            ArgumentCaptor<Map<String, Object>> metadataCaptor = captureMetadata();
            Map<String, Object> metadata = metadataCaptor.getValue();

            Long responseTime = (Long) metadata.get("responseTimeMs");
            assertThat(responseTime).isEqualTo(0L);
        }

        @Test
        @DisplayName("Given request with User-Agent When afterCompletion is called Then includes User-Agent in metadata")
        void whenRequestWithUserAgent_includesUserAgentInMetadata() {
            // Given - Request con User-Agent
            configureSuccessfulRequest();
            when(request.getHeader("User-Agent")).thenReturn(TEST_USER_AGENT);
            simulatePreHandle();

            // When - Llamar a afterCompletion
            interceptor.afterCompletion(request, response, handler, null);

            // Then - Verificar que se incluyó el User-Agent
            ArgumentCaptor<Map<String, Object>> metadataCaptor = captureMetadata();
            Map<String, Object> metadata = metadataCaptor.getValue();

            assertThat(metadata.get("userAgent")).isEqualTo(TEST_USER_AGENT);
        }

        @Test
        @DisplayName("Given request with remote address When afterCompletion is called Then includes remote address in metadata")
        void whenRequestWithRemoteAddress_includesRemoteAddressInMetadata() {
            // Given - Request con remote address
            configureSuccessfulRequest();
            when(request.getRemoteAddr()).thenReturn(TEST_REMOTE_ADDR);
            simulatePreHandle();

            // When - Llamar a afterCompletion
            interceptor.afterCompletion(request, response, handler, null);

            // Then - Verificar que se incluyó el remote address
            ArgumentCaptor<Map<String, Object>> metadataCaptor = captureMetadata();
            Map<String, Object> metadata = metadataCaptor.getValue();

            assertThat(metadata.get("remoteAddress")).isEqualTo(TEST_REMOTE_ADDR);
        }
    }
}

