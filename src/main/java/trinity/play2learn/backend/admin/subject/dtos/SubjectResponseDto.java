package trinity.play2learn.backend.admin.subject.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import trinity.play2learn.backend.admin.course.dtos.CourseResponseDto;
import trinity.play2learn.backend.admin.teacher.dtos.TeacherResponseDto;

@Data
@Builder
@AllArgsConstructor
public class SubjectResponseDto {
    
    private Long id;
    private String name;
    private CourseResponseDto course;
    private TeacherResponseDto teacher;
    private Boolean optional;
    private Double actualBalance;
    private Double initialBalance;
}
