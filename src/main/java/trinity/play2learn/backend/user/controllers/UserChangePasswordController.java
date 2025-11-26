package trinity.play2learn.backend.user.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.user.dtos.changePassword.ChangePasswordRequestDto;
import trinity.play2learn.backend.user.services.user.interfaces.IUserChangePasswordService;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;
import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.configs.annotations.SessionUser;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/change-password")
@AllArgsConstructor
public class UserChangePasswordController {

    private final IUserChangePasswordService userChangePasswordService;

    @PostMapping
    @SessionRequired(roles = {Role.ROLE_STUDENT, Role.ROLE_TEACHER, Role.ROLE_ADMIN})
    public ResponseEntity<BaseResponse<Void>> changePassword(
        @SessionUser User user, 
        @Valid @RequestBody ChangePasswordRequestDto requestDto
    ) {
        userChangePasswordService.changePassword(user, requestDto.getOldPassword(), requestDto.getNewPassword());
        return ResponseFactory.ok(null, SuccessfulMessages.updatedSuccessfully("Contraseña"));
    }
}
