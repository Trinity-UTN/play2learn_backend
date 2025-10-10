package trinity.play2learn.backend.activity.activity.mappers;

import trinity.play2learn.backend.activity.activity.dtos.activityCompleted.ActivityCompletedResponseDto;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompleted;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.NoLudicaAttempt;
import trinity.play2learn.backend.admin.student.models.Student;

public class ActivityCompletedMapper {
    
    public static ActivityCompleted toModel(Activity activity, Student student, Double reward, Integer remainingAttempts, ActivityCompletedState state, NoLudicaAttempt noLudicaAttempt) {

        return ActivityCompleted.builder()
            .activity(activity)
            .student(student)
            .reward(reward)
            .remainingAttempts(remainingAttempts)
            .state(state)
            .noLudicaAttempt(noLudicaAttempt)
            .build();
    }

    public static ActivityCompletedResponseDto toDto(ActivityCompleted activityCompleted) {
        return ActivityCompletedResponseDto.builder()
            .id(activityCompleted.getId())
            .activityId(activityCompleted.getActivity().getId())
            .state(activityCompleted.getState())
            .reward(activityCompleted.getReward() == null ? 0.0 : (Math.round(activityCompleted.getReward() * 100.0) / 100.0))
            .remainingAttempts(activityCompleted.getRemainingAttempts())
            .build();
    }

}
