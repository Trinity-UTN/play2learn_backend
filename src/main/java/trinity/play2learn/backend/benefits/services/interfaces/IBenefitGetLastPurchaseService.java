package trinity.play2learn.backend.benefits.services.interfaces;

import java.util.Optional;

import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.benefits.models.BenefitPurchase;

public interface IBenefitGetLastPurchaseService {
    
    Optional<BenefitPurchase> getLastPurchase(Benefit benefit, Student student);
}
