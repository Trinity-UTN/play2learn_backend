package trinity.play2learn.backend.activity.activity.services.interfaces;

import org.springframework.web.multipart.MultipartFile;

import trinity.play2learn.backend.activity.activity.models.activityCompleted.NoLudicaAttempt;

public interface INoLudicaCreateAttemptService {
    
    NoLudicaAttempt createAttempt(String plainText, MultipartFile file);
}
