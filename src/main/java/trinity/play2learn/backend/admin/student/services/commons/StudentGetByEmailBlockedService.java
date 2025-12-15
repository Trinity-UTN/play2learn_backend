package trinity.play2learn.backend.admin.student.services.commons;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.repositories.IStudentRepository;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailBlockedService;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;
import trinity.play2learn.backend.configs.messages.NotFoundExceptionMesagges;

@Service
@AllArgsConstructor
public class StudentGetByEmailBlockedService implements IStudentGetByEmailBlockedService{

    private final IStudentRepository studentRepository;

    @Override
    public Student getByEmailBlocked(String email) {
        return studentRepository.findByUserEmailForUpdateAndDeletedAtIsNull(email)
                .orElseThrow(() -> new NotFoundException(
                        NotFoundExceptionMesagges.resourceNotFoundByEmail("Estudiante", email)));
    }   

}
