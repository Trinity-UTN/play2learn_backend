package trinity.play2learn.backend.benefits.services.commons;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.benefits.models.BenefitPurchase;
import trinity.play2learn.backend.benefits.repositories.IBenefitPurchaseRepository;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitGetLastPurchaseService;

@Service
@AllArgsConstructor
public class BenefitGetLastPurchaseService implements IBenefitGetLastPurchaseService {
    
    private final IBenefitPurchaseRepository benefitPurchaseRepository;

    @Override
    public Optional<BenefitPurchase> getLastPurchase(Benefit benefit, Student student) {
    
        return benefitPurchaseRepository.findTopByBenefitAndStudentOrderByPurchasedAtDesc(benefit, student);
    }
    

    
}
