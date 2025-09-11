package trinity.play2learn.backend.benefits.dtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import trinity.play2learn.backend.admin.subject.dtos.SubjectResponseDto;
import trinity.play2learn.backend.admin.subject.dtos.SubjectSimplifiedResponseDto;
import trinity.play2learn.backend.benefits.models.BenefitCategory;
import trinity.play2learn.backend.benefits.models.BenefitColor;
import trinity.play2learn.backend.benefits.models.BenefitIcon;

@Data
@Builder
@AllArgsConstructor
public class BenefitResponseDto {
    
    private Long id;
    private String name;
    private String description;
    private Long cost;
    private Integer totalRedeemableAmount;
    private Integer redeemableAmountPerStudent;
    private LocalDateTime endAt;
    private SubjectSimplifiedResponseDto subjectDto;
    private BenefitIcon icon;
    private BenefitCategory category;
    private BenefitColor color;
}
