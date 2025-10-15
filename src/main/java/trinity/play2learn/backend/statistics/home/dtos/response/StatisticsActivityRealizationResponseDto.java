package trinity.play2learn.backend.statistics.home.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class StatisticsActivityRealizationResponseDto {

    private String name;

    private String subject;

    private String result;

    private Double reward;

    private String doneAgo;
    
}
