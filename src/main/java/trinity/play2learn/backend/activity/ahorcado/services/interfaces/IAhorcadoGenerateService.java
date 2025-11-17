package trinity.play2learn.backend.activity.ahorcado.services.interfaces;

import trinity.play2learn.backend.activity.ahorcado.dtos.AhorcadoRequestDto;
import trinity.play2learn.backend.activity.ahorcado.dtos.AhorcadoResponseDto;
import trinity.play2learn.backend.user.models.User;

public interface IAhorcadoGenerateService {
    
    AhorcadoResponseDto cu39GenerateAhorcado(AhorcadoRequestDto ahorcadoDto, User user);
}
