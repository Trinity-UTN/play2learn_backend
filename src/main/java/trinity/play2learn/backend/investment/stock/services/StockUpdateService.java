package trinity.play2learn.backend.investment.stock.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.investment.stock.mappers.StockHistoryMapper;
import trinity.play2learn.backend.investment.stock.models.Stock;
import trinity.play2learn.backend.investment.stock.repositories.IStockHistoryRepository;
import trinity.play2learn.backend.investment.stock.repositories.IStockRepository;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockCalculateVariationService;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockFindAllService;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockUpdateService;

@Service
@AllArgsConstructor
public class StockUpdateService implements IStockUpdateService {

    private final IStockFindAllService stockFindAllService;

    private final IStockRepository stockRepository;

    private final IStockHistoryRepository stockHistoryRepository;

    private final IStockCalculateVariationService stockCalculateVariationService;
    
    @Override
    @Transactional
    @Scheduled(cron = "0 0 1 * * *") // Se ejecuta todos los días a la 1:00 AM
    //@Scheduled(cron = "*/10 * * * * *") Para pruebas, se ejecuta cada 10 segundos
    public void cu78updateStock() {
        // Recorre todos los stocks y actualiza su precio
        for (Stock stock : stockFindAllService.execute()){
            
            // Obtiene la variacion del stock
            Double variation = stockCalculateVariationService.cu84calculateVariation(stock);

            // Actualiza el precio siempre dentro de los limites
            // Limite superior: 2.5 veces el precio inicial
            // Limite inferior: 0
            Double newPrice = Math.min(Math.max(stock.getCurrentPrice() * (1 + variation / 100), 0.0), stock.getInitialPrice().doubleValue()*2.5);

            stock.setCurrentPrice(newPrice);

            Stock stockUpdated = stockRepository.save(stock);

            stockHistoryRepository.save(StockHistoryMapper.toModel (stockUpdated, variation));
        }
    }
    
}
