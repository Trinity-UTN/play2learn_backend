package trinity.play2learn.backend.activity.activity.mappers;

import java.time.Duration;

import trinity.play2learn.backend.activity.activity.dtos.activityCompleted.ActivityCompletedResponseDto;
import trinity.play2learn.backend.activity.activity.dtos.activityCompleted.ActivityStudentResultsResponseDto;
import trinity.play2learn.backend.activity.activity.dtos.activityStudent.ActivityStudentGetResponseDto;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompleted;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.NoLudicaAttempt;
import trinity.play2learn.backend.admin.student.models.Student;

public class ActivityCompletedMapper {
    
    public static ActivityCompleted toModel(
        Activity activity, Student student, Double reward, Integer remainingAttempts, ActivityCompletedState state, 
        NoLudicaAttempt noLudicaAttempt, int score, int correctAnswers, int incorrectAnswers, int unanswered) {

        return ActivityCompleted.builder()
            .activity(activity)
            .student(student)
            .reward(reward)
            .remainingAttempts(remainingAttempts)
            .state(state)
            .noLudicaAttempt(noLudicaAttempt)
            .score(score)
            .correctAnswers(correctAnswers)
            .incorrectAnswers(incorrectAnswers)
            .unanswered(unanswered)
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

    public static ActivityStudentGetResponseDto toStudentGetDto(
        String studentName, ActivityCompletedState state, int attempts, Double reward) {

        return ActivityStudentGetResponseDto.builder()
            .studentName(studentName)
            .state(state)
            .attempts(attempts)
            .reward(reward == null ? 0.0 : (Math.round(reward * 100.0) / 100.0))
            .build();
    }

    public static ActivityStudentResultsResponseDto toStudentResultsDto(
        ActivityCompleted activityCompleted, int attempts) {

        return ActivityStudentResultsResponseDto.builder()
            .id(activityCompleted.getId())
            .activityId(activityCompleted.getActivity().getId())
            .state(activityCompleted.getState())
            .attempts(attempts)
            .reward(activityCompleted.getReward() == null ? 0.0 : (Math.round(activityCompleted.getReward() * 100.0) / 100.0))
            .completedTimeInSeconds(Duration.between(activityCompleted.getStartedAt(), activityCompleted.getCompletedAt()).getSeconds())
            .score(activityCompleted.getScore())
            .correctAnswers(activityCompleted.getCorrectAnswers())
            .incorrectAnswers(activityCompleted.getIncorrectAnswers())
            .unanswered(activityCompleted.getUnanswered())
            .build();
    }
}
