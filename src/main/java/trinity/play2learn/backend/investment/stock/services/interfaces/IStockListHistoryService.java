package trinity.play2learn.backend.investment.stock.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.investment.stock.dtos.response.StockHistoryResponseDto;

public interface IStockListHistoryService {

    public List<StockHistoryResponseDto> cu79getStockHistories (Long stockId);

} 
