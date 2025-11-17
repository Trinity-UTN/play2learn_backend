package trinity.play2learn.backend.benefits.services.interfaces;

import trinity.play2learn.backend.benefits.models.Benefit;

public interface IBenefitGetByIdService {
    
    Benefit getById(Long id);
}
