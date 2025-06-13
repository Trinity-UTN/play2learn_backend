package trinity.play2learn.backend.admin.classes.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.admin.year.dtos.YearResponseDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassResponseDto {

    private Long id;
    private String name;
    private YearResponseDto year;
    
}
