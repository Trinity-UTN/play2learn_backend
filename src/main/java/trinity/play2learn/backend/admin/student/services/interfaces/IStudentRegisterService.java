package trinity.play2learn.backend.admin.student.services.interfaces;

import trinity.play2learn.backend.admin.student.dtos.StudentRequestDto;
import trinity.play2learn.backend.admin.student.dtos.StudentResponseDto;

public interface IStudentRegisterService {

    public StudentResponseDto cu4registerStudent (StudentRequestDto studentRequestDto);
    
}
