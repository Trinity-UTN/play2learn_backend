package trinity.play2learn.backend.activity.memorama.dtos;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.configs.messages.ValidationMessages;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouplesMemoramaRequestDto {
    @NotEmpty(message = ValidationMessages.NOT_NULL_CONCEPT)
    @Size(max = 50, message = ValidationMessages.MAX_LENGTH_CONCEPT)
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = ValidationMessages.PATTERN_CONCEPT)
    private String concept;

    @NotNull(message = ValidationMessages.NOT_NULL_IMAGE)
    private MultipartFile image;
}
