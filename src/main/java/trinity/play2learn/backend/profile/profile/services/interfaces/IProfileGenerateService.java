package trinity.play2learn.backend.profile.profile.services.interfaces;

import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.profile.profile.dtos.response.ProfileResponseDto;

public interface IProfileGenerateService {
    
    public ProfileResponseDto cu52generateProfile (Student student);
}
