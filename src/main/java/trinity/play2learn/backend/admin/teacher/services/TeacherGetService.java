package trinity.play2learn.backend.admin.teacher.services;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.teacher.dtos.TeacherResponseDto;
import trinity.play2learn.backend.admin.teacher.mapper.TeacherMapper;
import trinity.play2learn.backend.admin.teacher.services.interfaces.IGetTeacherByIdService;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherGetService;

@Service
@AllArgsConstructor
public class TeacherGetService implements ITeacherGetService{
    private final IGetTeacherByIdService getTeacherByIdService;

    @Override
    public TeacherResponseDto cu28GetTeacherById(Long id) {
        return TeacherMapper.toDto(getTeacherByIdService.getTeacherById(id));
    }
}
