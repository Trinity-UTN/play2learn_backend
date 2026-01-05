package trinity.play2learn.backend.configs.fileUpload.services.interfaces;

import java.io.File;

public interface IUploadToGoogleDriveService {

    String uploadPdf(File pdfFile, String fileName) throws Exception;
}
