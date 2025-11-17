package trinity.play2learn.backend.admin.subject.services.interfaces;

import trinity.play2learn.backend.admin.course.models.Course;
import trinity.play2learn.backend.admin.student.models.Student;

public interface ISubjectAddStudentByCourseService {
    
    void addStudentByCourse(Course course, Student student);
}
