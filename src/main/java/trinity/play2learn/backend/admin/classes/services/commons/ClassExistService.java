package trinity.play2learn.backend.admin.classes.services.commons;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.classes.services.interfaces.IClassExistService;
import trinity.play2learn.backend.admin.year.models.Year;
import trinity.play2learn.backend.admin.classes.repositories.IClassRepository;


/**
 * Servicio para validar si ya existe un curso con el mismo atributo/s.
 */
@Service
@AllArgsConstructor
public class ClassExistService implements IClassExistService {

    private final IClassRepository classRepository;
    /**
     *  Valida si ya existe un curso con el mismo nombre y año. 
     *
     * @param String name
     * @param Year year
     * @return boolean
     */
    @Override
    public boolean validate(String name, Year year) {
        return classRepository.existsByNameAndYear(name, year);
    }
    /**
     * Valida si ya existe un curso con el mismo id.
     *
     * @param Long id
     * @return boolean
     */
    @Override
    public boolean validate(Long id) {
        return classRepository.existsById(id);
    }
    /**
     * Valida si ya existe un curso con el mismo nombre.
     *
     * @param String name
     * @return boolean
     */
    @Override
    public boolean validate(String name) {
        return classRepository.existsByName(name);
    }
    
}
