package trinity.play2learn.backend.benefits.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.benefits.dtos.benefitPurchase.BenefitPurchasedUsedResponseDto;
import trinity.play2learn.backend.user.models.User;

public interface IBenefitListUsedByStudentService {
    
    List<BenefitPurchasedUsedResponseDto> cu93ListUsedByStudent(User user);
}
