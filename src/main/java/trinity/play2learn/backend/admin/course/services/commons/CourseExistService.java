package trinity.play2learn.backend.admin.course.services.commons;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.course.repositories.ICourseRepository;
import trinity.play2learn.backend.admin.course.services.interfaces.ICourseExistService;
import trinity.play2learn.backend.admin.year.models.Year;


/**
 * Servicio para validar si ya existe un curso con el mismo atributo/s.
 */
@Service
@AllArgsConstructor
public class CourseExistService implements ICourseExistService {

    private final ICourseRepository courseRepository;
    /**
     *  Valida si ya existe un curso con el mismo nombre y año. 
     *
     * @param String name
     * @param Year year
     * @return boolean
     */
    @Override
    public boolean validate(String name, Year year) {
        return courseRepository.existsByNameAndYear(name, year);
    }
    /**
     * Valida si ya existe un curso con el mismo id.
     *
     * @param Long id
     * @return boolean
     */
    @Override
    public boolean validate(Long id) {
        return courseRepository.existsById(id);
    }
    /**
     * Valida si ya existe un curso con el mismo nombre.
     *
     * @param String name
     * @return boolean
     */
    @Override
    public boolean validate(String name) {
        return courseRepository.existsByName(name);
    }
    
}
