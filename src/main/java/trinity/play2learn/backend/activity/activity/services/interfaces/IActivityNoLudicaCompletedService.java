package trinity.play2learn.backend.activity.activity.services.interfaces;

import org.springframework.web.multipart.MultipartFile;

import trinity.play2learn.backend.activity.activity.dtos.activityCompleted.ActivityCompletedResponseDto;
import trinity.play2learn.backend.user.models.User;

public interface IActivityNoLudicaCompletedService {
    
    ActivityCompletedResponseDto cu72ActivityNoLudicaCompleted(Long activityId, String plainText, MultipartFile fileName, User user);
}
