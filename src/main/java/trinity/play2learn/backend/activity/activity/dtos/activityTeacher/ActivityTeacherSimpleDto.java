package trinity.play2learn.backend.activity.activity.dtos.activityTeacher;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.activity.activity.models.activity.ActivityStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivityTeacherSimpleDto {
    
    private Long id;
    private String name;
    private String description;
    private Long subjectId;
    private String subjectName;
    private Long courseId;
    private String course;
    private Long yearId;
    private ActivityStatus status;
    private LocalDateTime date; //Puede ser la fecha de creacion o la fecha de expiracion dependiendo de status
}
