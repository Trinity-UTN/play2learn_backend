package trinity.play2learn.backend.admin.teacher.services;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.teacher.dtos.TeacherResponseDto;
import trinity.play2learn.backend.admin.teacher.dtos.TeacherUpdateDto;
import trinity.play2learn.backend.admin.teacher.mapper.TeacherMapper;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.teacher.repositories.ITeacherRepository;
import trinity.play2learn.backend.admin.teacher.services.interfaces.IGetTeacherByIdService;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherExistsByDniService;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherUpdateService;

@Service
@AllArgsConstructor
public class TeacherUpdateService implements ITeacherUpdateService{
    
    private final ITeacherRepository teacherRepository;
    private final IGetTeacherByIdService getTeacherByIdService;
    private final ITeacherExistsByDniService teacherExistsByDniService;
    @Override
    public TeacherResponseDto cu23UpdateTeacher(Long id, TeacherUpdateDto teacherDto) {

        Teacher teacherDb = getTeacherByIdService.getTeacherById(id); //Lanza un NotFound si no encuentra un docente con el id proporcionado

        //Lanza un Conflict si existe un profesor con el mismo dni y no es el del id.
        teacherExistsByDniService.validate(teacherDto.getDni(), id); 

        Teacher teacherToSave = TeacherMapper.toUpdateModel(id, teacherDto, teacherDb.getUser());

        return TeacherMapper.toDto(teacherRepository.save(teacherToSave));
    }
}
