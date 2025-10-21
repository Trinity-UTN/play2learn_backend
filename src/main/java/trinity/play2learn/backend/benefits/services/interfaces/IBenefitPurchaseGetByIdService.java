package trinity.play2learn.backend.benefits.services.interfaces;

import trinity.play2learn.backend.benefits.models.BenefitPurchase;

public interface IBenefitPurchaseGetByIdService {
    
    BenefitPurchase getById(Long id);
}
