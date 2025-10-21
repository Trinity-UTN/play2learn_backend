package trinity.play2learn.backend.investment.stock.dtos.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CandleStickChartValueResponseDto {

    private LocalDateTime date;

    private Double open;

    private Double close;

    private Double high;

    private Double low;
    
}
