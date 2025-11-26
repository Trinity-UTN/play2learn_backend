package trinity.play2learn.backend.user.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.services.user.interfaces.IUserRestorePasswordService;

@RestController
@RequestMapping("/restore-password")
@AllArgsConstructor
public class UserRestorePasswordController {

    private final IUserRestorePasswordService userRestorePasswordService;

    @PatchMapping("/student/{id}")
    @SessionRequired(roles = {Role.ROLE_ADMIN})
    public ResponseEntity<BaseResponse<Void>> restoreStudentPassword(
        @PathVariable Long id
    ) {
        userRestorePasswordService.cu114RestoreStudentPassword(id);
        return ResponseFactory.ok(null, SuccessfulMessages.updatedSuccessfully("Contraseña"));
    }

    @PatchMapping("/teacher/{id}")
    @SessionRequired(roles = {Role.ROLE_ADMIN})
    public ResponseEntity<BaseResponse<Void>> restoreTeacherPassword(
        @PathVariable Long id
    ) {
        userRestorePasswordService.cu115RestoreTeacherPassword(id);
        return ResponseFactory.ok(null, SuccessfulMessages.updatedSuccessfully("Contraseña"));
    }
}
