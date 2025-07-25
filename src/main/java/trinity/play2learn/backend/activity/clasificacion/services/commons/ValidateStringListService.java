package trinity.play2learn.backend.activity.clasificacion.services.commons;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.clasificacion.services.interfaces.IValidateStringListService;
import trinity.play2learn.backend.configs.exceptions.BadRequestException;

@Service
@AllArgsConstructor
public class ValidateStringListService implements IValidateStringListService {

    //Valida que no haya elementos repetidos en una lista de strings
    @Override
    public void validateDuplicateStringsInList(List<String> strings , String message) {
        
        Long listSize =  ((long) strings.size());

        Long distinctStrings = strings.stream()
            .map(String::toLowerCase)
            .distinct()
            .count();

        if (listSize != distinctStrings) {

            throw new BadRequestException(message);
        }
    }
    
    
}
