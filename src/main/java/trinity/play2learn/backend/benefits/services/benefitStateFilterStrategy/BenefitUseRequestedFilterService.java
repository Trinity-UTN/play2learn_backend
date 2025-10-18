package trinity.play2learn.backend.benefits.services.benefitStateFilterStrategy;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitFilterStrategyService;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitIsUseRequestedService;

@Service("USE_REQUESTED")
@AllArgsConstructor
public class BenefitUseRequestedFilterService implements IBenefitFilterStrategyService {
    
    private final IBenefitIsUseRequestedService benefitIsUseRequestedService;

    @Override
    public List<Benefit> filter(List<Benefit> benefits, Student student) {

        return benefits
            .stream()
            .filter(b -> b.getEndAt().isAfter(LocalDateTime.now()))
            .filter(b -> benefitIsUseRequestedService.isUseRequested(b, student))
            .toList();
    }
}
