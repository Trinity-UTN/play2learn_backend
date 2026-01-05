package trinity.play2learn.backend.configs.fileUpload.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import lombok.RequiredArgsConstructor;
import trinity.play2learn.backend.configs.fileUpload.services.interfaces.IUploadToGoogleDriveService;

import com.google.api.client.http.FileContent;

@Service
@RequiredArgsConstructor
public class UploadToGoogleDriveService implements IUploadToGoogleDriveService {

    private final Drive drive;

    @Value("${google.drive.folder.id}")
    private String folderId;    

    @Override
    public String uploadPdf(java.io.File pdfFile, String fileName) throws Exception {

        try {
            File fileMetadata = new File();
            fileMetadata.setName(fileName);
            fileMetadata.setParents(List.of(folderId));

            FileContent mediaContent = new FileContent("application/pdf", pdfFile);

            File uploadedFile = drive.files()
                    .create(fileMetadata, mediaContent)
                    .setFields("id")
                    .execute();

            // Hacerlo público
            Permission permission = new Permission();
            permission.setType("anyone");
            permission.setRole("reader");

            drive.permissions()
                    .create(uploadedFile.getId(), permission)
                    .execute();

            return uploadedFile.getId();
        } catch (Exception e) {
            throw new RuntimeException("Error al subir el archivo a Google Drive: " + e.getMessage(), e);
        }
    }
}
