package trinity.play2learn.backend.benefits.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.benefits.dtos.BenefitResponseDto;
import trinity.play2learn.backend.user.models.User;

public interface IBenefitListService {
    
    List<BenefitResponseDto> cu55ListBenefits(User user);
}
