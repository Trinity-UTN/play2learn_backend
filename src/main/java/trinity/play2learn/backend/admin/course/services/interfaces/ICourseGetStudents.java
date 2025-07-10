package trinity.play2learn.backend.admin.course.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.admin.student.models.Student;

public interface ICourseGetStudents {
    
    List<Student> getStudentsByCourseId(Long courseId);
}
