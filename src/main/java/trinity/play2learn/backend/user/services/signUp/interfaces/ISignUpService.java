package trinity.play2learn.backend.user.services.signUp.interfaces;

import org.springframework.validation.BindingResult;

import trinity.play2learn.backend.user.dtos.signUp.SignUpRequestDto;
import trinity.play2learn.backend.user.dtos.signUp.SignUpResponseDto;

public interface ISignUpService {
    
    SignUpResponseDto signUp(SignUpRequestDto signUpDto , BindingResult result);
}
