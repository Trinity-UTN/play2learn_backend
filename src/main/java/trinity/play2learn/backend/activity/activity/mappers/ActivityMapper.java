package trinity.play2learn.backend.activity.activity.mappers;

import java.time.LocalDateTime;
import java.util.List;

import trinity.play2learn.backend.activity.activity.dtos.activityStudent.ActivityStudentApprovedResponseDto;
import trinity.play2learn.backend.activity.activity.dtos.activityStudent.ActivityStudentCountResponseDto;
import trinity.play2learn.backend.activity.activity.dtos.activityStudent.ActivityStudentGetResponseDto;
import trinity.play2learn.backend.activity.activity.dtos.activityStudent.ActivityStudentNotApprovedResponseDto;
import trinity.play2learn.backend.activity.activity.dtos.activityTeacher.ActivityTeacherGetResponseDto;
import trinity.play2learn.backend.activity.activity.dtos.activityTeacher.ActivityTeacherSimpleDto;
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

        public static ActivityTeacherSimpleDto toSimpleDto(Activity activity, ActivityStatus status, LocalDateTime date) {
                return ActivityTeacherSimpleDto.builder()
                                .id(activity.getId())
                                .name(activity.getName())
                                .description(activity.getDescription())
                                .subjectId(activity.getSubject().getId())
                                .subjectName(activity.getSubject().getName())
                                .courseId(activity.getSubject().getCourse().getId())
                                .course(activity.getSubject().getCourse().getFullName())
                                .yearId(activity.getSubject().getCourse().getYear().getId())
                                .status(status)
                                .date(date)
                                .build();
        }

        public static ActivityTeacherGetResponseDto toTeacherGetDto(Activity activity, ActivityStatus status, int studentsAttemptedCount, int studentsApprovedCount,
                        Double averageCompletionTime, Double participationPercentage, Double successPercentage,
                        List<ActivityStudentGetResponseDto> activityStudentGetDtos, Double reward) {
                return ActivityTeacherGetResponseDto.builder()
                                .id(activity.getId())
                                .name(activity.getName())
                                .description(activity.getDescription())
                                .startDate(activity.getStartDate())
                                .endDate(activity.getEndDate())
                                .status(status)
                                .difficulty(activity.getDifficulty())
                                .maxTime(activity.getMaxTime()) 
                                .subjectName(activity.getSubject().getName())
                                .courseName(activity.getSubject().getCourse().getFullName())
                                .attempts(activity.getAttempts())
                                .reward(reward == null ? 0.0 : (Math.round(reward * 100.0) / 100.0))
                                .typeReward(activity.getTypeReward())
                                .studentsAttemptedCount(studentsAttemptedCount)
                                .studentsApprovedCount(studentsApprovedCount)
                                .participationPercentage(participationPercentage)
                                .averageCompletionTime(averageCompletionTime)
                                .successPercentage(successPercentage)
                                .activityStudentGetDtos(activityStudentGetDtos)
                                .build();
        }
}
