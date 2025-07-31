package trinity.play2learn.backend.admin.course.services.commons;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.course.repositories.ICourseRepository;
import trinity.play2learn.backend.admin.course.services.interfaces.ICourseExistByService;
import trinity.play2learn.backend.admin.year.models.Year;
import trinity.play2learn.backend.configs.exceptions.ConflictException;


/**
 * Servicio para validar si ya existe un curso con el mismo atributo/s.
 */
@Service
@AllArgsConstructor
public class CourseExistByService implements ICourseExistByService {

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

    //Valida si ya existe un curso con ese nombre en ese año, exceptuando el curso pasado por ID
    @Override
    public void validateExceptId(Long id, String name, Year year) {
        if (courseRepository.existsByNameAndYearAndIdNot(name, year, id)) {
            
            throw new ConflictException("A class with the same name already exists in the selected year.");
            
        }
    }
    
}
