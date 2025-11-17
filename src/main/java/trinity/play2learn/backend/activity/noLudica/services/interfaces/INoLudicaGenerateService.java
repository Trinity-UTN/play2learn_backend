package trinity.play2learn.backend.activity.noLudica.services.interfaces;

import trinity.play2learn.backend.activity.noLudica.dtos.request.NoLudicaRequestDto;
import trinity.play2learn.backend.activity.noLudica.dtos.response.NoLudicaResponseDto;
import trinity.play2learn.backend.user.models.User;

public interface INoLudicaGenerateService {
    
    public NoLudicaResponseDto cu45GenerateNoLudica (NoLudicaRequestDto dto, User user);
    
}
