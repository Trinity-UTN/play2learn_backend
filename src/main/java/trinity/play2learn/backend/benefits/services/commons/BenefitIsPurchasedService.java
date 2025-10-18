package trinity.play2learn.backend.benefits.services.commons;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.benefits.models.BenefitPurchase;
import trinity.play2learn.backend.benefits.repositories.IBenefitPurchaseRepository;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitIsPurchasedService;

@Service
@AllArgsConstructor
public class BenefitIsPurchasedService implements IBenefitIsPurchasedService{
    
    private final IBenefitPurchaseRepository benefitPurchaseRepository;

    @Override
    public Boolean isPurchased(Student student, Benefit benefit) {
        
        Optional<BenefitPurchase> lastBenefitPurchase = benefitPurchaseRepository.findTopByBenefitAndStudentOrderByPurchasedAtDesc(benefit, student);

        if (lastBenefitPurchase.isEmpty()) {
            return false;
        }

        return lastBenefitPurchase.get().isPurchased();

    }
    
    
}
