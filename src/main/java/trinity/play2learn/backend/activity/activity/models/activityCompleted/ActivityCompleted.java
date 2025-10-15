package trinity.play2learn.backend.activity.activity.models.activityCompleted;

import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.admin.student.models.Student;

@Entity
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
public class ActivityCompleted {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double reward;
    private Integer remainingAttempts;
    
    private ActivityCompletedState state;

    @ManyToOne
    @NotNull
    private Activity activity;

    @ManyToOne
    @NotNull
    private Student student;

    private LocalDateTime completedAt;

    private LocalDateTime startedAt;

    //En caso de ser un intento de NoLudica, se guarda la respuesta del estudiante para que el docente la corrija
    @JoinColumn(name = "no_ludica_attempt_id", nullable = true)
    @OneToOne(cascade = CascadeType.ALL)
    private NoLudicaAttempt noLudicaAttempt;

    @PrePersist //Antes de persistir la actividad se guarda su fecha de creacion
    private void setStartedAt(){
        this.startedAt = LocalDateTime.now();
    }
}
