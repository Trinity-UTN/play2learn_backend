package trinity.play2learn.backend.benefits.mappers;

import trinity.play2learn.backend.admin.student.mappers.StudentMapper;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.benefits.dtos.benefitPurchase.BenefitPurchaseResponseDto;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.benefits.models.BenefitPurchase;
import trinity.play2learn.backend.benefits.models.BenefitPurchaseState;

public class BenefitPurchaseMapper {
    
    public static BenefitPurchase toModel(Benefit benefit, Student student) {
        return BenefitPurchase.builder()
            .benefit(benefit)
            .student(student)
            .state(BenefitPurchaseState.PURCHASED)
            .build();
    }

    public static BenefitPurchaseResponseDto toDto(BenefitPurchase benefitPurchase, Integer purchasesLeftByStudent) {
        return BenefitPurchaseResponseDto.builder()
            .id(benefitPurchase.getId())
            .student(StudentMapper.toSimplificatedDto(benefitPurchase.getStudent()))
            .state(benefitPurchase.getState())
            .purchasedAt(benefitPurchase.getPurchasedAt())
            .benefitDto(BenefitMapper.toDto(benefitPurchase.getBenefit()))
            .purchasesLeft(benefitPurchase.getBenefit().getPurchasesLeft())
            .purchasesLeftByStudent(purchasesLeftByStudent)
            .build();
    }
}
