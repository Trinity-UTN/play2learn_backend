package trinity.play2learn.backend.benefits.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.benefits.models.Benefit;

public interface IBenefitFilterStrategyService {
    
    List<Benefit> filter(List<Benefit> benefits, Student student);
}
