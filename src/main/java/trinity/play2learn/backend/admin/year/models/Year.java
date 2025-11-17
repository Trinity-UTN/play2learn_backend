package trinity.play2learn.backend.admin.year.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa un año académico en el sistema.
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "years")
public class Year {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column (unique = true, length = 50)
    private String name;

    @Column(nullable = true)
    private LocalDateTime deletedAt;

    public void delete () {
        this.deletedAt = LocalDateTime.now();
    }

    public void restore () {
        this.deletedAt = null;
    }
}
