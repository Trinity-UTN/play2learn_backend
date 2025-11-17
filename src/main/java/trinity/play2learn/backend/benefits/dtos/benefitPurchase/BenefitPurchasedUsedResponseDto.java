package trinity.play2learn.backend.benefits.dtos.benefitPurchase;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.benefits.models.BenefitCategory;
import trinity.play2learn.backend.benefits.models.BenefitColor;
import trinity.play2learn.backend.benefits.models.BenefitIcon;
import trinity.play2learn.backend.benefits.models.BenefitPurchaseState;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BenefitPurchasedUsedResponseDto {
    
    private Long id;
    
    private BenefitPurchaseState state;

    private Long benefitId;
    private String benefitName;
    private String benefitDescription;
    private BenefitCategory category;
    private BenefitColor color;
    private BenefitIcon icon;

    private Long subjectId;
    private String subjectName;

    private LocalDateTime usedAt;
}
