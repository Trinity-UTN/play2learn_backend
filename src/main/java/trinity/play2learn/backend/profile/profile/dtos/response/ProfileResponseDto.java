package trinity.play2learn.backend.profile.profile.dtos.response;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import trinity.play2learn.backend.profile.avatar.dtos.response.AspectResponseDto;

@Data
@Builder
public class ProfileResponseDto {
    
    private Long id;
    private AspectResponseDto selectedBody;
    private AspectResponseDto selectedShirt;
    private AspectResponseDto selectedHat;
    private List<AspectResponseDto> ownedAspects;
    
}
