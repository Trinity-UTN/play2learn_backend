package trinity.play2learn.backend.admin.teacher.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.admin.course.dtos.CourseResponseDto;
import trinity.play2learn.backend.admin.subject.dtos.SubjectSimplifiedResponseDto;
import trinity.play2learn.backend.admin.year.dtos.YearResponseDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeacherSubjectsDto {
    
    private List<SubjectSimplifiedResponseDto> subjects;
    private List<CourseResponseDto> courses;
    private List<YearResponseDto> years;
}
