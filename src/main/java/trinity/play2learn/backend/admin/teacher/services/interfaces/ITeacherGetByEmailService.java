package trinity.play2learn.backend.admin.teacher.services.interfaces;

import trinity.play2learn.backend.admin.teacher.models.Teacher;

public interface ITeacherGetByEmailService {
    
    Teacher getByEmail(String email);
}
