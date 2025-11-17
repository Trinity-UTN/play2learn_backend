package trinity.play2learn.backend.benefits.services.interfaces;

import trinity.play2learn.backend.benefits.dtos.benefitPurchase.BenefitPurchaseRequestDto;
import trinity.play2learn.backend.benefits.dtos.benefitPurchase.BenefitPurchaseResponseDto;
import trinity.play2learn.backend.user.models.User;

public interface IBenefitPurchaseService {
    
    BenefitPurchaseResponseDto cu75PurchaseBenefit(BenefitPurchaseRequestDto benefitRequestDto, User user);
}
