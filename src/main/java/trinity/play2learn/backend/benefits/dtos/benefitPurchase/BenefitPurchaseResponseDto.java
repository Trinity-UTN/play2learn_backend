package trinity.play2learn.backend.benefits.dtos.benefitPurchase;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.admin.student.dtos.StudentSimplificatedResponse;
import trinity.play2learn.backend.benefits.dtos.benefit.BenefitResponseDto;
import trinity.play2learn.backend.benefits.models.BenefitPurchaseState;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BenefitPurchaseResponseDto {
    
    private Long id;
    private StudentSimplificatedResponse student;
    private BenefitPurchaseState state;
    private LocalDateTime purchasedAt;
    private BenefitResponseDto benefitDto;
    //Si es null significa que el limite es ilimitado
    private Integer purchasesLeft;
    //Si es null significa que el limite es ilimitado
    private Integer purchasesLeftByStudent;
}
