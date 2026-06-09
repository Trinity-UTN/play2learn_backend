package trinity.play2learn.backend.activity.activity.services.commons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.dtos.activityStudent.ActivityStudentNotApprovedResponseDto;
import trinity.play2learn.backend.activity.activity.mappers.ActivityMapper;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activity.ActivityStatus;
import trinity.play2learn.backend.activity.activity.models.activity.TypeReward;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompleted;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCalculateRewardStrategyService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCreateNotApprovedDtosService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetRemainingAttemptsService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetStatusService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityIsBeingDoneService;
import trinity.play2learn.backend.activity.activity.services.strategyCalculateReward.Poisson;
import trinity.play2learn.backend.admin.student.models.Student;

@Service
@AllArgsConstructor
public class ActivityCreateNotApprovedDtosService implements IActivityCreateNotApprovedDtosService {

    private final IActivityGetRemainingAttemptsService activityGetRemainingAttemptsService;
    private final IActivityGetStatusService activityGetStatusService;
    private final Map<String, IActivityCalculateRewardStrategyService> activityCalculateRewardStrategyServiceMap;
    private final IActivityIsBeingDoneService activityIsBeingDoneService;
    private final Poisson poissonRewardStrategy;

    @Override
    public List<ActivityStudentNotApprovedResponseDto> createNotApprovedDtos(List<Activity> activities, Student student) {
        List<ActivityStudentNotApprovedResponseDto> activitiesDto = new ArrayList<>();

        for (Activity activity : activities) {
            Integer remainingAttempts = activityGetRemainingAttemptsService.getStudentRemainingAttempts(activity,
                    student);
            ActivityStatus activityStatus = activityGetStatusService.getStatus(activity);
            IActivityCalculateRewardStrategyService rewardStrategyService = activityCalculateRewardStrategyServiceMap
                    .get(activity.getTypeReward().name());
            Double minReward = 0.0;
            Double maxReward = Math.floor(rewardStrategyService.execute(activity));

            activitiesDto.add(ActivityMapper.toNotApprovedDto(
                    activity,
                    remainingAttempts,
                    activityStatus,
                    minReward,
                    maxReward,
                    activityIsBeingDoneService.execute(activity, student)));
        }

        return activitiesDto;
    }

    @Override
    public List<ActivityStudentNotApprovedResponseDto> createNotApprovedDtos(
            List<Activity> activities,
            Student student,
            Map<Long, ActivityCompleted> latestCompletionByActivityId,
            Set<Long> activityIdsInProgress) {
        return createNotApprovedDtos(activities, student, latestCompletionByActivityId, activityIdsInProgress,
                Collections.emptyMap(), Collections.emptyMap());
    }

    @Override
    public List<ActivityStudentNotApprovedResponseDto> createNotApprovedDtos(
            List<Activity> activities,
            Student student,
            Map<Long, ActivityCompleted> latestCompletionByActivityId,
            Set<Long> activityIdsInProgress,
            Map<Long, Integer> studentsCountBySubjectId,
            Map<Long, Integer> approvedCountByActivityId) {

        List<ActivityStudentNotApprovedResponseDto> activitiesDto = new ArrayList<>();

        for (Activity activity : activities) {
            ActivityCompleted lastCompleted = latestCompletionByActivityId.get(activity.getId());

            Integer remainingAttempts = lastCompleted != null
                    ? lastCompleted.getRemainingAttempts()
                    : activity.getAttempts();

            ActivityStatus activityStatus = activityGetStatusService.getStatus(activity);
            Double minReward = 0.0;
            Double maxReward = Math.floor(calculateMaxReward(
                    activity, studentsCountBySubjectId, approvedCountByActivityId));

            activitiesDto.add(ActivityMapper.toNotApprovedDto(
                    activity,
                    remainingAttempts,
                    activityStatus,
                    minReward,
                    maxReward,
                    activityIdsInProgress.contains(activity.getId())));
        }

        return activitiesDto;
    }

    private double calculateMaxReward(
            Activity activity,
            Map<Long, Integer> studentsCountBySubjectId,
            Map<Long, Integer> approvedCountByActivityId) {

        int studentsCount = Math.max(1, studentsCountBySubjectId.getOrDefault(activity.getSubject().getId(), 1));

        if (activity.getTypeReward() == TypeReward.EQUITATIVO) {
            return activity.getInitialBalance() / studentsCount;
        }

        int approvedCount = approvedCountByActivityId.getOrDefault(activity.getId(), 0);
        int lambda = Math.max(1, activity.getAttempts() / 2);
        var distribution = poissonRewardStrategy.calculateDistribution(
                studentsCount, activity.getInitialBalance(), lambda);
        int index = Math.min(approvedCount, studentsCount - 1);

        return distribution.get(index);
    }
}
