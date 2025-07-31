package trinity.play2learn.backend.admin.subject.services.commons;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.course.models.Course;
import trinity.play2learn.backend.admin.subject.repositories.ISubjectRepository;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectsExistsByCourseService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.messages.ConflictExceptionMessages;

@Service
@AllArgsConstructor
public class SubjectExistsByCourseService implements ISubjectsExistsByCourseService{
    
    private ISubjectRepository subjectRepository;
    
    @Override
    public void validate(Course course) {
        if (subjectRepository.existsByCourse(course)) {
            throw new ConflictException(
                ConflictExceptionMessages.resourceAlreadyExistsByAtribute(
                    "Materia", 
                    "curso", 
                    course.getName()
                )
            );
        }

    }
}
