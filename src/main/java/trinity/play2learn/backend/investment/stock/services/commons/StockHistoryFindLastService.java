package trinity.play2learn.backend.investment.stock.services.commons;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;
import trinity.play2learn.backend.investment.stock.models.Stock;
import trinity.play2learn.backend.investment.stock.models.StockHistory;
import trinity.play2learn.backend.investment.stock.repositories.IStockHistoryRepository;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockHistoryFindLastService;

@Service
@AllArgsConstructor
public class StockHistoryFindLastService implements IStockHistoryFindLastService {

    private final IStockHistoryRepository stockHistoryRepository;
    
    @Override
    public StockHistory execute(Stock stock) {
        return stockHistoryRepository.findTopByStockOrderByCreatedAtDesc(stock)
        .orElseThrow(() -> new NotFoundException("No se encontro el stock"));
    }
    
}
