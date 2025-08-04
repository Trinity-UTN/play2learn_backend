package trinity.play2learn.backend.profile.avatar.services.interfaces;

import java.io.IOException;

import trinity.play2learn.backend.profile.avatar.dtos.request.AspectRegisterRequestDto;
import trinity.play2learn.backend.profile.avatar.dtos.response.AspectResponseDto;

public interface IAspectRegisterService {
    
    public AspectResponseDto cu47registerAspect (AspectRegisterRequestDto dto) throws IOException;
    
}
