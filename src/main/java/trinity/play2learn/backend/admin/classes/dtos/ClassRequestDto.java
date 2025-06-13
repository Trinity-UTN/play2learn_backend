package trinity.play2learn.backend.admin.classes.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassRequestDto {

    @NotEmpty(message = "Name is required.")
    @Size(max = 50, message = "Maximum length for name is 50 characters.")
    @Pattern(regexp = "^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\\s]+$", message = "Name can only contain letters, numbers, spaces, and the characters áéíóúÁÉÍÓÚñÑ.")
    private String name;

    @NotNull(message = "Year ID is required.")
    private Long year_id;
    
}
