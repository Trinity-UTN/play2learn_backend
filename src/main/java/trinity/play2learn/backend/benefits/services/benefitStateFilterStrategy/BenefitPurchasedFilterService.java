package trinity.play2learn.backend.benefits.services.benefitStateFilterStrategy;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitFilterStrategyService;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitIsPurchasedService;

@Service("PURCHASED")
@AllArgsConstructor
public class BenefitPurchasedFilterService implements IBenefitFilterStrategyService{
    
    private final IBenefitIsPurchasedService benefitIsPurchasedService;

    @Override
    public List<Benefit> filter(List<Benefit> benefits, Student student) {

        return benefits
            .stream()
            .filter(benefit -> benefit.getEndAt().isAfter(LocalDateTime.now()))
            .filter(benefit -> benefitIsPurchasedService.isPurchased(student, benefit))
            .toList();
    }
}
