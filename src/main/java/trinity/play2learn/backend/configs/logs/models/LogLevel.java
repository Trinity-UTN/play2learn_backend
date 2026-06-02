package trinity.play2learn.backend.configs.logs.models;

/**
 * Enum que representa los niveles de severidad disponibles para registro de logs
 * en el Backend Logs API.
 * 
 * <p>Este enum proporciona tipado fuerte para los niveles de log, asegurando que
 * solo se puedan usar valores válidos. Los niveles siguen una jerarquía de severidad
 * de menor a mayor: DEBUG, INFO, WARN, ERROR.</p>
 * 
 * <p><strong>Niveles disponibles:</strong></p>
 * <ul>
 *   <li>{@code ERROR}: Errores críticos que requieren atención inmediata.
 *       Generalmente representan excepciones no manejadas o fallos del sistema
 *       que impiden el funcionamiento normal de la aplicación.</li>
 *   <li>{@code WARN}: Advertencias que pueden indicar problemas potenciales.
 *       Representan situaciones que no detienen la ejecución pero que deberían
 *       ser revisadas (ej: valores por defecto usados, configuraciones faltantes).</li>
 *   <li>{@code INFO}: Información general del flujo de la aplicación.
 *       Representa eventos importantes del negocio o del sistema que son útiles
 *       para entender el comportamiento de la aplicación (ej: inicio de sesión,
 *       creación de recursos).</li>
 *   <li>{@code DEBUG}: Información detallada para depuración.
 *       Representa información técnica detallada que es útil solo durante el
 *       desarrollo y depuración (ej: valores de variables, flujo de ejecución).</li>
 * </ul>
 * 
 * <p><strong>Uso:</strong></p>
 * <p>Este enum se utiliza en {@code LogRegisterRequestDto} y {@code LogsClientService}
 * para tipar fuertemente los niveles de log, evitando errores de tipeo y mejorando
 * la mantenibilidad del código.</p>
 * 
 * @see trinity.play2learn.backend.configs.logs.dtos.LogRegisterRequestDto
 * @see trinity.play2learn.backend.configs.logs.services.LogsClientService
 */
public enum LogLevel {
    /**
     * Nivel de error crítico.
     * 
     * <p>Se utiliza para errores que requieren atención inmediata, como excepciones
     * no manejadas, fallos del sistema, o errores que impiden el funcionamiento
     * normal de la aplicación.</p>
     */
    ERROR,

    /**
     * Nivel de advertencia.
     * 
     * <p>Se utiliza para situaciones que pueden indicar problemas potenciales pero
     * que no detienen la ejecución de la aplicación. Ejemplos: valores por defecto
     * usados, configuraciones faltantes, o comportamientos inesperados que se manejan
     * automáticamente.</p>
     */
    WARN,

    /**
     * Nivel de información.
     * 
     * <p>Se utiliza para registrar eventos importantes del negocio o del sistema
     * que son útiles para entender el comportamiento de la aplicación. Ejemplos:
     * inicio de sesión, creación de recursos, o transacciones importantes.</p>
     */
    INFO,

    /**
     * Nivel de depuración.
     * 
     * <p>Se utiliza para registrar información técnica detallada que es útil solo
     * durante el desarrollo y depuración. Ejemplos: valores de variables, flujo de
     * ejecución detallado, o estados internos del sistema.</p>
     */
    DEBUG
}

