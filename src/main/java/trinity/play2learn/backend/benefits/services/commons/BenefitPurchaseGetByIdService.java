package trinity.play2learn.backend.benefits.services.commons;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.benefits.models.BenefitPurchase;
import trinity.play2learn.backend.benefits.repositories.IBenefitPurchaseRepository;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitPurchaseGetByIdService;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;

@Service
@AllArgsConstructor
public class BenefitPurchaseGetByIdService implements IBenefitPurchaseGetByIdService{
    
    private final IBenefitPurchaseRepository benefitPurchaseRepository;

    @Override
    public BenefitPurchase getById(Long id) {
        
        return benefitPurchaseRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(
            () -> new NotFoundException("No existe una compra de beneficio con el id proporcionado")
        );
    }
}
