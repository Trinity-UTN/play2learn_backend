package trinity.play2learn.backend.benefits.services.interfaces;

import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.benefits.models.Benefit;

public interface IBenefitIsUseRequestedService {
    
    Boolean isUseRequested(Benefit benefit, Student student);
}
