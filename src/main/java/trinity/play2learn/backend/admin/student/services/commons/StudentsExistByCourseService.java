package trinity.play2learn.backend.admin.student.services.commons;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.repositories.IStudentRepository;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentsExistByCourseService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.messages.ConflictExceptionMessages;

@Service
@AllArgsConstructor
public class StudentsExistByCourseService implements IStudentsExistByCourseService {
    
    private final IStudentRepository studentRepository;

    @Override
    public void validate(Long courseId) {
        if (studentRepository.existsByCourseId(courseId)) {
            throw new ConflictException(
                ConflictExceptionMessages.resourceAlreadyExistsByAtribute(
                    "Estudiante", 
                    "curso", 
                    String.valueOf(courseId)
                )
            );
        }
    }
}
