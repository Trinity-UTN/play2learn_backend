package trinity.play2learn.backend.investment.stock.dtos.request;

import java.math.BigInteger;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import trinity.play2learn.backend.investment.stock.models.OrderStop;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockOrderStopRequestDto {
    
    @NotNull
    private Long stockId;

    @NotNull
    @Positive
    private BigInteger quantity;

    @NotNull
    @Positive
    private Double pricePerUnit;

    @NotNull
    private OrderStop orderStop;

}
