package trinity.play2learn.backend.investment.stock.services.interfaces;

import trinity.play2learn.backend.investment.stock.models.Stock;

public interface IStockHistoryCalculateTrendService {

    public boolean execute (Stock stock);
    
}
