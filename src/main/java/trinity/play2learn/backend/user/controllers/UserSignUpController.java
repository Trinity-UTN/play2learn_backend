package trinity.play2learn.backend.user.controllers;
//Caso de uso 8

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.user.dtos.signUp.SignUpRequestDto;
import trinity.play2learn.backend.user.dtos.signUp.SignUpResponseDto;
import trinity.play2learn.backend.user.services.signUp.interfaces.ISignUpService;

@RequestMapping("/signUp")
@RestController
@AllArgsConstructor
public class UserSignUpController {
    
    private final ISignUpService signUpService;

    @PostMapping
    public ResponseEntity<BaseResponse<SignUpResponseDto>> signUp(@Valid @RequestBody SignUpRequestDto signUpDto) {
        return ResponseFactory.created(signUpService.signUp(signUpDto), "Created succesfully");
    }

}
