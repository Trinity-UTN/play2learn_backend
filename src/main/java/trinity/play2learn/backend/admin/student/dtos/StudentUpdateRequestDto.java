package trinity.play2learn.backend.admin.student.dtos;

import jakarta.validation.constraints.Email;
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
public class StudentUpdateRequestDto {

    @NotNull(message = "ID is required.")
    private Long id;

    @NotEmpty(message = "Name is required.")
    @Size(max = 50, message = "Maximum length for name is 50 characters.")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "Name can only contain letters, spaces, and the characters áéíóúÁÉÍÓÚñÑ.")
    private String name;

    @NotEmpty(message = "Lastname is required.")
    @Size(max = 50, message = "Maximum length for Lastname is 50 characters.")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "Lastname can only contain letters, spaces, and the characters áéíóúÁÉÍÓÚñÑ.")
    private String lastname;

    @NotEmpty(message = "DNI is required.")
    @Pattern(regexp = "^[0-9]{8}$", message = "DNI must be exactly 8 digits.")
    private String dni;

    @NotEmpty(message = "Email is required.")
    @Size(max = 100, message = "Maximum length for email is 100 characters.")
    @Email(message = "Email must be a valid email address.")
    private String email;

    @NotNull(message = "Course ID is required.")
    private Long course_id;    
}
