package trinity.play2learn.backend.investment.stock.services.interfaces;

import trinity.play2learn.backend.investment.stock.dtos.response.StockResponseDto;
import trinity.play2learn.backend.user.models.User;

public interface IStockGetService {
    
    public StockResponseDto cu100GetStock (Long id, User user);

}
