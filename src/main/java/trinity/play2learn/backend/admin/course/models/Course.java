package trinity.play2learn.backend.admin.course.models;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.admin.year.models.Year;


/*
 * Representa un curso en el sistema.
 * Cada curso tiene un nombre y año asociado, los cuales su combinacion debe ser única.
*/
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "courses",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "year_id"})
    }
)
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column (length = 50)
    private String name;

    @ManyToOne (optional = false)
    private Year year;

    @Column(nullable = true)
    private LocalDateTime deleted_at;

    public void delete () {
        this.deleted_at = LocalDateTime.now();
    }

    public void restore () {
        this.deleted_at = null;
    }
}
