package trinity.play2learn.backend.admin.teacher.dtos;

import trinity.play2learn.backend.user.dtos.user.UserResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeacherResponseDto {

    private Long id;

    private String name;

    private String lastname;

    private String dni;

    private UserResponseDto user;

    private boolean active;
}
