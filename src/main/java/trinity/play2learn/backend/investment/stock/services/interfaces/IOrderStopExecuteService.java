package trinity.play2learn.backend.investment.stock.services.interfaces;

import trinity.play2learn.backend.investment.stock.models.Stock;

public interface IOrderStopExecuteService {
    
    public void execute (Stock stock);
    
}
