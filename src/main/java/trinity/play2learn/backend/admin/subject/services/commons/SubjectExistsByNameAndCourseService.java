package trinity.play2learn.backend.admin.subject.services.commons;

import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.course.models.Course;
import trinity.play2learn.backend.admin.subject.repositories.ISubjectRepository;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectExistsByNameAndCourseService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.messages.ConflictExceptionMessages;

@Service
@AllArgsConstructor
public class SubjectExistsByNameAndCourseService implements ISubjectExistsByNameAndCourseService {
    
    private final ISubjectRepository subjectRepository;

    @Override
    public void existByNameAndCourse(String name , Course course) {

        if (subjectRepository.existsByNameIgnoreCaseAndCourse(name, course)) {
            throw new ConflictException(
                ConflictExceptionMessages.resourceAlreadyExistsByName(
                    "Materia",
                    "nombre"
                )
            );
        }
            
    }

    @Override
    public void existByNameAndCourseAndIdNot(String name, Course course, Long id) {

        if (subjectRepository.existsByNameIgnoreCaseAndCourseAndIdNot(name, course, id)) {
            throw new ConflictException(
                ConflictExceptionMessages.resourceAlreadyExistsByName(
                    "Materia",
                    "nombre"
                )
            );
        }
    }
    
}
