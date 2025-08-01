package trinity.play2learn.backend.activity.ordenarSecuencia.dtos.request;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import trinity.play2learn.backend.configs.messages.ValidationMessages;

@Data
public class EventRequestDto {

    @NotBlank(message = ValidationMessages.NOT_EMPTY_NAME)
    @Size(max = 50, message = ValidationMessages.MAX_LENGTH_NAME)
    private String name;

    @NotBlank(message = ValidationMessages.NOT_EMPTY_DESCRIPTION)
    @Size(max = 100, message = ValidationMessages.MAX_LENGTH_DESCRIPTION_100)
    private String description;

    @Min(value = 0, message = ValidationMessages.MIN_ORDER)
    private Integer order;

    private String imageKey;

    private MultipartFile image;
    
}
