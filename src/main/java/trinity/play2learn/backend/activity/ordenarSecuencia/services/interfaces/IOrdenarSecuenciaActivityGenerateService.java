package trinity.play2learn.backend.activity.ordenarSecuencia.services.interfaces;

import java.io.IOException;

import trinity.play2learn.backend.activity.ordenarSecuencia.dtos.request.OrdenarSecuenciaRequestDto;
import trinity.play2learn.backend.activity.ordenarSecuencia.dtos.response.OrdenarSecuenciaResponseDto;

public interface IOrdenarSecuenciaActivityGenerateService {
    
    public OrdenarSecuenciaResponseDto cu44GenerateOrdenarSecuencia (
        OrdenarSecuenciaRequestDto dto
    ) throws IOException;
}
