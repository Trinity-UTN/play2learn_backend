package trinity.play2learn.backend.investment.stock.services.interfaces;

import java.time.LocalDateTime;
import java.util.List;

import trinity.play2learn.backend.investment.stock.models.Stock;
import trinity.play2learn.backend.investment.stock.models.StockHistory;

public interface IStockHistoryFindByStockAndRangeService {
    
    public List<StockHistory> execute(Stock stock, LocalDateTime startRange, LocalDateTime endRange);
    
}
