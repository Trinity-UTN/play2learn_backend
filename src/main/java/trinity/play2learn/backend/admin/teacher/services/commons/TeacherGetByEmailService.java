package trinity.play2learn.backend.admin.teacher.services.commons;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.teacher.repositories.ITeacherRepository;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherGetByEmailService;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;
import trinity.play2learn.backend.configs.messages.NotFoundExceptionMesagges;

@Service
@AllArgsConstructor
public class TeacherGetByEmailService implements ITeacherGetByEmailService {
    
    private final ITeacherRepository teacherRepository;

    @Override
    public Teacher getByEmail(String email) {

        return teacherRepository.findByUserEmailAndDeletedAtIsNull(email)
            .orElseThrow( () -> new NotFoundException(NotFoundExceptionMesagges.resourceNotFoundByEmail("Teacher", email)) );
    }
    
    
}
