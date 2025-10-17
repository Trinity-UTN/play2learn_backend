package trinity.play2learn.backend.benefits.dtos.benefitPurchase;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.configs.messages.ValidationMessages;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BenefitPurchaseRequestDto {
    
    @NotNull(message = ValidationMessages.NOT_NULL_BENEFIT_ID)
    private Long benefitId;
}
