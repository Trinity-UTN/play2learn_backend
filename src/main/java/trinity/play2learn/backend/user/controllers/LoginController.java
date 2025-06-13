package trinity.play2learn.backend.user.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.user.dtos.login.LoginRequestDto;
import trinity.play2learn.backend.user.dtos.login.LoginResponseDto;
import trinity.play2learn.backend.user.services.login.interfaces.ILoginService;

@RestController
@RequestMapping("/login")
@AllArgsConstructor
public class LoginController {
    
    private final ILoginService loginService;
    
    @PostMapping
    public ResponseEntity<BaseResponse<LoginResponseDto>> signin(@Valid @RequestBody LoginRequestDto loginDto) {
        return ResponseFactory.ok(loginService.login(loginDto), "Logged in successfully");
    }
}
