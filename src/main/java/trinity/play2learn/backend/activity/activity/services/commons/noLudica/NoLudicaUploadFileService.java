package trinity.play2learn.backend.activity.activity.services.commons.noLudica;

import java.io.File;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.services.interfaces.INoLudicaUploadFileService;
import trinity.play2learn.backend.configs.fileUpload.mappers.StoredFileMapper;
import trinity.play2learn.backend.configs.fileUpload.models.StoredFile;
import trinity.play2learn.backend.configs.fileUpload.services.interfaces.IUploadToGoogleDriveService;

@Service
@AllArgsConstructor
public class NoLudicaUploadFileService implements INoLudicaUploadFileService {

    private final IUploadToGoogleDriveService uploadFromGoogleDriveService;

    // Si el archivo existe, lo sube a Google Drive
    // Si el archivo no existe, devuelve null
    @Override
    public StoredFile uploadFileIfExist(MultipartFile file) {

        StoredFile storedFile = null;

        if (!(file == null || file.isEmpty())) {

            try {

                // Crea un archivo temporal en el sistema
                File tempFile = File.createTempFile("attempt-", ".pdf");

                // Copia el contenido del MultipartFile al archivo temporal
                file.transferTo(tempFile);

                // Sube el archivo a Google Drive y obtiene su ID
                String driveFileId = uploadFromGoogleDriveService.uploadPdf(
                        tempFile,
                        file.getOriginalFilename());

                // Crea el objeto de dominio con los metadatos del archivo
                storedFile = StoredFileMapper.toModel(
                        file.getOriginalFilename(), // nombre original
                        driveFileId, // ID en Google Drive
                        file.getContentType(), // tipo MIME
                        file.getSize() // tamaño en bytes
                );

                // Elimina el archivo temporal
                tempFile.delete();

            } catch (Exception e) {
                throw new RuntimeException("Error al subir el archivo a Google Drive: " + e.getMessage(), e);
            }

        }

        return storedFile;

    }

}
