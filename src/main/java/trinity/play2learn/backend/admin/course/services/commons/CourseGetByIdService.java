package trinity.play2learn.backend.admin.course.services.commons;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.course.models.Course;
import trinity.play2learn.backend.admin.course.repositories.ICourseRepository;
import trinity.play2learn.backend.admin.course.services.interfaces.ICourseGetByIdService;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;

@Service
@AllArgsConstructor
public class CourseGetByIdService implements ICourseGetByIdService {

    private final ICourseRepository courseRepository;

    /**
     * Obtiene un curso por su ID.
     *
     * @param id ID del curso a obtener.
     * @return Course correspondiente al ID proporcionado.
     * @throws NotFoundException si no se encuentra un curso con el ID proporcionado.
     */
    @Override
    public Course findById(Long id) {
        return courseRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("Course not found with id: " + id));
    }
    
}
