package trinity.play2learn.backend.student.services.interfaces;

import trinity.play2learn.backend.admin.student.dtos.StudentResponseDto;
import trinity.play2learn.backend.user.models.User;

public interface IStudentGetByTokenService {
    
    StudentResponseDto cu71GetStudentByToken(User user);
}
