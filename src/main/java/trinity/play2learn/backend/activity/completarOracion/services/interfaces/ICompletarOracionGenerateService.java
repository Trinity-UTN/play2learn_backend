package trinity.play2learn.backend.activity.completarOracion.services.interfaces;

import trinity.play2learn.backend.activity.completarOracion.dtos.request.CompletarOracionActivityRequestDto;
import trinity.play2learn.backend.activity.completarOracion.dtos.response.CompletarOracionActivityResponseDto;
import trinity.play2learn.backend.user.models.User;

public interface ICompletarOracionGenerateService {
    
    CompletarOracionActivityResponseDto cu42generateCompletarOracionActivity(CompletarOracionActivityRequestDto completarOracionActivityRequestDto, User user);
}
