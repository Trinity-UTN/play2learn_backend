package trinity.play2learn.backend.profile.profile.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.dtos.StudentResponseDto;
import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.profile.profile.dtos.request.UnselectAspectRequestDto;
import trinity.play2learn.backend.profile.profile.services.interfaces.IProfileUnselectAspectService;
import trinity.play2learn.backend.user.models.Role;

@RestController
@AllArgsConstructor
@RequestMapping("profile/unselect-aspect")
public class UnselectAspectController {

    private final IProfileUnselectAspectService profileUnselectAspectService;

    @PatchMapping
    @SessionRequired(roles = {Role.ROLE_STUDENT, Role.ROLE_ADMIN})
    public ResponseEntity<BaseResponse<StudentResponseDto>> selectAspect(
        @RequestBody @Valid UnselectAspectRequestDto request
    ) {
        return ResponseFactory.created(
            profileUnselectAspectService.cu59unselectAspect(request), 
            SuccessfulMessages.createdSuccessfully("Aspecto")
        );
    }
    
}
