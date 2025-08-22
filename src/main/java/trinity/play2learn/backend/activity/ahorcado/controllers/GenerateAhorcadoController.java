package trinity.play2learn.backend.activity.ahorcado.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.ahorcado.dtos.AhorcadoRequestDto;
import trinity.play2learn.backend.activity.ahorcado.dtos.AhorcadoResponseDto;
import trinity.play2learn.backend.activity.ahorcado.services.interfaces.IAhorcadoGenerateService;
import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.user.models.Role;

@RestController
@AllArgsConstructor
@RequestMapping("activities/ahorcado")
public class GenerateAhorcadoController {
    
    private final IAhorcadoGenerateService generateAhorcadoService;
    
    @PostMapping
    @SessionRequired(roles = {Role.ROLE_ADMIN, Role.ROLE_TEACHER})
    public ResponseEntity<BaseResponse<AhorcadoResponseDto>> generateAhorcado(@Valid @RequestBody AhorcadoRequestDto ahorcadoDto) {
        return ResponseFactory.created(
            generateAhorcadoService.cu39GenerateAhorcado(ahorcadoDto), 
            SuccessfulMessages.createdSuccessfully("Actividad de ahorcado")
        );
    }
}
