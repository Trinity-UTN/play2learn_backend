package trinity.play2learn.backend.investment.stock.dtos.response;

import java.math.BigInteger;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StockSellResponseDto {
    
    private BigInteger quantity;

    private Double pricePerUnit;

    private Double total;
    
}
