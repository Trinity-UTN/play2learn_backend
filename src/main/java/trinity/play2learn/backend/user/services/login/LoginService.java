package trinity.play2learn.backend.user.services.login;

import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.user.dtos.login.LoginRequestDto;
import trinity.play2learn.backend.user.dtos.login.LoginResponseDto;
import trinity.play2learn.backend.user.mapper.UserMapper;
import trinity.play2learn.backend.user.models.User;
import trinity.play2learn.backend.user.services.jwt.interfaces.IJwtService;
import trinity.play2learn.backend.user.services.login.interfaces.ILoginService;
import trinity.play2learn.backend.user.services.user.interfaces.IUserService;

@Service
@AllArgsConstructor
public class LoginService implements ILoginService {

    private final IUserService userService;
    private final IJwtService jwtService;
    
    //Valida el email y la contraseña y devuelve el token del usuario si todo es correcto.
    @Override
    public LoginResponseDto login(LoginRequestDto loginDto) {
 
        User userToLogin = userService.findUserOrThrowException(loginDto.getEmail(), loginDto.getPassword()); 
        //Devuelve UnauthorizedException si no encuentra al usuario o si la contraseña es incorrecta

        String accessToken = jwtService.generateAccessToken(userToLogin); //El jwtService genera y devuelve el access token.
        //El JWT contiene: email, role, issuedAt, expiration
        String refreshToken = jwtService.generateRefreshToken(userToLogin); //El jwtService genera y devuelve el refresh token.

        return UserMapper.toLoginDto(userToLogin, accessToken, refreshToken);
    }
     
}
