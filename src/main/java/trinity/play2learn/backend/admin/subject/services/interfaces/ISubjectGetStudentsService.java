package trinity.play2learn.backend.admin.subject.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.admin.student.models.Student;

public interface ISubjectGetStudentsService {
    List<Student> findStudentsBySubjectId(Long subjectId);
}
