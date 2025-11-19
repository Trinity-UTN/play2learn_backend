package trinity.play2learn.backend.configs.logs.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import trinity.play2learn.backend.configs.logs.interceptors.SuccessfulRequestLoggingInterceptor;

/**
 * Configuración de WebMvc que registra el interceptor para el registro automático
 * de logs de peticiones HTTP exitosas.
 * <p>
 * Esta configuración aplica el {@link SuccessfulRequestLoggingInterceptor} a todas
 * las rutas REST de la aplicación, permitiendo que se registren automáticamente
 * logs con nivel INFO cuando ocurran peticiones exitosas (códigos 2xx).
 * <p>
 * <strong>Patrón de configuración:</strong>
 * <p>
 * Esta clase sigue el mismo patrón que otras configuraciones del proyecto
 * (por ejemplo, {@code ArgumentSolversConfig}) implementando {@link WebMvcConfigurer}
 * y registrando interceptores mediante el método {@code addInterceptors}.
 * <p>
 * <strong>Alcance del interceptor:</strong>
 * <p>
 * El interceptor se aplica a todas las rutas de la aplicación mediante el patrón
 * {@code "/**"}. Esto asegura que todas las peticiones HTTP exitosas sean registradas
 * automáticamente sin necesidad de configuración adicional en los controladores.
 * <p>
 * <strong>Nota sobre rendimiento:</strong>
 * <p>
 * El interceptor está diseñado para ser eficiente y no afectar el rendimiento de las
 * peticiones. El registro de logs se realiza de forma asíncrona (fire-and-forget),
 * por lo que no bloquea el flujo principal de la aplicación.
 *
 * @see SuccessfulRequestLoggingInterceptor
 * @see WebMvcConfigurer
 * @see org.springframework.web.servlet.config.annotation.InterceptorRegistry
 * @since 1.0.0
 */
@Configuration
@RequiredArgsConstructor
public class LogsInterceptorConfig implements WebMvcConfigurer {

    private final SuccessfulRequestLoggingInterceptor successfulRequestLoggingInterceptor;

    /**
     * Registra el interceptor de logs exitosos en el registro de interceptores de Spring MVC.
     * <p>
     * El interceptor se aplica a todas las rutas de la aplicación mediante el patrón
     * {@code "/**"}, asegurando que todas las peticiones HTTP exitosas sean interceptadas
     * y registradas automáticamente.
     *
     * @param registry el registro de interceptores de Spring MVC
     */
    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(successfulRequestLoggingInterceptor)
                .addPathPatterns("/**"); // Aplicar a todas las rutas
    }
}

