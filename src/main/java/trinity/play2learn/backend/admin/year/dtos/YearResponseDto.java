package trinity.play2learn.backend.admin.year.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YearResponseDto {
    private Long id;
    private String name;
}
