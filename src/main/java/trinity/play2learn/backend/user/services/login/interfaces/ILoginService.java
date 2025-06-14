package trinity.play2learn.backend.user.services.login.interfaces;

import trinity.play2learn.backend.user.dtos.login.LoginRequestDto;
import trinity.play2learn.backend.user.dtos.login.LoginResponseDto;

public interface ILoginService {
    
    LoginResponseDto login(LoginRequestDto loginDto);
}
