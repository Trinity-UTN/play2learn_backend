package trinity.play2learn.backend.benefits.services.interfaces;

import trinity.play2learn.backend.benefits.dtos.benefitPurchase.BenefitUseRequestedResponseDto;
import trinity.play2learn.backend.user.models.User;

public interface IBenefitRequestUseService {
    
    BenefitUseRequestedResponseDto cu81RequestBenefitUse(User user, Long benefitId);
}
