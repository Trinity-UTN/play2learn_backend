package trinity.play2learn.backend.benefits.dtos.benefit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BenefitStudentCountResponseDto {
    
    private int available;
    private int purchased;
    private int use_requested;
    private int used;
    private int expired;
}
