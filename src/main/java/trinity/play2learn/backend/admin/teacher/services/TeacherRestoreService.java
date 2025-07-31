package trinity.play2learn.backend.admin.teacher.services;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.teacher.dtos.TeacherResponseDto;
import trinity.play2learn.backend.admin.teacher.mapper.TeacherMapper;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.teacher.repositories.ITeacherRepository;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherGetByIdService;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherRestoreService;

@Service
@AllArgsConstructor
public class TeacherRestoreService implements ITeacherRestoreService{
    
    private final ITeacherRepository teacherRepository;
    private final ITeacherGetByIdService getTeacherByIdService;

    @Override
    public TeacherResponseDto cu35RestoreTeacher(Long id) {

        Teacher teacher = getTeacherByIdService.findDeletedById(id);

        teacher.restore();

        return TeacherMapper.toDto(teacherRepository.save(teacher));
    }
    

}
