package trinity.play2learn.backend.admin.student.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.CascadeType;
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
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import trinity.play2learn.backend.admin.course.models.Course;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.profile.profile.models.Profile;
import trinity.play2learn.backend.user.models.User;

/**
 * Entidad que representa un estudiante en el sistema.
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column (length = 50)
    private String name;

    @NotBlank
    @Column (length = 50)
    private String lastname;

    @NotBlank
    @Column (unique = true, length = 8)
    private String dni;

    @ManyToOne
    @JoinColumn(name = "course_id", referencedColumnName = "id", nullable = false)
    private Course course;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(nullable = true)
    private LocalDateTime deletedAt;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_id", referencedColumnName = "id")
    @ToString.Exclude
    private Profile profile;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "wallet_id", referencedColumnName = "id")
    @ToString.Exclude
    private Wallet wallet;

    @Column(nullable = true)
    private LocalDate birthdate;

    @Column(nullable = true)
    private String emailTutor;

    public void delete () {
        this.deletedAt = LocalDateTime.now();
        user.delete();
    }

    public void restore () {
        this.deletedAt = null;
        user.restore();
    }
    @Override
    public int hashCode() {
        return Objects.hash(id); // suficiente en entidades JPA
    }

}
