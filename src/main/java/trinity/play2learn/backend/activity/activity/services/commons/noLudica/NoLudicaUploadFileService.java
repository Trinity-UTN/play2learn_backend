package trinity.play2learn.backend.activity.activity.services.commons.noLudica;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.services.interfaces.INoLudicaUploadFileService;
import trinity.play2learn.backend.configs.fileUpload.mappers.StoredFileMapper;
import trinity.play2learn.backend.configs.fileUpload.models.StoredFile;
import trinity.play2learn.backend.configs.fileUpload.services.interfaces.IUploadToCloudinaryService;

@Service
@AllArgsConstructor
public class NoLudicaUploadFileService implements INoLudicaUploadFileService {

    private final IUploadToCloudinaryService uploadToCloudinaryService;

    //Si el archivo no existe devuelve null
    //Si existe lo sube a Cloudinary, persiste los metadatos en StoredFile y lo devuelve.
    @Override
    public StoredFile uploadFileIfExist(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            // Subir archivo a Cloudinary
            Map<String, Object> uploadResult = uploadToCloudinaryService.uploadFileToCloudinary(file);

            String publicId  = (String) uploadResult.get("public_id");
            String secureUrl = (String) uploadResult.get("secure_url");

            // Crear entidad StoredFile con metadatos
            return StoredFileMapper.toModel(
                    file.getOriginalFilename(),
                    publicId,
                    secureUrl,
                    file.getContentType(),
                    file.getSize()
            );

        } catch (Exception e) {
            throw new RuntimeException("Error al subir el archivo a Cloudinary", e);
        }
    }

}
