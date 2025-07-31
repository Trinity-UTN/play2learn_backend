package trinity.play2learn.backend.admin.student.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.configs.messages.ValidationMessages;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentUpdateRequestDto {

    @NotEmpty(message = ValidationMessages.NOT_EMPTY_NAME)
    @Size(max = 50, message = ValidationMessages.MAX_LENGTH_NAME)
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = ValidationMessages.PATTERN_NAME)
    private String name;

    @NotEmpty(message = ValidationMessages.NOT_EMPTY_LASTNAME)
    @Size(max = 50, message = ValidationMessages.MAX_LENGTH_LASTNAME)
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = ValidationMessages.PATTERN_LASTNAME)
    private String lastname;

    @NotEmpty(message = ValidationMessages.NOT_EMPTY_DNI)
    @Pattern(regexp = "^[0-9]{8}$", message = ValidationMessages.PATTERN_DNI)
    private String dni;

    @NotNull(message = ValidationMessages.NOT_NULL_COURSE)
    private Long course_id;   
}
