package trinity.play2learn.backend.investment.stock.services.interfaces;

import trinity.play2learn.backend.investment.stock.dtos.request.StockBuyRequestDto;
import trinity.play2learn.backend.investment.stock.dtos.response.StockSellResponseDto;
import trinity.play2learn.backend.user.models.User;

public interface IStockSellService {

    public StockSellResponseDto cu90sellStock (StockBuyRequestDto dto, User user);
    
}
