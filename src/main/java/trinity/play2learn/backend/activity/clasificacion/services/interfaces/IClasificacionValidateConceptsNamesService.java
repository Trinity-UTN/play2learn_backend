package trinity.play2learn.backend.activity.clasificacion.services.interfaces;

import trinity.play2learn.backend.activity.clasificacion.dtos.request.ClasificacionActivityRequestDto;

public interface IClasificacionValidateConceptsNamesService {
    
    void validateDuplicateConceptsNames(ClasificacionActivityRequestDto clasificacionActivityRequestDto);
}
