package trinity.play2learn.backend.admin.teacher.services.interfaces;

import trinity.play2learn.backend.admin.teacher.models.Teacher;

public interface IGetTeacherByIdService {
    
    Teacher getTeacherById(Long id);

    Teacher getEliminatedTeacherById(Long id);
}
