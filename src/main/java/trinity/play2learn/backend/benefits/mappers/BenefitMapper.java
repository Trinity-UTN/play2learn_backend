package trinity.play2learn.backend.benefits.mappers;

import java.util.List;
import trinity.play2learn.backend.admin.subject.mappers.SubjectMapper;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.benefits.dtos.benefit.BenefitRequestDto;
import trinity.play2learn.backend.benefits.dtos.benefit.BenefitResponseDto;
import trinity.play2learn.backend.benefits.dtos.benefit.BenefitStudentCountResponseDto;
import trinity.play2learn.backend.benefits.dtos.benefit.BenefitStudentResponseDto;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.benefits.models.BenefitStudentState;

public class BenefitMapper {
    
    public static Benefit toModel(BenefitRequestDto benefitDto , Subject subject){ 
        return Benefit.builder()
            .name(benefitDto.getName())
            .description(benefitDto.getDescription())
            .cost(benefitDto.getCost())
            .purchaseLimit(benefitDto.getPurchaseLimit())
            .purchaseLimitPerStudent(benefitDto.getPurchaseLimitPerStudent())
            .endAt(benefitDto.getEndAt())
            .subject(subject)
            .icon(benefitDto.getIcon())
            .category(benefitDto.getCategory())
            .color(benefitDto.getColor())
            .build();
    }

    public static BenefitResponseDto toDto(Benefit benefit){
        return BenefitResponseDto.builder()
            .id(benefit.getId())
            .name(benefit.getName())
            .description(benefit.getDescription())
            .cost(benefit.getCost())
            .purchaseLimit(benefit.getPurchaseLimit())
            .purchaseLimitPerStudent(benefit.getPurchaseLimitPerStudent())
            .endAt(benefit.getEndAt())
            .state(benefit.getState())
            .subjectDto(SubjectMapper.toSimplifiedDto(benefit.getSubject()))
            .icon(benefit.getIcon())
            .category(benefit.getCategory())
            .color(benefit.getColor())
            .build();
    }

    public static List<BenefitResponseDto> toListDto(List<Benefit> benefits) {
        return benefits.stream()
            .map(BenefitMapper::toDto)
            .toList();
    }

    public static BenefitStudentResponseDto toStudentDto(
        Benefit benefit, BenefitStudentState state, Integer purchasesLeftByStudent) {
     
        return BenefitStudentResponseDto.builder()
            .id(benefit.getId())
            .name(benefit.getName())
            .description(benefit.getDescription())
            .cost(benefit.getCost())
            .state(state)
            .purchasesLeft(benefit.getPurchasesLeft())
            .purchasesLeftByStudent(purchasesLeftByStudent)
            .endAt(benefit.getEndAt())
            .subjectId(benefit.getSubject().getId())
            .subjectName(benefit.getSubject().getName())
            .icon(benefit.getIcon())
            .category(benefit.getCategory())
            .color(benefit.getColor())
            .build();
    }

    public static BenefitStudentCountResponseDto tCountResponseDto(Integer available, Integer purchased, Integer use_requested, Integer used, Integer expired) {

        return BenefitStudentCountResponseDto.builder()
            .available(available)
            .purchased(purchased)
            .use_requested(use_requested)
            .used(used)
            .expired(expired)
            .build();
    }
}
