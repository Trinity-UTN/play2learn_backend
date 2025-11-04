package trinity.play2learn.backend.investment.stock.services.commons;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.investment.stock.models.Stock;
import trinity.play2learn.backend.investment.stock.repositories.IStockRepository;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockCalculateAmountInvestedService;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockCalculateByWalletService;

@Service
@AllArgsConstructor
public class StockCalculateAmountInvestedService implements IStockCalculateAmountInvestedService {
    
    private final IStockRepository stockRepository;

    private final IStockCalculateByWalletService stockCalculateByWalletService;
    
    @Override
    public Double execute(Wallet wallet) {

        Double total = 0.0;

        for (Stock stock : stockRepository.findAll()) {
            total += stock.getCurrentPrice() * stockCalculateByWalletService.execute(stock, wallet).doubleValue();
        }
        
        return total;
    }
    
}
