package trinity.play2learn.backend.profile.avatar.dtos.request;

import java.math.BigDecimal;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.configs.messages.ValidationMessages;
import trinity.play2learn.backend.profile.avatar.models.TypeAspect;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AspectRegisterRequestDto {

    @NotEmpty(message = ValidationMessages.NOT_EMPTY_NAME)
    @Size(max = 50, message = ValidationMessages.MAX_LENGTH_NAME)
    @Pattern(regexp = "^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\\s]+$", message = ValidationMessages.PATTERN_NAME)
    private String name;

    @NotNull(message = ValidationMessages.NOT_NULL_IMAGE)
    private MultipartFile image;

    @NotNull(message = ValidationMessages.NOT_NULL_PRICE)
    private BigDecimal price;

    @NotNull(message = ValidationMessages.NOT_NULL_TYPE)
    private TypeAspect type;
    
}
