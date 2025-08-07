package trinity.play2learn.backend.configs.interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import jakarta.servlet.http.HttpServletRequest;
import trinity.play2learn.backend.configs.annotations.SessionUser;
import trinity.play2learn.backend.configs.exceptions.UnauthorizedException;
import trinity.play2learn.backend.configs.messages.UnauthorizedExceptionMessages;
import trinity.play2learn.backend.user.models.User;
import trinity.play2learn.backend.user.services.jwt.JwtService;
import trinity.play2learn.backend.user.services.user.interfaces.IUserGetByEmailService;

@Component
public class SessionUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private IUserGetByEmailService userGetByEmailService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(SessionUser.class) 
               && parameter.getParameterType().equals(User.class);
    }

    @SuppressWarnings("null")
    @Override
    public Object resolveArgument(MethodParameter parameter, 
                                ModelAndViewContainer mavContainer,
                                NativeWebRequest webRequest, 
                                WebDataBinderFactory binderFactory) throws Exception {
        
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        
        // Extraer el token del header Authorization
        String authHeader = request.getHeader("Authorization"); //Obtiene el encabezado de autorizacion donde se ubica el token

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException(
                UnauthorizedExceptionMessages.INVALID_ACCESS_TOKEN
            );
        }

        String jwt = authHeader.substring(7);

        String username;
        try {

            username = jwtService.extractUsername(jwt);
        } catch (Exception e) {

            throw new UnauthorizedException(
                UnauthorizedExceptionMessages.INVALID_ACCESS_TOKEN
            );
        }

        User user = userGetByEmailService.findUserByEmail(username); //Lanza un 404 si el usuario no existe o esta eliminado
        return user;
    }
}
