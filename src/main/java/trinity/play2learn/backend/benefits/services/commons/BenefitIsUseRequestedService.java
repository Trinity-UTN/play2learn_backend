package trinity.play2learn.backend.benefits.services.commons;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.benefits.models.BenefitPurchase;
import trinity.play2learn.backend.benefits.repositories.IBenefitPurchaseRepository;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitIsUseRequestedService;

@Service
@AllArgsConstructor
public class BenefitIsUseRequestedService implements IBenefitIsUseRequestedService {

    private final IBenefitPurchaseRepository benefitPurchaseRepository;

    @Override
    public Boolean isUseRequested(Benefit benefit, Student student) {

        Optional<BenefitPurchase> lastBenefitPurchase = benefitPurchaseRepository.findTopByBenefitAndStudentOrderByPurchasedAtDesc(benefit, student);

        if (lastBenefitPurchase.isEmpty()) {
            return false;
        }

        return lastBenefitPurchase.get().isUseRequested();

    }
}
