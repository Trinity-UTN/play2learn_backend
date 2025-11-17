package trinity.play2learn.backend.user.services.login;

import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.mappers.StudentMapper;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.admin.teacher.mapper.TeacherMapper;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherGetByEmailService;
import trinity.play2learn.backend.user.dtos.login.LoginRequestDto;
import trinity.play2learn.backend.user.dtos.login.LoginResponseDto;
import trinity.play2learn.backend.user.mapper.UserMapper;
import trinity.play2learn.backend.user.models.User;
import trinity.play2learn.backend.user.services.jwt.interfaces.IJwtService;
import trinity.play2learn.backend.user.services.login.interfaces.IUserLoginService;
import trinity.play2learn.backend.user.services.user.interfaces.IUserService;

@Service
@AllArgsConstructor
public class UserLoginService implements IUserLoginService {

    private final IUserService userService;
    private final IJwtService jwtService;
    private final ITeacherGetByEmailService teacherGetByEmailService;
    private final IStudentGetByEmailService studentGetByEmailService;

    //Valida el email y la contraseña y devuelve el token del usuario si todo es correcto.
    @Override
    public LoginResponseDto cu1Login(LoginRequestDto loginDto) {
 
        User userToLogin = userService.findUserByEmailAndPassword(loginDto.getEmail(), loginDto.getPassword()); 
        //Devuelve UnauthorizedException si no encuentra al usuario o si la contraseña es incorrecta

        String accessToken = jwtService.generateAccessToken(userToLogin); //El jwtService genera y devuelve el access token.
        //El JWT contiene: email, role, issuedAt, expiration
        String refreshToken = jwtService.generateRefreshToken(userToLogin); //El jwtService genera y devuelve el refresh token.

        Object roleData = switch (userToLogin.getRole()) {
            case ROLE_TEACHER -> TeacherMapper.toDto(
                teacherGetByEmailService.getByEmail(userToLogin.getEmail())
            );
            case ROLE_STUDENT -> StudentMapper.toDto(
                studentGetByEmailService.getByEmail(userToLogin.getEmail())
            );
            default -> null;
        };

        return UserMapper.toLoginDto(userToLogin, accessToken, refreshToken, roleData);
    }
     
}
