package trinity.play2learn.backend.activity.clasificacion.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.clasificacion.dtos.request.ClasificacionActivityRequestDto;
import trinity.play2learn.backend.activity.clasificacion.dtos.response.ClasificacionActivityResponseDto;
import trinity.play2learn.backend.activity.clasificacion.services.interfaces.IClasificacionGenerateService;
import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.user.models.Role;

@RestController
@AllArgsConstructor
@RequestMapping("/activities/clasificacion")
public class ClasificacionActivityGenerateController {
    
    private final IClasificacionGenerateService clasificacionGenerateService;

    @PostMapping
    @SessionRequired(roles = {Role.ROLE_ADMIN, Role.ROLE_TEACHER})
    public ResponseEntity<BaseResponse<ClasificacionActivityResponseDto>> generateClasificacion(@Valid @RequestBody ClasificacionActivityRequestDto activityRequestDto) {

        return ResponseFactory.created(
            clasificacionGenerateService.cu43GenerateClasificacionActivity(activityRequestDto), 
            SuccessfulMessages.createdSuccessfully("Actividad de clasificación")
        );
    }
}
