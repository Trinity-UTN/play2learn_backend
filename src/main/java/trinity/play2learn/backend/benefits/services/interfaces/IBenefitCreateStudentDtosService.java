package trinity.play2learn.backend.benefits.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.benefits.dtos.benefit.BenefitStudentResponseDto;
import trinity.play2learn.backend.benefits.models.Benefit;

public interface IBenefitCreateStudentDtosService {
    
    List<BenefitStudentResponseDto> createBenefitStudentDtos(List<Benefit> benefits, Student student);
}
