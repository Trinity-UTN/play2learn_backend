package trinity.play2learn.backend.configs.fileUpload.controllers;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.services.interfaces.INoLudicaUploadFileService;
import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.user.models.Role;

@RestController
@AllArgsConstructor
@RequestMapping("/test/googleDrive")
public class UploadToDriveManualTestController {

    private final INoLudicaUploadFileService noLudicaUploadFileService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SessionRequired(roles = { Role.ROLE_DEV })
    public ResponseEntity<Void> uploadFile(@RequestParam MultipartFile file) {

        noLudicaUploadFileService.uploadFileIfExist(file);

        return ResponseEntity.ok().build();
    }

}
