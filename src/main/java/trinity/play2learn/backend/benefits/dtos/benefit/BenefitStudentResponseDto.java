package trinity.play2learn.backend.benefits.dtos.benefit;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.benefits.models.BenefitCategory;
import trinity.play2learn.backend.benefits.models.BenefitColor;
import trinity.play2learn.backend.benefits.models.BenefitIcon;
import trinity.play2learn.backend.benefits.models.BenefitStudentState;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BenefitStudentResponseDto {
    
    private Long id;
    private String name;
    private String description;
    private Long cost;
    private BenefitStudentState state; //Available, Purchased, UseRequested, Expired
    private Integer purchasesLeft; //Null si es ilimitado
    private Integer purchasesLeftByStudent; //Null si es ilimitado
    private LocalDateTime endAt; //Fecha de vencimiento
    private Long subjectId;
    private String subjectName;
    private BenefitIcon icon;
    private BenefitCategory category;
    private BenefitColor color;
}
