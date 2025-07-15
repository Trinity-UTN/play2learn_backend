package trinity.play2learn.backend.admin.student.services.interfaces;

import trinity.play2learn.backend.admin.student.dtos.StudentResponseDto;
import trinity.play2learn.backend.admin.student.dtos.StudentUpdateRequestDto;

public interface IStudentUpdateService {
    
    public StudentResponseDto cu18updateStudent (StudentUpdateRequestDto dto);
    
}
