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
import trinity.play2learn.backend.configs.messages.SuccesfullyMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;

@RestController
@AllArgsConstructor
@RequestMapping("/activities/clasificacion")
public class ClasificacionActivityGenerateController {
    
    private final IClasificacionGenerateService clasificacionGenerateService;

    @PostMapping
    public ResponseEntity<BaseResponse<ClasificacionActivityResponseDto>> generateClasificacion(@Valid @RequestBody ClasificacionActivityRequestDto activityRequestDto) {

        return ResponseFactory.created(
            clasificacionGenerateService.cu43GenerateClasificacionActivity(activityRequestDto), 
            SuccesfullyMessages.createdSuccessfully("Actividad de clasificación")
        );
    }
}
