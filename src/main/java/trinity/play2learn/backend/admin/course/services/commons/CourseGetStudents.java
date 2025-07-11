package trinity.play2learn.backend.admin.course.services.commons;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.course.services.interfaces.ICourseGetStudents;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.repositories.IStudentRepository;

@Service
@AllArgsConstructor
public class CourseGetStudents implements ICourseGetStudents{
    
    private final IStudentRepository studentRepository;
    
    @Override
    public List<Student> getStudentsByCourseId(Long courseId) {
        return studentRepository.findByCourseId(courseId);
    }
    
}
