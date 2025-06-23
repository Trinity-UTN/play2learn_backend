package trinity.play2learn.backend.configs.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;
import trinity.play2learn.backend.configs.exceptions.InternalServerException;
import trinity.play2learn.backend.configs.exceptions.UnauthorizedException;
import io.jsonwebtoken.JwtException;

import trinity.play2learn.backend.user.services.jwt.interfaces.IJwtService; 

@Aspect
@Component
public class JwtSessionAspect {

    private final IJwtService jwtService;

    public JwtSessionAspect(IJwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Before("@annotation(sessionRequired)")
    public void validateJwt(JoinPoint joinPoint , SessionRequired sessionRequired) {
        
        String jwtRole = "";

        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes(); //Obtiene la solicitud actual
        
        if (attrs == null) {
            throw new InternalServerException("Not a HTTP request."); //Esta excepcion se lanza si la solicitud obtenida no es HTTP
        }

        HttpServletRequest request = attrs.getRequest();
        String authHeader = request.getHeader("Authorization"); //Obtiene el encabezado de autorizacion donde se ubica el token

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Missing or invalid authentication token.");
        }

        String jwt = authHeader.substring(7);

        //Chequeo que el token no haya expirado
        if (jwtService.isTokenExpired(jwt)) {
            throw new UnauthorizedException("Token expired.");
            
        }

        try {
            jwtRole = jwtService.extractRole(jwt); //Valida la firma del token y devuelve el role del token de ser valido
            
        } catch (JwtException e) {
            //Si la firma del token no es valida, lanzo una excepcion.
            throw new UnauthorizedException("Invalid authentication token.");
        }

        String requiredRole = sessionRequired.role().toString(); //Obtiene el role requerido para la sesion

        if (!jwtRole.equals(requiredRole)) {
            throw new UnauthorizedException(requiredRole + " role required.");
        }

        //Si no se lanza ninguna de las excepciones anteriores, el token es valido
    }
}