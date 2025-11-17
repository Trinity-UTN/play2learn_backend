package trinity.play2learn.backend.admin.student.services;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.dtos.StudentResponseDto;
import trinity.play2learn.backend.admin.student.mappers.StudentMapper;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByIdService;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetService;

@Service
@AllArgsConstructor
public class StudentGetService implements IStudentGetService {

    private final IStudentGetByIdService studentGetByIdService;

    @Override
    public StudentResponseDto cu22GetStudent(Long id) {
        return StudentMapper.toDto(studentGetByIdService.findById(id));
    }
    
}
