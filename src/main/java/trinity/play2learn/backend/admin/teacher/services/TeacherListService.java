package trinity.play2learn.backend.admin.teacher.services;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.teacher.dtos.TeacherResponseDto;
import trinity.play2learn.backend.admin.teacher.mapper.TeacherMapper;
import trinity.play2learn.backend.admin.teacher.repositories.ITeacherRepository;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherListService;

@Service
@AllArgsConstructor
public class TeacherListService implements ITeacherListService {
    
    private final ITeacherRepository teacherRepository;

    @Override
    public List<TeacherResponseDto> cu25ListTeachers() {
        return TeacherMapper.toListDto(teacherRepository.findAllByDeletedAtIsNull());
    }
}
