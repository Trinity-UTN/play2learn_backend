package trinity.play2learn.backend.admin.student.services.interfaces;

import trinity.play2learn.backend.admin.student.models.Student;

public interface IStudentGetByIdService {

    public Student findById (Long id);

    public Student findDeletedById (Long id);
    
}
