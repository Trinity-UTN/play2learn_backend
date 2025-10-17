package trinity.play2learn.backend.benefits.mappers;

import java.util.List;

import trinity.play2learn.backend.admin.subject.mappers.SubjectMapper;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.benefits.dtos.benefit.BenefitRequestDto;
import trinity.play2learn.backend.benefits.dtos.benefit.BenefitResponseDto;
import trinity.play2learn.backend.benefits.models.Benefit;

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
}
