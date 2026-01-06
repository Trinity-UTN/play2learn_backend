package trinity.play2learn.backend.configs.fileUpload.services.interfaces;

import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

public interface IUploadToCloudinaryService {

    Map<String, Object> uploadFileToCloudinary(MultipartFile file);
}
