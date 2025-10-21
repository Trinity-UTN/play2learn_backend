package trinity.play2learn.backend.investment.stock.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.investment.stock.models.Stock;
import trinity.play2learn.backend.investment.stock.models.StockHistory;

public interface IStockHistoryFindByStockService {
    
    public List<StockHistory> execute (Stock stock);
    
}
