package trinity.play2learn.backend.benefits.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.benefits.dtos.benefitPurchase.BenefitUseRequestedResponseDto;
import trinity.play2learn.backend.user.models.User;

public interface IBenefitListUseRequestedService {
    
    List<BenefitUseRequestedResponseDto> cu82ListUseRequestedByTeacher(User user);
}
