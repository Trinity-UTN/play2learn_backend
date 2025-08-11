package trinity.play2learn.backend.benefits.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.benefits.dtos.BenefitResponseDto;
import trinity.play2learn.backend.user.models.User;

public interface IBenefitListByTeacherService {
    
    List<BenefitResponseDto> cu55ListBenefitsByTeacher(User user);
}
