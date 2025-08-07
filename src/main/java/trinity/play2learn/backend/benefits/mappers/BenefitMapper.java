package trinity.play2learn.backend.benefits.mappers;

import trinity.play2learn.backend.admin.subject.mappers.SubjectMapper;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.benefits.dtos.BenefitRequestDto;
import trinity.play2learn.backend.benefits.dtos.BenefitResponseDto;
import trinity.play2learn.backend.benefits.models.Benefit;

public class BenefitMapper {
    
    public static Benefit toModel(BenefitRequestDto benefitDto , Subject subject){ 
        return Benefit.builder()
            .name(benefitDto.getName())
            .description(benefitDto.getDescription())
            .cost(benefitDto.getCost())
            .totalRedeemableAmount(benefitDto.getTotalRedeemableAmount())
            .redeemableAmountPerStudent(benefitDto.getRedeemableAmountPerStudent())
            .subject(subject)
            .build();
    }

    public static BenefitResponseDto toDto(Benefit benefit){
        return BenefitResponseDto.builder()
            .id(benefit.getId())
            .name(benefit.getName())
            .description(benefit.getDescription())
            .cost(benefit.getCost())
            .totalRedeemableAmount(benefit.getTotalRedeemableAmount())
            .redeemableAmountPerStudent(benefit.getRedeemableAmountPerStudent())
            .subjectDto(SubjectMapper.toSubjectDto(benefit.getSubject()))
            .build();
    }
}
