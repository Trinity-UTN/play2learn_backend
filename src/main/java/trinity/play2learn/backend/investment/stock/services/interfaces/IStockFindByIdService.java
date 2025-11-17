package trinity.play2learn.backend.investment.stock.services.interfaces;

import trinity.play2learn.backend.investment.stock.models.Stock;

public interface IStockFindByIdService {

    public Stock execute (Long stockId);

} 
