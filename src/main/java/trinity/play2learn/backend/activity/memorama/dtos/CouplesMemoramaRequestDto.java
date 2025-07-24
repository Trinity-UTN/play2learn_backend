package trinity.play2learn.backend.activity.memorama.dtos;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouplesMemoramaRequestDto {
    @NotEmpty(message = "Concepto is required.")
    @Size(max = 50, message = "Maximum length for concepto is 50 characters.")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "Concepto can only contain letters, spaces, and the characters áéíóúÁÉÍÓÚñÑ.")
    private String concept;

    @NotNull(message = "Imagen is required.")
    private MultipartFile image;
}
