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
import trinity.play2learn.backend.profile.profile.dtos.request.AddAspectRequestDto;
import trinity.play2learn.backend.profile.profile.services.interfaces.IProfileSelectAspectService;
import trinity.play2learn.backend.user.models.Role;

@RestController
@AllArgsConstructor
@RequestMapping("profile/select-aspect")
public class SelectAspectController {

    private final IProfileSelectAspectService profileAddAspectToInventoryService;

    @PatchMapping
    @SessionRequired(roles = {Role.ROLE_STUDENT, Role.ROLE_ADMIN})
    public ResponseEntity<BaseResponse<StudentResponseDto>> selectAspect(
        @RequestBody @Valid AddAspectRequestDto request
    ) {
        return ResponseFactory.created(
            profileAddAspectToInventoryService.cu54selectAspect(
                request.getProfileId(),
                request.getAspectId()
            ), 
            SuccessfulMessages.createdSuccessfully("Aspecto")
        );
    }
}
