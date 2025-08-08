package trinity.play2learn.backend.profile.profile.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.dtos.StudentResponseDto;
import trinity.play2learn.backend.configs.aspects.SessionRequired;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.profile.profile.dtos.request.AddAspectRequestDto;
import trinity.play2learn.backend.profile.profile.services.interfaces.IProfileAddAspectToInventoryService;
import trinity.play2learn.backend.user.models.Role;

@RestController
@AllArgsConstructor
@RequestMapping("profile/add-aspect-to-inventory")
public class AddAspectToInventoryController {

    private final IProfileAddAspectToInventoryService profileAddAspectToInventoryService;

    @PostMapping
    @SessionRequired(roles = {Role.ROLE_STUDENT, Role.ROLE_ADMIN})
    public ResponseEntity<BaseResponse<StudentResponseDto>> addAspectToInventory(
        @RequestBody @Valid AddAspectRequestDto request
    ) {
        return ResponseFactory.created(
            profileAddAspectToInventoryService.cu53addAspectToInventory(
                request.getAspectId(),
                request.getProfileId()
            ), 
            SuccessfulMessages.createdSuccessfully("Aspecto")
        );
    }
}
