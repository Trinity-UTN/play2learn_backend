package trinity.play2learn.backend.activity.activity.services.commons;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.dtos.activityTeacher.ActivityTeacherSimpleDto;
import trinity.play2learn.backend.activity.activity.mappers.ActivityMapper;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activity.ActivityStatus;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCreateTeacherSimpleDtosService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetStatusService;

@Service
@AllArgsConstructor
public class ActivityCreateTeacherSimpleDtosService implements IActivityCreateTeacherSimpleDtosService{
    
    private final IActivityGetStatusService activityGetStatusService;
    
    @Override
    public List<ActivityTeacherSimpleDto> createTeacherSimpleDtos(List<Activity> activities) {

        List<ActivityTeacherSimpleDto> activityTeacherSimpleDtos = new ArrayList<>();

        for (Activity activity : activities) {
            ActivityStatus status = activityGetStatusService.getStatus(activity);

            //En caso de no estar publicada, se muestra la fecha de publicacion.
            //En caso de estar publicada o expirada, se muestra la fecha de expiracion.
            LocalDateTime date = status == ActivityStatus.CREATED ? activity.getStartDate() : activity.getEndDate();

            activityTeacherSimpleDtos.add(ActivityMapper.toSimpleDto(activity, status, date));
        }

        return activityTeacherSimpleDtos;
    }
    
    

}
