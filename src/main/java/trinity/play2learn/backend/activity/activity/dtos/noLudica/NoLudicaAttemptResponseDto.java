package trinity.play2learn.backend.activity.activity.dtos.noLudica;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoLudicaAttemptResponseDto {
    
    private String plainText;
    private Boolean hasFile;
    private String fileName;
    private String downloadUrl;
}
