package trinity.play2learn.backend.benefits.services.benefitStateFilterStrategy;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitFilterStrategyService;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitIsPurchasedService;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitIsUseRequestedService;

@Service("AVAILABLE")
@AllArgsConstructor
public class BenefitAvailableFilterService implements IBenefitFilterStrategyService{

    private final IBenefitIsPurchasedService benefitIsPurchasedService;
    private final IBenefitIsUseRequestedService benefitIsUseRequestedService;

    @Override
    public List<Benefit> filter(List<Benefit> benefits, Student student) {
        
        return benefits
            .stream()
            .filter(b -> b.getEndAt().isAfter(LocalDateTime.now())) //Quito los beneficios expirados
            .filter(b -> !benefitIsPurchasedService.isPurchased(student, b)) //Quito los beneficios que estan comprados por el estudiante
            .filter(b -> !benefitIsUseRequestedService.isUseRequested(b, student)) //Quito los beneficios que estan en solicitud de uso
            .toList();
        
    }
    
}
