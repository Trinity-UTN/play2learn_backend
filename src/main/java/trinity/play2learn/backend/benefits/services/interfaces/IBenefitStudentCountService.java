package trinity.play2learn.backend.benefits.services.interfaces;

import trinity.play2learn.backend.benefits.dtos.benefit.BenefitStudentCountResponseDto;
import trinity.play2learn.backend.user.models.User;

public interface IBenefitStudentCountService {
    
    BenefitStudentCountResponseDto cu89CountByStudentState(User user);

}
