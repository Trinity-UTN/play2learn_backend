package trinity.play2learn.backend.benefits.services.commons;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.benefits.dtos.benefit.BenefitStudentResponseDto;
import trinity.play2learn.backend.benefits.mappers.BenefitMapper;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.benefits.models.BenefitStudentState;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitCreateStudentDtosService;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitGetPurchasesLeftByStudentService;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitGetStudentStateService;

@Service
@AllArgsConstructor
public class BenefitCreateStudentDtosService implements IBenefitCreateStudentDtosService {

    private final IBenefitGetStudentStateService benefitGetStudentStateService;
    private final IBenefitGetPurchasesLeftByStudentService benefitGetPurchasesLeftByStudentService;

    @Override
    public List<BenefitStudentResponseDto> createBenefitStudentDtos(List<Benefit> benefits, Student student) {

        List<BenefitStudentResponseDto> benefitStudentResponseDtos = new ArrayList<>();

        for (Benefit benefit : benefits) {

            BenefitStudentState benefitStudentState = benefitGetStudentStateService.getStudentState(benefit, student);

            Integer purchasesLeftByStudent = benefitGetPurchasesLeftByStudentService.getPurchasesLeftByStudent(benefit, student);

            BenefitStudentResponseDto benefitStudentResponseDto = BenefitMapper.toStudentDto(benefit, benefitStudentState, purchasesLeftByStudent);
            benefitStudentResponseDtos.add(benefitStudentResponseDto);
        }

        return benefitStudentResponseDtos;
    }

}
