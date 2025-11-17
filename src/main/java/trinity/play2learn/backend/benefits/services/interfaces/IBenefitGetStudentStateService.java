package trinity.play2learn.backend.benefits.services.interfaces;

import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.benefits.models.BenefitStudentState;

public interface IBenefitGetStudentStateService {
    
    BenefitStudentState getStudentState(Benefit benefit, Student student);
    
}
