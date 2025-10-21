package trinity.play2learn.backend.investment.stock.services.interfaces;

import trinity.play2learn.backend.investment.stock.models.Stock;
import trinity.play2learn.backend.investment.stock.models.StockHistory;

public interface IStockHistoryFindLastService {
    
    public StockHistory execute (Stock stock);

}
