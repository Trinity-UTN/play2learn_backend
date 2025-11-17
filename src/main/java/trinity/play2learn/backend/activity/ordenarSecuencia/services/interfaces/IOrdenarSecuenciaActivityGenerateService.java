package trinity.play2learn.backend.activity.ordenarSecuencia.services.interfaces;

import java.io.IOException;

import trinity.play2learn.backend.activity.ordenarSecuencia.dtos.request.OrdenarSecuenciaRequestDto;
import trinity.play2learn.backend.activity.ordenarSecuencia.dtos.response.OrdenarSecuenciaResponseDto;
import trinity.play2learn.backend.user.models.User;

public interface IOrdenarSecuenciaActivityGenerateService {
    
    public OrdenarSecuenciaResponseDto cu44GenerateOrdenarSecuencia (
        OrdenarSecuenciaRequestDto dto, User user
    ) throws IOException;
}
