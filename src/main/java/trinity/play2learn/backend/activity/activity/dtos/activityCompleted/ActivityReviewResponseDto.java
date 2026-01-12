package trinity.play2learn.backend.activity.activity.dtos.activityCompleted;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ActivityReviewResponseDto {
    
    private Integer score;
    private String comment;
    
}
