package trinity.play2learn.backend.configs.fileUpload.models;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class StoredFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String fileName;

    @NotNull
    private String providerFileId; // Google Drive fileId

    @NotNull
    private String provider; // "GOOGLE_DRIVE"

    private String mimeType;

    private Long fileSize;

    private LocalDateTime uploadedAt;

    @PrePersist
    public void setUploadedAt() {
        this.uploadedAt = LocalDateTime.now();
    }

}
