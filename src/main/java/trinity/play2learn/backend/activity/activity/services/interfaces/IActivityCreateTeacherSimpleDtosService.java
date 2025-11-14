package trinity.play2learn.backend.activity.activity.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.activity.activity.dtos.activityTeacher.ActivityTeacherSimpleDto;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;

public interface IActivityCreateTeacherSimpleDtosService {
    
    List<ActivityTeacherSimpleDto> createTeacherSimpleDtos(List<Activity> activities);
}
