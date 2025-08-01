package trinity.play2learn.backend.activity.clasificacion.services.commons;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.clasificacion.dtos.request.ClasificacionActivityRequestDto;
import trinity.play2learn.backend.activity.clasificacion.services.interfaces.IClasificacionValidateConceptsNamesService;
import trinity.play2learn.backend.activity.clasificacion.services.interfaces.IValidateStringListService;
import trinity.play2learn.backend.configs.messages.ValidationMessages;

@Service
@AllArgsConstructor
public class ClasificacionValidateConceptsNamesService implements IClasificacionValidateConceptsNamesService {
    
    private final IValidateStringListService validateStringListService;
    
    public void validateDuplicateConceptsNames(ClasificacionActivityRequestDto clasificacionActivityRequestDto) {

        List<String> conceptNames = 
        clasificacionActivityRequestDto.getCategories()
        .stream()
        .flatMap(category -> category.getConcepts().stream()) //Junta los conceptos de todas las categorias en una sola lista
        .map(concept -> concept.getName()) //Arma una lista con los nombres de los conceptos
        .toList();

        //Lanza un 400 si hay conceptos repetidos
        validateStringListService.validateDuplicateStringsInList(conceptNames, ValidationMessages.UNIQUE_CONCEPTS_NAME);
    }
}
