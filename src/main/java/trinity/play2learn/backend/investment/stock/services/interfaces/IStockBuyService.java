package trinity.play2learn.backend.investment.stock.services.interfaces;

import trinity.play2learn.backend.investment.stock.dtos.request.StockBuyRequestDto;
import trinity.play2learn.backend.investment.stock.dtos.response.StockBuyResponseDto;
import trinity.play2learn.backend.user.models.User;

public interface IStockBuyService {
   
    public StockBuyResponseDto cu84buystocks (StockBuyRequestDto stockBuyRequestDto, User user);

}
