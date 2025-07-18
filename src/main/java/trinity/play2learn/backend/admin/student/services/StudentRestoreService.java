package trinity.play2learn.backend.admin.student.services;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.dtos.StudentResponseDto;
import trinity.play2learn.backend.admin.student.mappers.StudentMapper;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.repositories.IStudentRepository;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByIdService;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentRestoreService;

@Service
@AllArgsConstructor
public class StudentRestoreService implements IStudentRestoreService {
    
    private final IStudentGetByIdService studentGetByIdService;

    private final IStudentRepository studentRepository;
    
    @Override
    public StudentResponseDto cu38RestoreStudent(Long id) {
        
        Student student = studentGetByIdService.getDeleted(id);

        student.restore();

        return StudentMapper.toDto(studentRepository.save(student));
    }
    
}
