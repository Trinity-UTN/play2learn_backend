package trinity.play2learn.backend.configs.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Configuración para habilitar procesamiento asíncrono en Spring.
 * 
 * <p>Esta configuración permite ejecutar operaciones de forma asíncrona y no bloqueante
 * usando la anotación {@code @Async}. El executor configurado está optimizado para
 * operaciones fire-and-forget que no deben bloquear el flujo principal de la aplicación.</p>
 * 
 * <p>Características principales:
 * <ul>
 *   <li>Thread pool con tamaño configurado para operaciones no bloqueantes</li>
 *   <li>Manejo de tareas rechazadas cuando el pool está saturado</li>
 *   <li>Manejo de excepciones no capturadas en tareas asíncronas</li>
 *   <li>Thread-safe por defecto</li>
 * </ul>
 * </p>
 * 
 * <p>Esta configuración es especialmente útil para operaciones como el registro de logs
 * desde sistemas externos, donde el fallo de la operación no debe afectar el flujo
 * principal de la aplicación.</p>
 * 
 * @see org.springframework.scheduling.annotation.Async
 */
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    /**
     * Configura el ThreadPoolTaskExecutor para operaciones asíncronas.
     * 
     * <p>El executor está configurado con:
     * <ul>
     *   <li>corePoolSize: 5 threads mínimos en el pool</li>
     *   <li>maxPoolSize: 10 threads máximos en el pool</li>
     *   <li>queueCapacity: 100 tareas en cola antes de crear nuevos threads</li>
     *   <li>threadNamePrefix: "async-log-" para identificación en logs</li>
     *   <li>RejectedExecutionHandler: DiscardOldestPolicy para descartar tareas antiguas
     *       cuando el pool está saturado, evitando bloqueos</li>
     * </ul>
     * </p>
     * 
     * <p>Cuando el pool de threads está saturado (todos los threads ocupados y la cola llena),
     * el {@code RejectedExecutionHandler} descarta la tarea más antigua de la cola para
     * aceptar la nueva tarea. Esto es apropiado para operaciones fire-and-forget donde
     * es preferible perder logs antiguos antes que bloquear el flujo principal.</p>
     * 
     * <p>Este executor es thread-safe y está diseñado para operaciones no bloqueantes
     * como el registro de logs desde sistemas externos.</p>
     * 
     * @return Executor configurado para operaciones asíncronas
     */
    @Bean(name = "taskExecutor")
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("async-log-");
        
        // Configuración de RejectedExecutionHandler para manejar overflow del pool
        // DiscardOldestPolicy: descarta la tarea más antigua cuando la cola está llena
        // Esto es apropiado para operaciones fire-and-forget donde preferimos perder
        // logs antiguos antes que bloquear el flujo principal
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
        
        executor.initialize();
        return executor;
    }

    /**
     * Configura el manejador de excepciones no capturadas en tareas asíncronas.
     * 
     * <p>Cuando una tarea asíncrona lanza una excepción que no es capturada dentro del
     * método, este handler se encarga de registrarla sin afectar el flujo principal.
     * Las excepciones se registran en el log con información detallada para troubleshooting.</p>
     * 
     * <p>Para operaciones fire-and-forget como el registro de logs, es importante que
     * las excepciones no se propaguen al thread principal, por lo que este handler
     * las registra silenciosamente.</p>
     * 
     * @return AsyncUncaughtExceptionHandler que registra excepciones no capturadas
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new CustomAsyncExceptionHandler();
    }

    /**
     * Handler personalizado para excepciones no capturadas en tareas asíncronas.
     * 
     * <p>Este handler registra las excepciones en el log sin propagarlas al thread
     * principal, lo cual es apropiado para operaciones fire-and-forget.</p>
     */
    private static class CustomAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

        private static final Logger logger = LoggerFactory.getLogger(CustomAsyncExceptionHandler.class);

        @Override
        public void handleUncaughtException(@NonNull Throwable ex, @NonNull Method method, @NonNull Object... params) {
            logger.error(
                "Excepción no capturada en método asíncrono: {}.{}(). Parámetros: {}",
                method.getDeclaringClass().getName(),
                method.getName(),
                params,
                ex
            );
        }
    }
}

