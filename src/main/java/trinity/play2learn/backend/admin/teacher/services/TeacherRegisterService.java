package trinity.play2learn.backend.admin.teacher.services;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.teacher.dtos.TeacherRequestDto;
import trinity.play2learn.backend.admin.teacher.dtos.TeacherResponseDto;
import trinity.play2learn.backend.admin.teacher.mapper.TeacherMapper;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.teacher.repositories.ITeacherRepository;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherExistsByDniService;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherRegisterService;
import trinity.play2learn.backend.user.models.Role;
import trinity.play2learn.backend.user.models.User;
import trinity.play2learn.backend.user.services.user.interfaces.IUserCreateService;

@Service
@AllArgsConstructor
public class TeacherRegisterService implements ITeacherRegisterService {

    private final IUserCreateService userCreateService;
    private final ITeacherRepository teacherRepository;
    private final ITeacherExistsByDniService teacherExistsByDniService;

    @Override
    public TeacherResponseDto register(TeacherRequestDto teacherDto) {
        
        //Lanza un Conflict si ya existe un docente con el mismo dni
        teacherExistsByDniService.validate(teacherDto.getDni());

        User userSaved = userCreateService.create(teacherDto.getEmail(), teacherDto.getDni(), Role.ROLE_TEACHER);

        Teacher teacherToSave = TeacherMapper.toModel(teacherDto, userSaved);

        return TeacherMapper.toDto(teacherRepository.save(teacherToSave)); 

    }
    

}
