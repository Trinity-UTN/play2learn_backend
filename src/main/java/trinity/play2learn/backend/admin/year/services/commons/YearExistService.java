package trinity.play2learn.backend.admin.year.services.commons;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.year.repositories.IYearRepository;
import trinity.play2learn.backend.admin.year.services.interfaces.IYearExistService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;

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
        return yearRepository.existsByName(name);
    }
    /**
     * Valida si ya existe un año con el mismo id.
     *
     * @param Long id
     * @return boolean
     */
    @Override
    public boolean validate(Long id) {
        return yearRepository.existsById(id);
    }

    @Override
    public void validateExceptId(String name, Long id) {
        if (yearRepository.existsByNameAndIdNot(name, id)) {
            throw new ConflictException("A year with the same name already exists.");   
        }
    }
    
}
