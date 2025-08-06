package trinity.play2learn.backend.profile.profile.services.interfaces;

import trinity.play2learn.backend.admin.student.dtos.StudentResponseDto;

public interface IProfileSelectAspectService {
    
    public StudentResponseDto cu54selectAspect (Long profileId, Long aspectId);
    
}
