package trinity.play2learn.backend.investment.stock.dtos.request;

import java.math.BigInteger;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockBuyRequestDto {
    
    @NotNull
    private Long stockId;

    @NotNull
    @Positive
    private BigInteger quantity;
    
}
