package trinity.play2learn.backend.benefits.services.commons;

import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitValidatePurchaseLimitService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;

@Service
@AllArgsConstructor
public class BenefitValidatePurchaseLimitService implements IBenefitValidatePurchaseLimitService {
    
    @Override
    public void validatePurchaseLimit(Benefit benefit) {
        
        //Si el beneficio no tiene un límite de compras, no se valida
        if (benefit.getPurchaseLimit() == null || benefit.getPurchaseLimit() == 0) {
            return;
        }
                
        if (benefit.getPurchasesLeft() <= 0) {
            throw new ConflictException("El beneficio ya ha alcanzado el límite de compras permitidas.");
        }
    }
    
    
}
