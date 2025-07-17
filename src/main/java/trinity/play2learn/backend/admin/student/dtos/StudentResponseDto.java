package trinity.play2learn.backend.admin.student.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.admin.course.dtos.CourseResponseDto;
import trinity.play2learn.backend.user.dtos.user.UserResponseDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponseDto {
    
    private Long id;

    private String name;

    private String lastname;

    private String dni;
    
    private UserResponseDto user;

    private CourseResponseDto course;

    private boolean active;

}
