package trinity.play2learn.backend.admin.teacher.services.interfaces;

import trinity.play2learn.backend.admin.teacher.dtos.TeacherRequestDto;
import trinity.play2learn.backend.admin.teacher.dtos.TeacherResponseDto;

public interface ITeacherRegisterService {
    
    TeacherResponseDto register(TeacherRequestDto teacher);
}
