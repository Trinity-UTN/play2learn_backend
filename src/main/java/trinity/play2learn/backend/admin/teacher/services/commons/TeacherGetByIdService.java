package trinity.play2learn.backend.admin.teacher.services.commons;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.teacher.repositories.ITeacherRepository;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherGetByIdService;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;
import trinity.play2learn.backend.configs.messages.NotFoundExceptionMesagges;

@Service
@AllArgsConstructor
public class TeacherGetByIdService implements ITeacherGetByIdService {
    
    private final ITeacherRepository teacherRepository;
    
    @Override
    public Teacher findById(Long id) {

        return teacherRepository.findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new NotFoundException(
                NotFoundExceptionMesagges.resourceNotFoundById("Docente", String.valueOf(id))
            ));
    }

    @Override
    public Teacher findDeletedById(Long id) {

        return teacherRepository.findByIdAndDeletedAtIsNotNull(id)
            .orElseThrow(() -> new NotFoundException(
                NotFoundExceptionMesagges.resourceDeletedNotFoundById("Docente", String.valueOf(id))
            ));
    }

    
}
