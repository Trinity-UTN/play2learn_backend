package trinity.play2learn.backend.configs.uploadCare.controllers;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.configs.annotations.SessionRequired;
import trinity.play2learn.backend.configs.uploadCare.services.UploadcareService;
import trinity.play2learn.backend.user.models.Role;

@RestController
@AllArgsConstructor
@RequestMapping("/test/uploadCare")
public class UploadCareManualTestController {
    
    private final UploadcareService uploadcareService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SessionRequired(roles = {Role.ROLE_DEV})
    public ResponseEntity<Void> uploadFile( @RequestParam MultipartFile file){ 

        uploadcareService.uploadToUploadcare(file);

        return ResponseEntity.ok().build();
    }

}
