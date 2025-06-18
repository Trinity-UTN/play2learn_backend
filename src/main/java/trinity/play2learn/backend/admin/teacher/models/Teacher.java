package trinity.play2learn.backend.admin.teacher.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.user.models.User;

@Entity
@Builder
@NoArgsConstructor
@Data
@AllArgsConstructor
public class Teacher {
    
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
