package trinity.play2learn.backend.activity.ordenarSecuencia.dtos.request;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EventRequestDto {

    @NotBlank(message = "El nombre del evento es obligatorio")
    @Size(max = 50, message = "El nombre no puede superar los 50 caracteres")
    private String name;

    @NotBlank(message = "La descripción del evento es obligatoria")
    @Size(max = 100, message = "La descripción no puede superar los 100 caracteres")
    private String description;

    @Min(value = 0, message = "El orden debe ser mayor o igual a 0")
    private Integer order;

    private String imageKey;

    private MultipartFile image;
    
}
