package trinity.play2learn.backend.investment.stock.services.commons;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.investment.stock.models.Stock;
import trinity.play2learn.backend.investment.stock.models.StockHistory;
import trinity.play2learn.backend.investment.stock.repositories.IStockHistoryRepository;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockHistoryCalculateTrendService;

@Service
@AllArgsConstructor
public class StockCalculateTrendService implements IStockHistoryCalculateTrendService {
    
    private final IStockHistoryRepository stockHistoryRepository;
    
    @Override
    public boolean execute(Stock stock) {
        // Obtiene la tendencia de la accion basado en las ultimas 10 variaciones
        // Si la suma de las variaciones es positiva, la tendencia es alcista
        // Si la suma de las variaciones es negativa, la tendencia es bajista
        double tendencia = stockHistoryRepository.findTop10ByStockOrderByCreatedAtAsc(stock).stream()
            .mapToDouble(StockHistory::getVariation)
            .sum();
        return tendencia > 0;
    }
    
}
