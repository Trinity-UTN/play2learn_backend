package trinity.play2learn.backend.configs.uploadCare.mappers;

import trinity.play2learn.backend.configs.uploadCare.models.UploadedFile;

public class UploadFileMapper {
    
    public static UploadedFile toModel(String fileName, String uuid, String cdnUrl) {
        return UploadedFile.builder()
            .fileName(fileName)
            .uuid(uuid)
            .cdnUrl(cdnUrl)
            .build();
    }
}
