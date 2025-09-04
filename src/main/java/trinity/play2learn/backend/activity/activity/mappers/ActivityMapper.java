package trinity.play2learn.backend.activity.activity.mappers;

import trinity.play2learn.backend.activity.activity.dtos.activityStudent.ActivityStudentNotApprovedResponseDto;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activity.ActivityStatus;

public class ActivityMapper {
    
    public static ActivityStudentNotApprovedResponseDto toStudentDto(
            Activity activity , Integer remainingAttempts, Boolean pending,
            ActivityStatus status, Double minReward, Double maxReward
        ) {

        return ActivityStudentNotApprovedResponseDto.builder()
            .id(activity.getId())
            .name(activity.getName())
            .description(activity.getDescription())
            .startDate(activity.getStartDate())
            .endDate(activity.getEndDate())
            .dificulty(activity.getDificulty())
            .maxTime(activity.getMaxTime())
            .subjectName(activity.getSubject().getName())
            .attempts(activity.getAttempts())
            .remainingAttempts(remainingAttempts)
            .status(status)
            .minReward(minReward)
            .maxReward(maxReward)
            .pending(pending)
            .build();
    }
}
