package trinity.play2learn.backend.activity.activity.mappers;

import trinity.play2learn.backend.activity.activity.models.activityCompleted.NoLudicaAttempt;
import trinity.play2learn.backend.configs.uploadCare.models.UploadedFile;

public class NoLudicaAttemptMapper {
    
    public static NoLudicaAttempt toModel(String plainText, UploadedFile file) {
        return NoLudicaAttempt.builder()
            .plainText(plainText)
            .file(file)
            .build();
    }
}
