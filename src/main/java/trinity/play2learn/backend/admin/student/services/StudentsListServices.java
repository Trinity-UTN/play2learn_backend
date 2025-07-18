package trinity.play2learn.backend.admin.student.services;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.dtos.StudentResponseDto;
import trinity.play2learn.backend.admin.student.mappers.StudentMapper;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.repositories.IStudentRepository;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentListService;

@Service
@AllArgsConstructor
public class StudentsListServices implements IStudentListService {
    
    private final IStudentRepository studentRepository;
    
    @Override
    public List<StudentResponseDto> cu20ListStudents() {
        return StudentMapper.toListDto((List<Student>) studentRepository.findAll());
    }
    
}
