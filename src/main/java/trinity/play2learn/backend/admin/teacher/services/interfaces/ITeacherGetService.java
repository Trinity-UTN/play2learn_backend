package trinity.play2learn.backend.admin.teacher.services.interfaces;

import trinity.play2learn.backend.admin.teacher.dtos.TeacherResponseDto;

public interface ITeacherGetService {
    
    TeacherResponseDto cu28GetTeacherById(Long id);
}
