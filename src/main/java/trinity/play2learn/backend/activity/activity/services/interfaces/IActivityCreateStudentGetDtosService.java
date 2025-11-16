package trinity.play2learn.backend.activity.activity.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.activity.activity.dtos.activityStudent.ActivityStudentGetResponseDto;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;

public interface IActivityCreateStudentGetDtosService {
    
    List<ActivityStudentGetResponseDto> createStudentGetDtos(Activity activity);
}
