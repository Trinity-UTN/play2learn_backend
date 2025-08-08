package trinity.play2learn.backend.benefits.models;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.admin.subject.models.Subject;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "benefits")
public class Benefit {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    @Size(max = 1000)
    private String description;

    @NotNull
    @Min(value = 1)
    private Long cost;

    //Cantidad total de veces que quedan por canjear el beneficio
    //Si es nulo es porque el beneficio es ilimitado
    private Integer totalRedeemableAmount;

    //Cantidad de veces que se puede canjear el beneficio por estudiante
    //Si es nulo es porque el beneficio es ilimitado
    private Integer redeemableAmountPerStudent;

    @ManyToOne
    @NotNull
    private Subject subject;

    private LocalDateTime deletedAt;

    public void delete(){
        this.deletedAt = LocalDateTime.now();
    }

    public void restore(){
        this.deletedAt = null;
    }
}
