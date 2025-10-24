package trinity.play2learn.backend.investment.stock.services.interfaces;

import trinity.play2learn.backend.investment.stock.dtos.request.StockOrderStopRequestDto;
import trinity.play2learn.backend.investment.stock.dtos.response.StockBuyResponseDto;
import trinity.play2learn.backend.user.models.User;

public interface IStockRegisterStopService {

    public StockBuyResponseDto cu91registerStopStock (StockOrderStopRequestDto dto, User user);
    
} 