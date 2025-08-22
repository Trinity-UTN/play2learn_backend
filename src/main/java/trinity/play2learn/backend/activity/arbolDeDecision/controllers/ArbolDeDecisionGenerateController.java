package trinity.play2learn.backend.activity.arbolDeDecision.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.arbolDeDecision.dtos.request.ArbolDeDecisionActivityRequestDto;
import trinity.play2learn.backend.activity.arbolDeDecision.dtos.response.ArbolDeDecisionActivityResponseDto;
import trinity.play2learn.backend.activity.arbolDeDecision.services.interfaces.IArbolDecisionGenerateService;
import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.user.models.Role;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@AllArgsConstructor
@RequestMapping("/activities/arbol-decision")
public class ArbolDeDecisionGenerateController {
    
    private final IArbolDecisionGenerateService arbolDecisionGenerateService;

    @PostMapping
    @SessionRequired(roles = {Role.ROLE_ADMIN, Role.ROLE_TEACHER})
    public ResponseEntity<BaseResponse<ArbolDeDecisionActivityResponseDto>> generate(@Valid @RequestBody ArbolDeDecisionActivityRequestDto activityDto) {
        
        return ResponseFactory.created(arbolDecisionGenerateService.cu46GenerateArbolDeDecisionActivity(activityDto), SuccessfulMessages.createdSuccessfully("Actividad arbol de decision"));
    }
    
}
