package trinity.play2learn.backend.student.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.dtos.StudentResponseDto;
import trinity.play2learn.backend.admin.student.mappers.StudentMapper;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.student.services.interfaces.IStudentGetByTokenService;
import trinity.play2learn.backend.user.models.User;

@Service
@AllArgsConstructor
public class StudentGetByTokenService implements IStudentGetByTokenService{

    private final IStudentGetByEmailService studentGetByEmailService;

    @Override
    @Transactional(readOnly = true)
    public StudentResponseDto cu71GetStudentByToken(User user) {
    
        Student student =  studentGetByEmailService.getByEmail(user.getEmail());

        return StudentMapper.toDto(student);
    }
    
    
}
