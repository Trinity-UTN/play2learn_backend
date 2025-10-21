package trinity.play2learn.backend.benefits.services.interfaces;

import trinity.play2learn.backend.benefits.dtos.benefitPurchase.BenefitPurchaseSimpleResponseDto;
import trinity.play2learn.backend.user.models.User;

public interface IBenefitRequestUseService {
    
    BenefitPurchaseSimpleResponseDto cu81RequestBenefitUse(User user, Long benefitId);
}
