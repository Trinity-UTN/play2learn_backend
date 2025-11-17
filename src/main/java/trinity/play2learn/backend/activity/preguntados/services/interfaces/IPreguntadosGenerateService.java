package trinity.play2learn.backend.activity.preguntados.services.interfaces;

import trinity.play2learn.backend.activity.preguntados.dtos.request.PreguntadosRequestDto;
import trinity.play2learn.backend.activity.preguntados.dtos.response.PreguntadosResponseDto;
import trinity.play2learn.backend.user.models.User;

public interface IPreguntadosGenerateService {
    
    PreguntadosResponseDto cu40GeneratePreguntados(PreguntadosRequestDto preguntadosRequestDto, User user);
}
