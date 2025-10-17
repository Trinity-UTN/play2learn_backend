package trinity.play2learn.backend.benefits.models;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
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

    //Cantidad total de veces que se puede comprar el beneficio
    //Si es nulo es porque el beneficio es ilimitado
    private Integer purchaseLimit;

    private Integer purchasesLeft;

    //Cantidad de veces que se puede canjear el beneficio por estudiante
    //Si es nulo es porque el beneficio es ilimitado
    private Integer purchaseLimitPerStudent;

    @NotNull
    private LocalDateTime endAt;

    @ManyToOne
    @NotNull
    private Subject subject;

    @Enumerated(EnumType.STRING)
    @NotNull
    private BenefitIcon icon;

    @Enumerated(EnumType.STRING)
    @NotNull
    private BenefitCategory category;

    @Enumerated(EnumType.STRING)
    @NotNull
    private BenefitColor color;

    private LocalDateTime deletedAt;

    public void delete(){
        this.deletedAt = LocalDateTime.now();
    }

    public void restore(){
        this.deletedAt = null;
    }

    public boolean isExpired(){
        return this.endAt.isBefore(LocalDateTime.now());
    }

    @PrePersist
    public void initializePurchasesLeft() {
        this.purchasesLeft = this.purchaseLimit;
    }

    public void decrementPurchasesLeft(){
        if (purchaseLimit == null || purchaseLimit == 0) {
            return;
        }
        
        this.purchasesLeft -= 1;
    }
}
