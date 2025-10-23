package trinity.play2learn.backend.activity.activity.mappers;

import java.time.LocalDateTime;

import trinity.play2learn.backend.activity.activity.dtos.activityStudent.ActivityStudentApprovedResponseDto;
import trinity.play2learn.backend.activity.activity.dtos.activityStudent.ActivityStudentCountResponseDto;
import trinity.play2learn.backend.activity.activity.dtos.activityStudent.ActivityStudentNotApprovedResponseDto;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activity.ActivityStatus;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;

public class ActivityMapper {

        public static ActivityStudentNotApprovedResponseDto toNotApprovedDto(
                        Activity activity, Integer remainingAttempts, Boolean pending,
                        ActivityStatus status, Double minReward, Double maxReward) {

                return ActivityStudentNotApprovedResponseDto.builder()
                                .id(activity.getId())
                                .name(activity.getName())
                                .description(activity.getDescription())
                                .startDate(activity.getStartDate())
                                .endDate(activity.getEndDate())
                                .difficulty(activity.getDifficulty())
                                .maxTime(activity.getMaxTime())
                                .subjectId(activity.getSubject().getId())
                                .subjectName(activity.getSubject().getName())
                                .attempts(activity.getAttempts())
                                .remainingAttempts(remainingAttempts)
                                .status(status)
                                .minReward(minReward)
                                .maxReward(maxReward)
                                .pending(pending)
                                .build();
        }

        public static ActivityStudentApprovedResponseDto toApprovedDto(Activity activity, Integer remainingAttempts,
                        Double reward, LocalDateTime completedAt) {

                return ActivityStudentApprovedResponseDto.builder()
                                .id(activity.getId())
                                .name(activity.getName())
                                .description(activity.getDescription())
                                .difficulty(activity.getDifficulty())
                                .subjectId(activity.getSubject().getId())
                                .subjectName(activity.getSubject().getName())
                                .attempts(activity.getAttempts())
                                .remainingAttempts(remainingAttempts)
                                .completedAt(completedAt)
                                .reward(reward)
                                .state(ActivityCompletedState.APPROVED)
                                .build();
        }

        public static ActivityStudentCountResponseDto toCountDto(Integer available, Integer approved,
                        Integer disapproved, Integer expired) {
                return ActivityStudentCountResponseDto.builder()
                                .available(available)
                                .approved(approved)
                                .disapproved(disapproved)
                                .expired(expired)
                                .build();
        }
}
