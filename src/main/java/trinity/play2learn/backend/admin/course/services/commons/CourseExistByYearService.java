package trinity.play2learn.backend.admin.course.services.commons;

import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.course.repositories.ICourseRepository;
import trinity.play2learn.backend.admin.course.services.interfaces.ICourseExistByYearService;
import trinity.play2learn.backend.admin.year.models.Year;
import trinity.play2learn.backend.configs.exceptions.ConflictException;

@Service
@AllArgsConstructor
public class CourseExistByYearService implements ICourseExistByYearService{

    private final ICourseRepository courseRepository;

    @Override
    public void validate(Year year) {
        if (courseRepository.existsByYear(year)) {
            throw new ConflictException("El año no puede ser eliminado porque tiene cursos asociados");
        }
    }
    
}
