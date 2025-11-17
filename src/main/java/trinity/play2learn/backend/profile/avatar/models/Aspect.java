package trinity.play2learn.backend.profile.avatar.models;

import java.math.BigDecimal;
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

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "aspects")
public class Aspect {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column (unique = true, length = 50)
    private String name;

    @NotBlank
    private String image;

    @Builder.Default
    private boolean available = true;

    private BigDecimal price;

    @Column(nullable = true)
    private LocalDateTime deletedAt;

    private TypeAspect type;

    public void delete () {
        this.deletedAt = LocalDateTime.now();
    }

    public void restore () {
        this.deletedAt = null;
    }
}
