package trinity.play2learn.backend.admin.student.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.admin.classes.models.Class;
import trinity.play2learn.backend.user.models.User;

/**
 * Entidad que representa un estudiante en el sistema.
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column (unique = true, length = 50)
    private String name;

    @NotBlank
    @Column (unique = true, length = 50)
    private String lastname;

    @NotBlank
    @Column (unique = true, length = 8)
    private String dni;

    @ManyToOne
    @JoinColumn(name = "class_id", referencedColumnName = "id", nullable = false)
    private Class classes;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(nullable = true)
    private LocalDateTime deleted_at;

    public void delete () {
        this.deleted_at = LocalDateTime.now();
    }

    public void restore () {
        this.deleted_at = null;
    }
}
