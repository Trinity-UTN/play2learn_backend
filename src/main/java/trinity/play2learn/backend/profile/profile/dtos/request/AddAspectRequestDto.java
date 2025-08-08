package trinity.play2learn.backend.profile.profile.dtos.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddAspectRequestDto {
    
    private Long aspectId;
    private Long profileId;

}
