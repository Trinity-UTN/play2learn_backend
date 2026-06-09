package trinity.play2learn.backend.configs.seed.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeedCredentialsDto {

    private String email;

    private String password;

    private String role;

    private String dni;

    private String name;

    private String yearName;

    private String courseName;
}
