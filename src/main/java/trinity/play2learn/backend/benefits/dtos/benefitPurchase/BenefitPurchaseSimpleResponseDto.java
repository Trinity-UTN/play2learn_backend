package trinity.play2learn.backend.benefits.dtos.benefitPurchase;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.benefits.models.BenefitPurchaseState;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BenefitPurchaseSimpleResponseDto {
    
    private Long id;
    private BenefitPurchaseState state;
    private Long benefitId;
    private String benefitName;
    private Long subjectId;
    private String subjectName;
    private Long studentId;
    private String studentName;
}
