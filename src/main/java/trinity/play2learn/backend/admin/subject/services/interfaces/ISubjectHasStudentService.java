package trinity.play2learn.backend.admin.subject.services.interfaces;

import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.subject.models.Subject;

public interface ISubjectHasStudentService {
    
    void subjectHasStudent(Subject subject, Student student);
    
}
