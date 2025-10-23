package trinity.play2learn.backend.investment.stock.dtos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StockBuyResponseDto {

    private Long id;

    private Double pricePerUnit;

    private Integer quantity;

    private Double total;

    private String createdAt;
    
}
