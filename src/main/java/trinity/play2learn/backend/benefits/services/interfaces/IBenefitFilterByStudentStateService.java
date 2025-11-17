package trinity.play2learn.backend.benefits.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.benefits.models.BenefitStudentState;

public interface IBenefitFilterByStudentStateService {
    
    List<Benefit> filterByStudentState(List<Benefit> benefits, Student student, BenefitStudentState state);
}
