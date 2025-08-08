package trinity.play2learn.backend.activity.activity.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.configs.messages.ValidationMessages;

@Data
@MappedSuperclass
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder //Esta notacion es necesaria para que el builder herede de la clase padre
public abstract class Activity {
    //El nombre de la actividad sera el tipo de actividad (Ahorcado, preguntados, etc)
    @Size(max = 1000, message = ValidationMessages.MAX_LENGTH_DESCRIPTION_1000)
    @Column(nullable = true)
    private String description;

    @NotNull
    private LocalDateTime startDate; //Fecha de inicio de exposicion de la actividad
    @NotNull
    private LocalDateTime createdAt;

    @NotNull 
    private LocalDateTime endDate; //Fecha de fin de exposicion de la actividad

    @Column(nullable = true)
    private LocalDateTime deletedAt;

    private Dificulty dificulty;
    private int maxTime; //Tiempo maximo de realizacion de la actividad

    @Column(nullable = true)
    private int attempts;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    public void delete(){
        this.deletedAt = LocalDateTime.now();
    }

    public void restore(){
        this.deletedAt = null;
    }
    
    @PrePersist //Antes de persistir la actividad se guarda su fecha de creacion
    private void setCreatedAt(){
        this.createdAt = LocalDateTime.now();
    }
}
