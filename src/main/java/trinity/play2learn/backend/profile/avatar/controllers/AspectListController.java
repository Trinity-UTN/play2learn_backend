package trinity.play2learn.backend.profile.avatar.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.configs.aspects.SessionRequired;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.profile.avatar.dtos.response.AspectResponseDto;
import trinity.play2learn.backend.profile.avatar.services.interfaces.IAspectListService;
import trinity.play2learn.backend.user.models.Role;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@AllArgsConstructor
@RequestMapping("profile/avatar/aspect")
public class AspectListController {

    private final IAspectListService aspectListService;
    
    @GetMapping
    @SessionRequired(roles = {Role.ROLE_ADMIN, Role.ROLE_TEACHER, Role.ROLE_STUDENT})
    public ResponseEntity<BaseResponse<List<AspectResponseDto>>> list() {
        return ResponseFactory.ok(aspectListService.cu48listAspects(), SuccessfulMessages.okSuccessfully());
    }

    
}
