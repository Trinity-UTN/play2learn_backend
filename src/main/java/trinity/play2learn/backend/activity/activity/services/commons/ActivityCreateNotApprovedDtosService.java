package trinity.play2learn.backend.activity.activity.services.commons;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.dtos.activityStudent.ActivityStudentNotApprovedResponseDto;
import trinity.play2learn.backend.activity.activity.mappers.ActivityMapper;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activity.ActivityStatus;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCalculateRewardStrategyService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCreateNotApprovedDtosService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetCompletedStateService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetRemainingAttemptsService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetStatusService;
import trinity.play2learn.backend.admin.student.models.Student;

@Service
@AllArgsConstructor
public class ActivityCreateNotApprovedDtosService implements IActivityCreateNotApprovedDtosService {

    private final IActivityGetCompletedStateService activityGetCompletedStateService;

    private final IActivityGetRemainingAttemptsService activityGetRemainingAttemptsService;

    private final IActivityGetStatusService activityGetStatusService;

    private final Map<String, IActivityCalculateRewardStrategyService> activityCalculateRewardStrategyServiceMap;


    @Override
    public List<ActivityStudentNotApprovedResponseDto> createNotApprovedDtos(List<Activity> activities, Student student) {

        List<ActivityStudentNotApprovedResponseDto> activitiesDto = new ArrayList<>();

        for (Activity activity : activities) {

            ActivityCompletedState activityCompletedState = activityGetCompletedStateService.getActivityCompletedState(activity, student);
            
            if (activityCompletedState == ActivityCompletedState.APPROVED) {
                
                continue; //Salta a la siguiente iteracion
            }
            
            Integer remainingAttempts = activityGetRemainingAttemptsService.getStudentRemainingAttempts(activity, student);

            ActivityStatus activityStatus = activityGetStatusService.getStatus(activity);
            System.out.println(activity.getTypeReward().name());
            IActivityCalculateRewardStrategyService rewardStrategyService = activityCalculateRewardStrategyServiceMap.get(activity.getTypeReward().name());
            
            Double minReward = 0.0; 
            
            Double maxReward = Math.round(rewardStrategyService.execute(activity) * 100.0) / 100.0;
            
            Boolean pending = false;

            if (activityCompletedState == ActivityCompletedState.PENDING) {
                pending = true;
            }
            
            activitiesDto.add(ActivityMapper.toNotApprovedDto(activity, remainingAttempts, pending, activityStatus, minReward, maxReward));

        }

        return activitiesDto;
    }
    
}
