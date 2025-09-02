package trinity.play2learn.backend.profile.profile.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.profile.avatar.models.Aspect;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "profile")
@ToString(exclude = "student")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "student_id", referencedColumnName = "id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "selected_body_id")
    private Aspect selectedBody;

    @ManyToOne
    @JoinColumn(name = "selected_shirt_id")
    private Aspect selectedShirt;

    @ManyToOne
    @JoinColumn(name = "selected_hat_id")
    private Aspect selectedHat;

    @ManyToMany
    @JoinTable(
        name = "profile_aspects",
        joinColumns = @JoinColumn(name = "profile_id"),
        inverseJoinColumns = @JoinColumn(name = "aspect_id")
    )
    @Builder.Default
    private List<Aspect> ownedAspects = new ArrayList<>();

    @Override
    public int hashCode() {
        return Objects.hash(id); // suficiente en entidades JPA
    }

}
