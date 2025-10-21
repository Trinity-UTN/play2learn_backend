package trinity.play2learn.backend.investment.stock.mappers;

import java.math.BigInteger;
import java.util.List;

import trinity.play2learn.backend.investment.stock.dtos.request.StockRegisterRequestDto;
import trinity.play2learn.backend.investment.stock.dtos.response.StockResponseDto;
import trinity.play2learn.backend.investment.stock.models.Stock;

public class StockMapper {

    public static Stock toModel(StockRegisterRequestDto stockDto) {
        return Stock.builder()
            .name(stockDto.getName())
            .abbreviation(stockDto.getAbbreviation())
            .initialPrice(stockDto.getInitialPrice())
            .currentPrice(stockDto.getInitialPrice())
            .availableAmount(stockDto.getTotalAmount())
            .totalAmount(stockDto.getTotalAmount())
            .soldAmount(BigInteger.ZERO)
            .riskLevel(stockDto.getRiskLevel())
            .build();
    }

    public static StockResponseDto toDto (Stock stock) {
        return StockResponseDto.builder()
            .id(stock.getId())
            .name(stock.getName())
            .abbreviation(stock.getAbbreviation())
            .totalAmount(stock.getTotalAmount())
            .availableAmount(stock.getAvailableAmount())
            .soldAmount(stock.getSoldAmount())
            .currentPrice(stock.getCurrentPrice())
            .initialPrice(stock.getInitialPrice())
            .riskLevel(stock.getRiskLevel())
            .build();
    }

    public static List<StockResponseDto> toDtoList (List<Stock> stocks) {
        return stocks.stream().map(StockMapper::toDto).toList();
    }
    
}
