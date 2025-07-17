package trinity.play2learn.backend.admin.teacher.services.commons;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.teacher.repositories.ITeacherRepository;
import trinity.play2learn.backend.admin.teacher.services.interfaces.IGetTeacherByIdService;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;

@Service
@AllArgsConstructor
public class GetTeacherByIdService implements IGetTeacherByIdService {
    
    private final ITeacherRepository teacherRepository;
    
    @Override
    public Teacher getTeacherById(Long id) {

        return teacherRepository.findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new NotFoundException("Teacher not found with id: " + id));
    }

    @Override
    public Teacher getEliminatedTeacherById(Long id) {

        return teacherRepository.findByIdAndDeletedAtIsNotNull(id)
            .orElseThrow(() -> new NotFoundException("Teacher not found or not eliminated with id: " + id));
    }

    
}
