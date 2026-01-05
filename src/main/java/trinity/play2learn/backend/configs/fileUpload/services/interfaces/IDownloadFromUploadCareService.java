package trinity.play2learn.backend.configs.fileUpload.services.interfaces;

import trinity.play2learn.backend.configs.fileUpload.dtos.FileDownloadData;

public interface IDownloadFromUploadCareService {
    
    FileDownloadData downloadFileFromUploadCare(String fileName, String cdnUrl);
}
