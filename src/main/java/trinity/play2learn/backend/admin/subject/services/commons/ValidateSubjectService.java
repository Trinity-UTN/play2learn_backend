package trinity.play2learn.backend.admin.subject.services.commons;

import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.course.models.Course;
import trinity.play2learn.backend.admin.subject.repositories.ISubjectRepository;
import trinity.play2learn.backend.admin.subject.services.interfaces.IValidateSubjectService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;

@Service
@AllArgsConstructor
public class ValidateSubjectService implements IValidateSubjectService {
    
    private final ISubjectRepository subjectRepository;

    @Override
    public void subjectExistByNameAndCourse(String name , Course course) {

        if (subjectRepository.existsByNameAndCourse(name, course)) {
            throw new ConflictException("Subject with name " + name + " already exists in course " + course.getYear().getName() + " " +course.getName());
        }
            
    }

    @Override
    public void subjectExistByNameAndCourseExceptId(String name, Course course, Long id) {

        if (subjectRepository.existsByNameAndCourseAndIdNot(name, course, id)) {
            throw new ConflictException("Subject with name " + name + " already exists in course " + course.getYear().getName() + " " +course.getName());
        }
    }
    
}
