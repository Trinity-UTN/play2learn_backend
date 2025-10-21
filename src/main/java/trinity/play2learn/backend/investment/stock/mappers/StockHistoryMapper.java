package trinity.play2learn.backend.investment.stock.mappers;

import java.time.LocalDateTime;
import java.util.List;

import trinity.play2learn.backend.investment.stock.dtos.response.CandleStickChartValueResponseDto;
import trinity.play2learn.backend.investment.stock.dtos.response.StockHistoryResponseDto;
import trinity.play2learn.backend.investment.stock.models.Stock;
import trinity.play2learn.backend.investment.stock.models.StockHistory;

public class StockHistoryMapper {

    public static StockHistory toModel (
        Stock stock,
        Double variation
        ) {
        return StockHistory.builder()
            .stock(stock)
            .price(stock.getCurrentPrice())
            .availableAmount(stock.getAvailableAmount())
            .soldAmount(stock.getSoldAmount())
            .createdAt(LocalDateTime.now())
            .variation(variation)
            .build();
    }

    public static StockHistoryResponseDto toDto (StockHistory stockHistory) {
        return StockHistoryResponseDto.builder()
            .id(stockHistory.getId())
            .price(stockHistory.getPrice())
            .availableAmount(stockHistory.getAvailableAmount())
            .soldAmount(stockHistory.getSoldAmount())
            .createdAt(stockHistory.getCreatedAt())
            .variation(stockHistory.getVariation())
            .build();
    }

    public static List<StockHistoryResponseDto> toDtoList (List<StockHistory> stockHistories) {
        return stockHistories.stream()
            .map(StockHistoryMapper::toDto)
            .toList();
    }
    

    public static CandleStickChartValueResponseDto toCandleStickValueDto (
        LocalDateTime date,
        Double open,
        Double close,
        Double high,
        Double low
        ) {
        return CandleStickChartValueResponseDto.builder()
            .date(date)
            .open(open)
            .close(close)
            .high(high)
            .low(low)
            .build();
    }

}
