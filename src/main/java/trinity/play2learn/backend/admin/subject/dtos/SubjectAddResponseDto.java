package trinity.play2learn.backend.admin.subject.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import trinity.play2learn.backend.admin.student.dtos.StudentSimplificatedResponse;

@Data
@Builder
@AllArgsConstructor
public class SubjectAddResponseDto {
    
    private Long id;
    private String subjectName;
    private String courseName;
    private List<StudentSimplificatedResponse> students;
}
