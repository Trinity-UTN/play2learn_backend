package trinity.play2learn.backend.investment.stock.services.commons;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.investment.stock.models.Stock;
import trinity.play2learn.backend.investment.stock.models.StockHistory;
import trinity.play2learn.backend.investment.stock.repositories.IStockHistoryRepository;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockHistoryFindByStockService;

@Service
@AllArgsConstructor
public class StockHistoryFindByStockService implements IStockHistoryFindByStockService {

    private final IStockHistoryRepository stockHistoryRepository;

    @Override
    public List<StockHistory> execute(Stock stock) {
        return stockHistoryRepository.findTop100ByStockOrderByCreatedAtAsc(stock);
    }
    
}
