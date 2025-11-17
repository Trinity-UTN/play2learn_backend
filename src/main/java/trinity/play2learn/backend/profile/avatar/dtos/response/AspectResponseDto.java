package trinity.play2learn.backend.profile.avatar.dtos.response;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;
import trinity.play2learn.backend.profile.avatar.models.TypeAspect;

@Data
@Builder
public class AspectResponseDto {
    
    private Long id;
    private String name;
    private String image;
    private BigDecimal price;
    private TypeAspect type;
    private boolean available;
    private boolean bought;

}
