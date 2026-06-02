package trinity.play2learn.backend.activity.activity.services.interfaces;

import org.springframework.web.multipart.MultipartFile;

import trinity.play2learn.backend.configs.fileUpload.models.StoredFile;

public interface INoLudicaUploadFileService {

    StoredFile uploadFileIfExist(MultipartFile file);
}
