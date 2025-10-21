package trinity.play2learn.backend.investment.stock.dtos.response;

import java.math.BigInteger;

import lombok.Builder;
import lombok.Data;
import trinity.play2learn.backend.investment.stock.models.RiskLevel;

@Data
@Builder
public class StockResponseDto {
    
    private Long id;

    private String name;

    private String abbreviation;

    private BigInteger totalAmount;

    private BigInteger availableAmount;

    private BigInteger soldAmount;

    private Double currentPrice;

    private Double initialPrice;

    private RiskLevel riskLevel;

}
