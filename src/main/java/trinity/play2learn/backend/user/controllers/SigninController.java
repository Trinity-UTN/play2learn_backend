package trinity.play2learn.backend.user.controllers;
//Caso de uso 8

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.user.dtos.SigninRequestDto;
import trinity.play2learn.backend.user.dtos.SigninResponseDto;
import trinity.play2learn.backend.user.services.signin.interfaces.ISigninService;

@RequestMapping("/signin")
@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class SigninController {
    
    private final ISigninService signinService;

    public SigninController(ISigninService signinService) {
        this.signinService = signinService;
    }

    @PostMapping
    public ResponseEntity<BaseResponse<SigninResponseDto>> signin(@Valid @RequestBody SigninRequestDto signinDto, BindingResult result) {
        return ResponseFactory.created(signinService.signin(signinDto, result), "Created succesfully");
    }

}
