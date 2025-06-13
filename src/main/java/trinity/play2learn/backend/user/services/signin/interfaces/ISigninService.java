package trinity.play2learn.backend.user.services.signin.interfaces;

import org.springframework.validation.BindingResult;

import trinity.play2learn.backend.user.dtos.signin.SigninRequestDto;
import trinity.play2learn.backend.user.dtos.signin.SigninResponseDto;

public interface ISigninService {
    
    SigninResponseDto signin(SigninRequestDto signinDto , BindingResult result);
}
