package trinity.play2learn.backend.admin.subject.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import trinity.play2learn.backend.configs.messages.ValidationMessages;

@Data
@Builder
@AllArgsConstructor
public class SubjectRequestDto {

    @NotEmpty(message = ValidationMessages.NOT_EMPTY_NAME)
    @Size(max = 50, message = ValidationMessages.MAX_LENGTH_NAME_50)
    @Pattern(regexp = "^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\\s]+$", message = ValidationMessages.PATTERN_NAME)
    private String name;

    @NotNull(message = ValidationMessages.NOT_NULL_COURSE)
    private Long courseId;

    private Long teacherId; // Opcional

    @NotNull(message = ValidationMessages.NOT_NULL_OPTIONAL)
    private Boolean optional;
}
