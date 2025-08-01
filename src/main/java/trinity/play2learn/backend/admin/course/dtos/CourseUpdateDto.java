package trinity.play2learn.backend.admin.course.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import trinity.play2learn.backend.configs.messages.ValidationMessages;

@Data
@Builder
@AllArgsConstructor
public class CourseUpdateDto {
    
    @NotEmpty(message = ValidationMessages.NOT_EMPTY_NAME)
    @Size(max = 50, message = ValidationMessages.MAX_LENGTH_NAME)
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = ValidationMessages.PATTERN_NAME)
    private String name;
}
