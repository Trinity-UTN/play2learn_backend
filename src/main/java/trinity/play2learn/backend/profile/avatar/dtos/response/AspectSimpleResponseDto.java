package trinity.play2learn.backend.profile.avatar.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.profile.avatar.models.TypeAspect;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AspectSimpleResponseDto {
    
    private String image;

    private TypeAspect type;
}
