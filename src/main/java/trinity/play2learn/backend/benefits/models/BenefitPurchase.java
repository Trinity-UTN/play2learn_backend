package trinity.play2learn.backend.benefits.models;


import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.admin.student.models.Student;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "benefit_purchases")
public class BenefitPurchase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull
    private Student student;

    @ManyToOne(cascade = CascadeType.ALL)
    @NotNull
    private Benefit benefit;

    @Enumerated(EnumType.STRING)
    @NotNull
    private BenefitPurchaseState state;

    @NotNull
    private LocalDateTime purchasedAt;

    @PrePersist
    public void setPurchasedAt() {
        this.purchasedAt = LocalDateTime.now();
    }

    public boolean isUsed() {
        return this.state == BenefitPurchaseState.USED;
    }

    public boolean isUseRequested() {
        return this.state == BenefitPurchaseState.USE_REQUESTED;
    }

    public boolean isPurchased() {
        return this.state == BenefitPurchaseState.PURCHASED;
    }

}
