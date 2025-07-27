package trinity.play2learn.backend.activity.ordenarSecuencia.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventResponseDto {
    private Long id;
    private String name;
    private String description;
    private String image;
    private Integer order;
}
