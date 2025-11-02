package trinity.play2learn.backend.investment.stock.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.investment.stock.dtos.response.StockSellResponseDto;
import trinity.play2learn.backend.investment.stock.models.Stock;

public interface IOrderFindPendingService {

    public List<StockSellResponseDto> execute(Stock stock, Wallet wallet);
    
} 