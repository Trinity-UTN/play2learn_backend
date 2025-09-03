package trinity.play2learn.backend.admin.subject.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.subject.models.Subject;

public interface ISubjectGetByStudentService {
    
    List<Subject> getByStudent(Student student);
}
