package trinity.play2learn.backend.activity.activity.services.interfaces;

import org.springframework.web.multipart.MultipartFile;

public interface INoLudicaValidationsService {
    
    void validateNoLudicaCompleted(String plaintext , MultipartFile file);
}
