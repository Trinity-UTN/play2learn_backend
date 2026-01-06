package trinity.play2learn.backend.activity.activity.dtos.activityCompleted;

import com.google.auto.value.AutoValue.Builder;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivityReviewNoLudicaRequestDto {

    @NotNull
    private Long activityCompletedId;

    @NotNull
    private Long studentId;

    @NotNull
    private ActivityCompletedState state;

    @NotNull
    private int score;

    @AssertTrue(message = "El estado debe ser APROBADO o DESAPROBADO")
    public boolean isApprovedOrDisapproved() {
        return state == ActivityCompletedState.APPROVED
            || state == ActivityCompletedState.DISAPPROVED;
    }
}
