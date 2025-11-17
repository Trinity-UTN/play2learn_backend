package trinity.play2learn.backend.activity.activity.services.commons;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.dtos.activityStudent.ActivityStudentApprovedResponseDto;
import trinity.play2learn.backend.activity.activity.mappers.ActivityMapper;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompleted;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCreateApprovedDtosService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetLastCompletedService;
import trinity.play2learn.backend.admin.student.models.Student;

@Service
@AllArgsConstructor
public class ActivityCreateApprovedDtosService implements IActivityCreateApprovedDtosService {
    
    private final IActivityGetLastCompletedService activityGetLastCompletedService;

    @Override
    public List<ActivityStudentApprovedResponseDto> createApprovedDtos(List<Activity> activities, Student student) {
        
        List<ActivityStudentApprovedResponseDto> activitiesDto = new ArrayList<>();

        for (Activity activity : activities) {

            ActivityCompleted lastCompleted = activityGetLastCompletedService.getLastCompleted(activity, student);
            
            if (lastCompleted == null) {

                continue;
            }

            if (lastCompleted.getState() != ActivityCompletedState.APPROVED) {
                
                continue; //Salta a la siguiente iteracion
            }
            
            activitiesDto.add(ActivityMapper.toApprovedDto(activity, lastCompleted.getRemainingAttempts(), lastCompleted.getReward(), lastCompleted.getCompletedAt()));

        }

        return activitiesDto;

    }
    
    
}
