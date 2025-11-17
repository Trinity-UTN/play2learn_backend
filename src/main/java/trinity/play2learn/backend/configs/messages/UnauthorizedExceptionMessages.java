package trinity.play2learn.backend.configs.messages;

import java.util.List;

import trinity.play2learn.backend.user.models.Role;

public class UnauthorizedExceptionMessages {
    
    public static final String UNAUTHORIZED = "Credenciales invalidas o acceso denegado.";
    public static final String TOKEN_EXPIRED = "El token ha expirado.";
    public static final String INVALID_REFRESH_TOKEN = "El refresh token es invalido.";
    public static final String INVALID_ACCESS_TOKEN = "El access token es invalido.";
    public static final String BENEFIT_UNAUTHORIZED_TEACHER = "No puedes crear beneficios de esta materia porque no estas asignado a la materia.";
    
    public static final String requiredRoles (List<Role> roles){
        return "Se necesita alguno de estos roles: " + roles;
    }

}
