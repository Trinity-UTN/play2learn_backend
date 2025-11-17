package trinity.play2learn.backend.activity.clasificacion.services.interfaces;

import trinity.play2learn.backend.activity.clasificacion.dtos.request.ClasificacionActivityRequestDto;
import trinity.play2learn.backend.activity.clasificacion.dtos.response.ClasificacionActivityResponseDto;
import trinity.play2learn.backend.user.models.User;

public interface IClasificacionGenerateService {
    
    ClasificacionActivityResponseDto cu43GenerateClasificacionActivity(ClasificacionActivityRequestDto clasificacionActivityRequestDto, User user);
}
