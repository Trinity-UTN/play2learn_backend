package trinity.play2learn.backend.activity.completarOracion.services.interfaces;

import trinity.play2learn.backend.activity.completarOracion.dtos.request.CompletarOracionActivityRequestDto;
import trinity.play2learn.backend.activity.completarOracion.dtos.response.CompletarOracionActivityResponseDto;

public interface ICompletarOracionGenerateService {
    
    CompletarOracionActivityResponseDto cu42generateCompletarOracionActivity(CompletarOracionActivityRequestDto completarOracionActivityRequestDto);
}
