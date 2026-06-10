package trinity.play2learn.backend.activity.activity.dtos.activityCompleted;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;
import trinity.play2learn.backend.activity.noLudica.dtos.response.NoLudicaResponseDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivityCompletedPendingDto {
    
    private Long activityCompletedId;
    private ActivityCompletedState state; //PENDING
    private String studentName;
    private String studentLastName;
    private LocalDateTime completedAt;
    private NoLudicaResponseDto activityDto;
}
