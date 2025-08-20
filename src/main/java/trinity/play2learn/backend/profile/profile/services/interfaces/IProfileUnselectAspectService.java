package trinity.play2learn.backend.profile.profile.services.interfaces;

import trinity.play2learn.backend.admin.student.dtos.StudentResponseDto;
import trinity.play2learn.backend.profile.profile.dtos.request.UnselectAspectRequestDto;

public interface IProfileUnselectAspectService {
    
    public StudentResponseDto cu59unselectAspect (UnselectAspectRequestDto dto);
    
}
