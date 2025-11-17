package trinity.play2learn.backend.benefits.services.interfaces;

import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.benefits.models.Benefit;

public interface IBenefitIsPurchasedService {
    
    Boolean isPurchased(Student student, Benefit benefit);
}
