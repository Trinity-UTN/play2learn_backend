package trinity.play2learn.backend.configs.fileUpload.services.interfaces;

import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

public interface IUploadToUploadcareService {
    
    Map<String, String> uploadToUploadcare(MultipartFile file);
}
