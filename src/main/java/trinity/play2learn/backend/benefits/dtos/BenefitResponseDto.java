package trinity.play2learn.backend.benefits.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import trinity.play2learn.backend.admin.subject.dtos.SubjectResponseDto;

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
    private SubjectResponseDto subjectDto;
    
}
