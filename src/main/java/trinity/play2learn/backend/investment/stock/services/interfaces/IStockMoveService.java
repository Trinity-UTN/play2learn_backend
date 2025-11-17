package trinity.play2learn.backend.investment.stock.services.interfaces;

import java.math.BigInteger;

import trinity.play2learn.backend.investment.stock.models.Stock;

public interface IStockMoveService {
    
    public void toSold (Stock stock, BigInteger quantity);

    public void toAvailable (Stock stock, BigInteger quantity);

}
