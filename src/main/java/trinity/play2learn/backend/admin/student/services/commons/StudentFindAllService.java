package trinity.play2learn.backend.admin.student.services.commons;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.repositories.IStudentRepository;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentFindAllService;

@Service
@AllArgsConstructor
public class StudentFindAllService implements IStudentFindAllService {
    
    private final IStudentRepository studentRepository;
    
    @Override
    public List<Student> findAll() {
        return studentRepository.findAllByDeletedAtIsNull();
    }
}
