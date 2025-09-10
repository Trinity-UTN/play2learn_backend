package trinity.play2learn.backend.statistics.home.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class StatisticsActivityDataDto {

    public String name;
    
    public int totalRealizations;

    public int createAgo;
    
}
