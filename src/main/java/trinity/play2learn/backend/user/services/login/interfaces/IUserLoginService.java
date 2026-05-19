package trinity.play2learn.backend.user.services.login.interfaces;

import jakarta.servlet.http.HttpServletResponse;
import trinity.play2learn.backend.user.dtos.login.LoginRequestDto;
import trinity.play2learn.backend.user.dtos.login.LoginResponseDto;

public interface IUserLoginService {
    
    LoginResponseDto cu1Login(LoginRequestDto loginDto, HttpServletResponse response);
}
