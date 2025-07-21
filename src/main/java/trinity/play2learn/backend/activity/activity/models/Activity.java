package trinity.play2learn.backend.activity.activity.models;

import java.time.LocalDateTime;

import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.admin.subject.models.Subject;

@Data
@MappedSuperclass
@AllArgsConstructor
@NoArgsConstructor
public abstract class Activity {
    private String name;
    private String description;

    private LocalDateTime startDate; //Fecha de inicio de exposicion de la actividad    
    private LocalDateTime createdAt; 
    private LocalDateTime endDate; //Fecha de fin de exposicion de la actividad
    private LocalDateTime deletedAt;

    private Dificulty difficulty;
    private int maxTime; //Tiempo maximo de realizacion de la actividad
    private Attempts attempts;

    private Subject subject;

    public void delete(){
        this.deletedAt = LocalDateTime.now();
    }

    public void restore(){
        this.deletedAt = null;
    }
    
}
