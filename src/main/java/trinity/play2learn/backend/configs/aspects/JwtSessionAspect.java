package trinity.play2learn.backend.configs.aspects;

import java.util.Arrays;
import java.util.List;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.configs.exceptions.InternalServerException;
import trinity.play2learn.backend.configs.exceptions.UnauthorizedException;
import trinity.play2learn.backend.configs.messages.InternalServerExceptionMessages;
import trinity.play2learn.backend.configs.messages.UnauthorizedExceptionMessages;
import io.jsonwebtoken.JwtException;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.services.jwt.interfaces.IJwtService;
import trinity.play2learn.backend.user.services.user.interfaces.IUserExistsByEmailService; 

@Aspect
@Component
@AllArgsConstructor
public class JwtSessionAspect {

    private final IJwtService jwtService;
    private final IUserExistsByEmailService userActiveValidation;


    @Before("@annotation(sessionRequired)")
    public void validateJwt(JoinPoint joinPoint , SessionRequired sessionRequired) {
        
        Role jwtRole;

        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes(); //Obtiene la solicitud actual

        if (attrs == null) {
            throw new InternalServerException(
                InternalServerExceptionMessages.NOT_HTTP
            ); //Esta excepcion se lanza si la solicitud obtenida no es HTTP
        }

        HttpServletRequest request = attrs.getRequest();
        String authHeader = request.getHeader("Authorization"); //Obtiene el encabezado de autorizacion donde se ubica el token

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException(
                UnauthorizedExceptionMessages.INVALID_ACCESS_TOKEN
            );
        }

        String jwt = authHeader.substring(7);

        //Chequeo que el token no haya expirado
        try {

            jwtService.isTokenExpired(jwt);
            
        } catch (JwtException e) {
            throw new UnauthorizedException(
                UnauthorizedExceptionMessages.TOKEN_EXPIRED
            );
        }

        try {
            userActiveValidation.validateIfUserIsActive(jwtService.extractUsername(jwt)); //Valida la firma del token y que el usuario este activo en la base de datos

            jwtRole = Role.valueOf(jwtService.extractRole(jwt)); //Devuelve el role del token
            
        } catch (JwtException e) {
            //Si la firma del token no es valida, lanzo una excepcion.
            throw new UnauthorizedException(
                UnauthorizedExceptionMessages.INVALID_ACCESS_TOKEN
            );
        }

        List<Role> requiredRoles = Arrays.asList(sessionRequired.roles()); //Convierte el array de roles permitidos en una lista 

        if (!requiredRoles.contains(jwtRole)) { //Valida que el role del token sea alguno de los roles permitidos

            throw new UnauthorizedException( 
                UnauthorizedExceptionMessages.requiredRoles(requiredRoles)
            ); 
            //Si el role del token no matchea con ninguno de los permitidos, lanza una excepcion
        }

        //Si no se lanza ninguna de las excepciones anteriores, el token es valido
    }
}