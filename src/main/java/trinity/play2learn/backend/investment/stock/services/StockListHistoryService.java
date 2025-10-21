package trinity.play2learn.backend.investment.stock.services;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.investment.stock.dtos.response.StockHistoryResponseDto;
import trinity.play2learn.backend.investment.stock.mappers.StockHistoryMapper;
import trinity.play2learn.backend.investment.stock.models.Stock;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockFindByIdService;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockHistoryFindByStockService;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockListHistoryService;

@Service
@AllArgsConstructor
public class StockListHistoryService implements IStockListHistoryService {

    private final IStockFindByIdService stockFindByIdService;

    private final IStockHistoryFindByStockService stockHistoryFindByStockService;
    
    
    @Override
    public List<StockHistoryResponseDto> cu79getStockHistories(Long stockId) {

        Stock stock = stockFindByIdService.execute(stockId);

        return StockHistoryMapper.toDtoList(stockHistoryFindByStockService.execute(stock));
    }
    
}
