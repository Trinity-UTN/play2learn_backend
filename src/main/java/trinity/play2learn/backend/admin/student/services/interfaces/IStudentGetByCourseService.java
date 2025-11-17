package trinity.play2learn.backend.admin.student.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.admin.student.models.Student;

public interface IStudentGetByCourseService {
    
    List<Student> getStudentsByCourseId(Long courseId);
}
