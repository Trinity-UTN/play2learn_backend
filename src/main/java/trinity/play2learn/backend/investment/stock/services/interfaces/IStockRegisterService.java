package trinity.play2learn.backend.investment.stock.services.interfaces;

import trinity.play2learn.backend.investment.stock.dtos.request.StockRegisterRequestDto;
import trinity.play2learn.backend.investment.stock.dtos.response.StockResponseDto;

public interface IStockRegisterService {
    
    public StockResponseDto cu77registerStock (StockRegisterRequestDto stockDto);
    
}
