package trinity.play2learn.backend.investment.stock.services.commons;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.investment.stock.mappers.StockHistoryMapper;
import trinity.play2learn.backend.investment.stock.models.Stock;
import trinity.play2learn.backend.investment.stock.repositories.IStockHistoryRepository;
import trinity.play2learn.backend.investment.stock.repositories.IStockRepository;
import trinity.play2learn.backend.investment.stock.services.interfaces.IOrderStopExecuteService;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockCalculateVariationService;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockUpdateSpecificService;

@Service
@AllArgsConstructor
public class StockUpdateSpecificService implements IStockUpdateSpecificService {

    private final IStockRepository stockRepository;

    private final IStockHistoryRepository stockHistoryRepository;

    private final IStockCalculateVariationService stockCalculateVariationService;
    
    private final IOrderStopExecuteService orderStopExecuteService;
    
    @Override
    public void execute(Stock stock) {
        // Obtiene la variacion del stock
        
        Double variation = stockCalculateVariationService.execute(stock);

        // Actualiza el precio siempre dentro de los limites
        // Limite superior: 2.5 veces el precio inicial
        // Limite inferior: 0
        Double newPrice = Math.min(Math.max(stock.getCurrentPrice() * (1 + variation / 100), 0.0), stock.getInitialPrice().doubleValue()*2.5);

        stock.setCurrentPrice(newPrice);

        Stock stockUpdated = stockRepository.save(stock);

        stockHistoryRepository.save(StockHistoryMapper.toModel (stockUpdated, variation));

        orderStopExecuteService.execute(stockUpdated);
    }
    
}
