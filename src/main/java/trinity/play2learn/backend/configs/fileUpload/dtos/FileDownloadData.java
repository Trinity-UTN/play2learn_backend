package trinity.play2learn.backend.configs.fileUpload.dtos;

import org.springframework.core.io.Resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileDownloadData {
    
    private String fileName;
    private String contentType;
    private Resource resource;
}
