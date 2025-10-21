package trinity.play2learn.backend.benefits.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.benefits.dtos.benefitPurchase.BenefitPurchaseSimpleResponseDto;
import trinity.play2learn.backend.user.models.User;

public interface IBenefitListUseRequestedService {
    
    List<BenefitPurchaseSimpleResponseDto> cu82ListUseRequestedByTeacher(User user);
}
