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


import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;

import trinity.play2learn.backend.user.services.jwt.interfaces.IJwtService; 

@Aspect
@Component
public class JwtSessionAspect {

    private final IJwtService jwtService;

    public JwtSessionAspect(IJwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Before("@annotation(SessionRequired)")
    public void validateJwt(JoinPoint joinPoint) {
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

        try {
            jwtService.extractUsername(jwt); 

        } catch (ExpiredJwtException ex) {
            
            //El token expiro
            throw new UnauthorizedException("The JWT has expired.");
        } catch (MalformedJwtException ex) {

            //Firma del metodo invalida
            throw new UnauthorizedException("Invalid authentication token.");

        } catch (UnsupportedJwtException ex) {

            // El formato del token no es soportado
            throw new UnauthorizedException("Unsupported authentication token format.");

        } catch (Exception ex) {

            //Ocurrio cualquier otro error
            throw new InternalServerException(ex.getMessage());

        }
        //Si no se lanza ninguna de las excepciones anteriores, el token es valido
    }
}