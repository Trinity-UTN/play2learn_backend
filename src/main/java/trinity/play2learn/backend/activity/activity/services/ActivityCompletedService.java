package trinity.play2learn.backend.activity.activity.services;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.dtos.activityCompleted.ActivityCompletedRequestDto;
import trinity.play2learn.backend.activity.activity.dtos.activityCompleted.ActivityCompletedResponseDto;
import trinity.play2learn.backend.activity.activity.models.Activity;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCompletedService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCompletedStrategyService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetByIdService;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.user.models.User;

@Service
@AllArgsConstructor
public class ActivityCompletedService implements IActivityCompletedService {
    
    private final IActivityGetByIdService activityFindByIdService;
    private final Map<String, IActivityCompletedStrategyService> activityCompletedStrategyServiceMap;
    private final IStudentGetByEmailService studentGetByEmailService;

    @Override
    @Transactional
    public ActivityCompletedResponseDto cu61ActivityCompleted(ActivityCompletedRequestDto activityCompletedRequestDto, User user) {

        Activity activity = activityFindByIdService.findActivityById(activityCompletedRequestDto.getActivityId());
        
        Student student = studentGetByEmailService.getByEmail(user.getEmail());

        IActivityCompletedStrategyService strategyService = activityCompletedStrategyServiceMap.get(activityCompletedRequestDto.getState().name());

        return strategyService.execute(activity, student);
    }
    
    
}
