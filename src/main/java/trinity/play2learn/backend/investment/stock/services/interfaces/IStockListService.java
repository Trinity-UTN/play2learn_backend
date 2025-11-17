package trinity.play2learn.backend.investment.stock.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.investment.stock.dtos.response.StockResponseDto;

public interface IStockListService {

    public List<StockResponseDto> cu86listStocks();
    
} 