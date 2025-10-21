package trinity.play2learn.backend.investment.stock.services.commons;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.investment.stock.models.Stock;
import trinity.play2learn.backend.investment.stock.models.StockHistory;
import trinity.play2learn.backend.investment.stock.repositories.IStockHistoryRepository;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockHistoryFindByStockAndRangeService;

@Service
@AllArgsConstructor
public class StockHistoryFindByStockAndRangeService implements IStockHistoryFindByStockAndRangeService {
    
    private final IStockHistoryRepository stockHistoryRepository;

    @Override
    public List<StockHistory> execute(
        Stock stock,
        LocalDateTime startRange, 
        LocalDateTime endRange
    ) {
        return stockHistoryRepository.findByStockAndCreatedAtBetweenOrderByCreatedAtAsc(
            stock, startRange, endRange
        );
    }

    
}
