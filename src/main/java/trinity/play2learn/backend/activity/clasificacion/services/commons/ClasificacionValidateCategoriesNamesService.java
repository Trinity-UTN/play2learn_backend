package trinity.play2learn.backend.activity.clasificacion.services.commons;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.clasificacion.dtos.request.CategoryClasificacionRequestDto;
import trinity.play2learn.backend.activity.clasificacion.dtos.request.ClasificacionActivityRequestDto;
import trinity.play2learn.backend.activity.clasificacion.services.interfaces.IClasificacionValidateCategoriesNamesService;
import trinity.play2learn.backend.activity.clasificacion.services.interfaces.IValidateStringListService;

@Service
@AllArgsConstructor
public class ClasificacionValidateCategoriesNamesService implements IClasificacionValidateCategoriesNamesService{

    private final IValidateStringListService ValidateStringListService;

    //Valida que no haya categorias repetidas y lanza una 400 si las hay
    @Override
    public void validateCategoriesNames(ClasificacionActivityRequestDto clasificacionActivityRequestDto) {

        List<String> categoriesNames = clasificacionActivityRequestDto.getCategories().stream().map(CategoryClasificacionRequestDto::getName).toList();

        ValidateStringListService.validateDuplicateStringsInList(categoriesNames, "The categories names must be unique.");
    }
    
    
}
