package trinity.play2learn.backend.benefits.services.interfaces;

import trinity.play2learn.backend.benefits.dtos.BenefitRequestDto;
import trinity.play2learn.backend.benefits.dtos.BenefitResponseDto;
import trinity.play2learn.backend.user.models.User;

public interface IBenefitGenerateService {
    
    BenefitResponseDto cu51GenerateBenefit(BenefitRequestDto benefitRequestDto, User user);
}
