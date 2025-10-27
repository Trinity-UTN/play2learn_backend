package trinity.play2learn.backend.benefits.mappers;

import java.util.List;

import trinity.play2learn.backend.admin.student.mappers.StudentMapper;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.benefits.dtos.benefitPurchase.BenefitPurchaseResponseDto;
import trinity.play2learn.backend.benefits.dtos.benefitPurchase.BenefitPurchaseSimpleResponseDto;
import trinity.play2learn.backend.benefits.dtos.benefitPurchase.BenefitPurchasedUsedResponseDto;
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

    public static BenefitPurchaseSimpleResponseDto toSimpleDto(
        BenefitPurchase benefitPurchase) {
        return BenefitPurchaseSimpleResponseDto.builder()
            .id(benefitPurchase.getId())
            .benefitId(benefitPurchase.getBenefit().getId())
            .benefitName(benefitPurchase.getBenefit().getName())
            .subjectId(benefitPurchase.getBenefit().getSubject().getId())
            .subjectName(benefitPurchase.getBenefit().getSubject().getName())
            .state(benefitPurchase.getState())
            .studentId(benefitPurchase.getStudent().getId())
            .studentName(benefitPurchase.getStudent().getCompleteName())
            .build();
    }

    public static List<BenefitPurchaseSimpleResponseDto> toSimpleDtoList(List<BenefitPurchase> benefitPurchases) {
        
        return benefitPurchases
            .stream()
            .map(benefitPurchase -> toSimpleDto(benefitPurchase))
            .toList();
    }

    public static BenefitPurchasedUsedResponseDto toUsedDto(BenefitPurchase benefitPurchase) {
        return BenefitPurchasedUsedResponseDto.builder()
            .id(benefitPurchase.getId())
            .state(benefitPurchase.getState())
            .benefitId(benefitPurchase.getBenefit().getId())
            .benefitName(benefitPurchase.getBenefit().getName())
            .benefitDescription(benefitPurchase.getBenefit().getDescription())
            .subjectId(benefitPurchase.getBenefit().getSubject().getId())
            .subjectName(benefitPurchase.getBenefit().getSubject().getName())
            .usedAt(benefitPurchase.getUsedAt())
            .category(benefitPurchase.getBenefit().getCategory())
            .build();
    }

    public static List<BenefitPurchasedUsedResponseDto> toUsedDtoList(List<BenefitPurchase> benefitPurchases) {
        return benefitPurchases
            .stream()
            .map(benefitPurchase -> toUsedDto(benefitPurchase))
            .toList();
    }
}
