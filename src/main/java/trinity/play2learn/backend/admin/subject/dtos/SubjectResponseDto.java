package trinity.play2learn.backend.admin.subject.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import trinity.play2learn.backend.admin.course.dtos.CourseResponseDto;
import trinity.play2learn.backend.admin.student.dtos.StudentSimplificatedResponse;
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
    private List<StudentSimplificatedResponse> students;

}
