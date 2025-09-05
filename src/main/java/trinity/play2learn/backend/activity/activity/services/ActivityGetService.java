package trinity.play2learn.backend.activity.activity.services;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.dtos.activityCreated.ActivityResponseDto;
import trinity.play2learn.backend.activity.activity.mappers.IActivityMapper;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetByIdService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetService;

@Service
@AllArgsConstructor
public class ActivityGetService implements IActivityGetService{
    
    private final Map<String, IActivityMapper> mappers;
    private final IActivityGetByIdService activityGetByIdService;

    @Override
    @Transactional(readOnly = true)
    public ActivityResponseDto cu64GetActivity(Long id) {

        Activity activity = activityGetByIdService.findActivityById(id);

        String activityType = activity.getClass().getSimpleName();
        String beanName = activityType.substring(0, 1).toLowerCase() + activityType.substring(1) + "Mapper";

        IActivityMapper mapper = mappers.get(beanName);
        if (mapper == null) {
            throw new IllegalArgumentException("No mapper found for activity type: " + activityType);
        }

        return mapper.toActivityDto(activity);
    }
    
}
