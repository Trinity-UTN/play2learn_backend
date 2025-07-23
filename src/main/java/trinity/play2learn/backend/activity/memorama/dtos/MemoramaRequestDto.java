package trinity.play2learn.backend.activity.memorama.dtos;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.activity.activity.dtos.ActivityRequestDto;

@Data
@EqualsAndHashCode(callSuper = true) //Esta notacion es necesaria para que el equals y el hashcode hereden de la clase padre (Sino @Data se pone en amarillo)
@AllArgsConstructor
@NoArgsConstructor
public class MemoramaRequestDto extends ActivityRequestDto {

    @Valid
    @NotEmpty(message = "Debe haber al menos una pareja")
    @Size(min = 4, max = 8, message = "La cantidad de parejas debe estar entre 4 y 8")
    private List<CouplesMemoramaRequestDto> couples;
    
}
