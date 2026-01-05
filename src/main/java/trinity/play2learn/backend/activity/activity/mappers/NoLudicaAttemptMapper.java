package trinity.play2learn.backend.activity.activity.mappers;

import trinity.play2learn.backend.activity.activity.dtos.noLudica.NoLudicaAttemptResponseDto;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.NoLudicaAttempt;
import trinity.play2learn.backend.configs.fileUpload.models.StoredFile;

public class NoLudicaAttemptMapper {

    public static NoLudicaAttempt toModel(String plainText, StoredFile file) {
        return NoLudicaAttempt.builder()
                .plainText(plainText)
                .file(file)
                .build();
    }

    public static NoLudicaAttemptResponseDto toDto(String plainText, Boolean hasFile, String fileName,
            String downloadUrl) {
        return NoLudicaAttemptResponseDto.builder()
                .plainText(plainText)
                .hasFile(hasFile)
                .fileName(fileName)
                .downloadUrl(downloadUrl)
                .build();
    }
}
