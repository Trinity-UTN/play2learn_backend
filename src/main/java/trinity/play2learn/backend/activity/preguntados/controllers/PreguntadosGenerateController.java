package trinity.play2learn.backend.activity.preguntados.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.preguntados.dtos.request.PreguntadosRequestDto;
import trinity.play2learn.backend.activity.preguntados.dtos.response.PreguntadosResponseDto;
import trinity.play2learn.backend.activity.preguntados.services.interfaces.IPreguntadosGenerateService;
import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.user.models.Role;

@RestController
@AllArgsConstructor
@RequestMapping("/activities/preguntados")
public class PreguntadosGenerateController {
    
    private final IPreguntadosGenerateService preguntadosGenerateService;

    @PostMapping
    @SessionRequired(roles = {Role.ROLE_ADMIN, Role.ROLE_TEACHER})
    public ResponseEntity<BaseResponse<PreguntadosResponseDto>> generatePreguntados(@Valid @RequestBody PreguntadosRequestDto preguntadosRequestDto) {

        return ResponseFactory.created(
            preguntadosGenerateService.cu40GeneratePreguntados(preguntadosRequestDto),
            SuccessfulMessages.createdSuccessfully("Actividad de preguntados")
        ); 
    }
}
