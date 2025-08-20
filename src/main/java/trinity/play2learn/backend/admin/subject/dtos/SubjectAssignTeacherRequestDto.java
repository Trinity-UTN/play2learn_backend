package trinity.play2learn.backend.admin.subject.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubjectAssignTeacherRequestDto {
    
    @NotNull
    private Long subjectId;
    
    @NotNull
    private Long teacherId;
}
