package trinity.play2learn.backend.activity.completarOracion.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.completarOracion.dtos.request.CompletarOracionActivityRequestDto;
import trinity.play2learn.backend.activity.completarOracion.dtos.response.CompletarOracionActivityResponseDto;
import trinity.play2learn.backend.activity.completarOracion.services.interfaces.ICompletarOracionGenerateService;
import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.configs.annotations.SessionUser;
import trinity.play2learn.backend.configs.messages.SuccessfulMessages;
import trinity.play2learn.backend.configs.response.BaseResponse;
import trinity.play2learn.backend.configs.response.ResponseFactory;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@AllArgsConstructor
@RequestMapping("activities/completar-oracion")
public class CompletarOracionGenerateController {
    
    private final ICompletarOracionGenerateService completarOracionGenerateService;

    @PostMapping
    @SessionRequired(roles = {Role.ROLE_ADMIN , Role.ROLE_TEACHER})
    public ResponseEntity<BaseResponse<CompletarOracionActivityResponseDto>> generateCompletarOracion(
        @Valid @RequestBody CompletarOracionActivityRequestDto activityRequestDto, @SessionUser User user) {
        
        return ResponseFactory.created(
            completarOracionGenerateService.cu42generateCompletarOracionActivity(activityRequestDto, user),
            SuccessfulMessages.createdSuccessfully("Actividad de completar oración")
        );
    }
    
}
