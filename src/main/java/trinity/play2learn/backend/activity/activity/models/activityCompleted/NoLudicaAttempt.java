package trinity.play2learn.backend.activity.activity.models.activityCompleted;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.configs.fileUpload.models.StoredFile;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "no_ludica_attempt")
public class NoLudicaAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT",nullable = true)
    private String plainText;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "stored_file_id")
    private StoredFile file;

}
