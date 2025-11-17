package trinity.play2learn.backend.benefits.services.commons;

import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.benefits.models.BenefitStudentState;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitGetStudentStateService;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitIsPurchasedService;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitIsUseRequestedService;

@Service
@AllArgsConstructor
public class BenefitGetStudentStateService implements IBenefitGetStudentStateService {

    private final IBenefitIsPurchasedService benefitIsPurchasedService;
    private final IBenefitIsUseRequestedService benefitIsUseRequestedService;

    @Override
    public BenefitStudentState getStudentState(Benefit benefit, Student student) {

        BenefitStudentState benefitStudentState = BenefitStudentState.AVAILABLE;

        if (benefitIsPurchasedService.isPurchased(student, benefit)) {
            benefitStudentState = BenefitStudentState.PURCHASED;
        }

        if (benefitIsUseRequestedService.isUseRequested(benefit, student)) {
            benefitStudentState = BenefitStudentState.USE_REQUESTED;
        }

        if (benefit.isExpired()) {
            benefitStudentState = BenefitStudentState.EXPIRED;
        }

        return benefitStudentState;
    }

}
