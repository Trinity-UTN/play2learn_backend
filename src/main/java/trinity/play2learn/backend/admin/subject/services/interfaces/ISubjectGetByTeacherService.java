package trinity.play2learn.backend.admin.subject.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.teacher.models.Teacher;

public interface ISubjectGetByTeacherService {
    
    List<Subject> getSubjectsByTeacher(Teacher teacher);
}
