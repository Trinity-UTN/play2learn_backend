package trinity.play2learn.backend.activity.activity.dtos.activityCompleted;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivityStudentResultsResponseDto {
    
    private Long id;
    private Long activityId;
    private ActivityCompletedState state;
    private int attempts;
    private Double reward;
    private long completedTimeInSeconds;
    private int score;
    private int correctAnswers;
    private int incorrectAnswers;
    private int unanswered;
}
