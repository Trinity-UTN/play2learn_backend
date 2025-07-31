package trinity.play2learn.backend.admin.teacher.services.interfaces;

import trinity.play2learn.backend.admin.teacher.models.Teacher;

public interface ITeacherGetByIdService {
    
    Teacher findById(Long id);

    Teacher findDeletedById(Long id);
}
