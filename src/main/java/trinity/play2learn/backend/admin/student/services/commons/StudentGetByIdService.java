package trinity.play2learn.backend.admin.student.services.commons;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.repositories.IStudentRepository;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByIdService;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;
import trinity.play2learn.backend.configs.messages.NotFoundExceptionMesagges;

@Service
@AllArgsConstructor
public class StudentGetByIdService implements IStudentGetByIdService {

    private final IStudentRepository studentRepository;

    @Override
    public Student findById(Long id) {
        return studentRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException(
                    NotFoundExceptionMesagges.resourceNotFound("Estudiante", String.valueOf(id))
                ));
    }

    @Override
    public Student findDeletedById(Long id) {
        return studentRepository.findByIdAndDeletedAtIsNotNull(id)
                .orElseThrow(() -> new NotFoundException(
                    NotFoundExceptionMesagges.resourceDeletedNotFound("Estudiante", String.valueOf(id))
                ));
    }

    
    
}
