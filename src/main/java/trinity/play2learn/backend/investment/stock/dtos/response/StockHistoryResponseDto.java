package trinity.play2learn.backend.investment.stock.dtos.response;

import java.math.BigInteger;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StockHistoryResponseDto {

    private Long id;

    private Double price;

    private BigInteger soldAmount;

    private BigInteger availableAmount;

    private String timestamp;

    private Double variation;

    private LocalDateTime createdAt;
    
}
