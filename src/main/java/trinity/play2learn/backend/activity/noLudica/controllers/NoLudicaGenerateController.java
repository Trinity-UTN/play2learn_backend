package trinity.play2learn.backend.activity.noLudica.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.noLudica.dtos.request.NoLudicaRequestDto;
import trinity.play2learn.backend.activity.noLudica.dtos.response.NoLudicaResponseDto;
import trinity.play2learn.backend.activity.noLudica.services.interfaces.INoLudicaGenerateService;
import trinity.play2learn.backend.configs.aspects.SessionRequired;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.user.models.Role;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@AllArgsConstructor
@RequestMapping("activities/no-ludica")
public class NoLudicaGenerateController {

    private final INoLudicaGenerateService noLudicaGenerateService;


    @PostMapping
    @SessionRequired (roles = {Role.ROLE_ADMIN, Role.ROLE_TEACHER})
    public ResponseEntity<BaseResponse<NoLudicaResponseDto>> generate(
            @Valid @RequestBody NoLudicaRequestDto requestDto
    ) {
        return ResponseFactory.created(
            noLudicaGenerateService.cu45GenerateNoLudica(requestDto),
            SuccessfulMessages.createdSuccessfully("Actividad no ludica")
        );
    }
    
    
}
