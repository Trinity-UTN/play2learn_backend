package trinity.play2learn.backend.activity.activity.services.commons;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.mappers.NoLudicaAttemptMapper;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.NoLudicaAttempt;
import trinity.play2learn.backend.activity.activity.services.interfaces.INoLudicaCreateAttemptService;
import trinity.play2learn.backend.configs.uploadCare.mappers.UploadFileMapper;
import trinity.play2learn.backend.configs.uploadCare.models.UploadedFile;
import trinity.play2learn.backend.configs.uploadCare.services.UploadcareService;

@Service
@AllArgsConstructor
public class NoLudicaCreateAttemptService implements INoLudicaCreateAttemptService {
    
    private final UploadcareService uploadcareService;

    @Override
    public NoLudicaAttempt createAttempt(String plainText, MultipartFile file) {

        UploadedFile uploadedFile = null;

        if (!(file == null || file.isEmpty())) {
            Map<String, String> uploadResult = uploadcareService.uploadToUploadcare(file);
            
            uploadedFile = UploadFileMapper.toModel(uploadResult.get("fileName"), uploadResult.get("uuid"), uploadResult.get("cdnUrl"));
            
        }

        return NoLudicaAttemptMapper.toModel(plainText, uploadedFile);

    }
    
    
}
