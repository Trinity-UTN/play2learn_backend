package trinity.play2learn.backend.configs.fileUpload.mappers;

import org.springframework.core.io.Resource;

import trinity.play2learn.backend.configs.fileUpload.dtos.FileDownloadData;
import trinity.play2learn.backend.configs.fileUpload.models.StoredFile;

public class StoredFileMapper {

    public static StoredFile toModel(String fileName, String providerFileId, String fileUrl, String mimeType, Long fileSize) {
        return StoredFile.builder()
                .fileName(fileName)
                .providerFileId(providerFileId)
                .fileUrl(fileUrl)
                .provider("CLOUDINARY")
                .mimeType(mimeType)
                .fileSize(fileSize)
                .build();
    }

    public static FileDownloadData toFileDownloadData(String fileName, String contentType, Resource resource) {
        return FileDownloadData.builder()
                .fileName(fileName)
                .contentType(contentType)
                .resource(resource)
                .build();
    }
}
