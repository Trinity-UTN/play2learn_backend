package trinity.play2learn.backend.benefits.dtos;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import trinity.play2learn.backend.benefits.models.BenefitCategory;
import trinity.play2learn.backend.benefits.models.BenefitColor;
import trinity.play2learn.backend.benefits.models.BenefitIcon;
import trinity.play2learn.backend.configs.messages.ValidationMessages;

@Data
@AllArgsConstructor
@Builder
public class BenefitRequestDto {
    
    @NotBlank(message = ValidationMessages.NOT_EMPTY_NAME)
    @Size(max = 100, message = ValidationMessages.MAX_LENGTH_NAME_100)
    private String name;

    @NotBlank(message = ValidationMessages.NOT_EMPTY_DESCRIPTION)
    @Size(max = 1000, message = ValidationMessages.MAX_LENGTH_DESCRIPTION_1000)
    private String description;

    @NotNull(message = ValidationMessages.NOT_NULL_COST)
    @Min(value = 1 , message = ValidationMessages.MIN_COST)
    private Long cost;

    //Cantidad total de veces que se pueden canjear el beneficio
    private Integer totalRedeemableAmount;
    //Cantidad de veces que se puede canjear el beneficio por estudiante
    private Integer redeemableAmountPerStudent;

    @NotNull(message = ValidationMessages.NOT_NULL_END_AT)
    @Future(message = ValidationMessages.FUTURE_END_AT)
    private LocalDateTime endAt;

    @NotNull(message = ValidationMessages.NOT_NULL_SUBJECT)
    private Long subjectId;

    @NotNull(message = ValidationMessages.NOT_NULL_ICON)
    private BenefitIcon icon;
    
    @NotNull(message = ValidationMessages.NOT_NULL_CATEGORY)
    private BenefitCategory category;

    @NotNull(message = ValidationMessages.NOT_NULL_COLOR)
    private BenefitColor color;
}
