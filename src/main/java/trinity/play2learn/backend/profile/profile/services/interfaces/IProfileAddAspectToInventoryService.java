package trinity.play2learn.backend.profile.profile.services.interfaces;

import trinity.play2learn.backend.admin.student.dtos.StudentResponseDto;

public interface IProfileAddAspectToInventoryService {
    
    public StudentResponseDto cu53addAspectToInventory (Long aspectId,  Long profileId);
    
}
