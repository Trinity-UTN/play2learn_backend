package trinity.play2learn.backend.admin.student.services.interfaces;

import trinity.play2learn.backend.admin.student.models.Student;

public interface IStudentGetByEmailService {
    
    Student getByEmail(String email);
}
