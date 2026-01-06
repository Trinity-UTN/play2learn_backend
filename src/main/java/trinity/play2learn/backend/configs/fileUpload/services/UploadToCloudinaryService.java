package trinity.play2learn.backend.configs.fileUpload.services;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import lombok.RequiredArgsConstructor;
import trinity.play2learn.backend.configs.fileUpload.services.interfaces.IUploadToCloudinaryService;

@Service
@RequiredArgsConstructor
public class UploadToCloudinaryService implements IUploadToCloudinaryService{
    
    private final Cloudinary cloudinary;

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> uploadFileToCloudinary(MultipartFile file) {
        try {
            return cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "resource_type", "raw",    
                            "use_filename", true,
                            "unique_filename", true,
                            "folder", "pdfs_no_ludica"
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException("Error al subir archivo a Cloudinary: " + e.getMessage(), e);
        }
    }
}
