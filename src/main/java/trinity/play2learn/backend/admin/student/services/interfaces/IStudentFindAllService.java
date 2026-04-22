package trinity.play2learn.backend.admin.student.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.admin.student.models.Student;

public interface IStudentFindAllService {
    
    List<Student> findAll();
}
