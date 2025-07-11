package trinity.play2learn.backend.admin.student.services.commons;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.repositories.IStudentRepository;
import trinity.play2learn.backend.admin.student.services.interfaces.IGetStudentsByCourse;

@Service
@AllArgsConstructor
public class GetStudentsByCourse implements IGetStudentsByCourse{
    
    private final IStudentRepository studentRepository;
    
    @Override
    public List<Student> getStudentsByCourseId(Long courseId) {
        return studentRepository.findByCourseId(courseId);
    }
    
}
