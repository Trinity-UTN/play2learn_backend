package trinity.play2learn.backend.admin.year.services.commons;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.year.repositories.IYearRepository;
import trinity.play2learn.backend.admin.year.services.interfaces.IYearExistService;

/**
 * Servicio para validar si ya existe un año con el mismo nombre.
 */
@Service
@AllArgsConstructor
public class YearExistService implements IYearExistService {

    private IYearRepository yearRepository;

    /**
     * Recibe un string y valida si existe un año con ese nombre.
     *
     * @param String name.
     * @return Boolean.
     */
    @Override
    public boolean validate(String name) {
        if (yearRepository.findByName(name).isPresent()) {
            return true;
        }
        return false;
    }
    
}
