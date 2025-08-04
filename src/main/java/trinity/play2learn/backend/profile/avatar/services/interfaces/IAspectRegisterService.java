package trinity.play2learn.backend.profile.avatar.services.interfaces;

import java.io.IOException;

import trinity.play2learn.backend.profile.avatar.dtos.request.AspectRegisterRequestDto;
import trinity.play2learn.backend.profile.avatar.dtos.response.AspectRegisterResponseDto;

public interface IAspectRegisterService {
    
    public AspectRegisterResponseDto cu47registerAspect (AspectRegisterRequestDto dto) throws IOException;
    
}
