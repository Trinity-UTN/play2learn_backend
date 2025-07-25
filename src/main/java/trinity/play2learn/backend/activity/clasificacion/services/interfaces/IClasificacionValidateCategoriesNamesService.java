package trinity.play2learn.backend.activity.clasificacion.services.interfaces;

import trinity.play2learn.backend.activity.clasificacion.dtos.request.ClasificacionActivityRequestDto;

public interface IClasificacionValidateCategoriesNamesService {
    
    void validateCategoriesNames(ClasificacionActivityRequestDto clasificacionActivityRequestDto);
}
