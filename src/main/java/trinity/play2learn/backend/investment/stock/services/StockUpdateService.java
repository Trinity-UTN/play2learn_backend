package trinity.play2learn.backend.investment.stock.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.investment.stock.models.Stock;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockFindAllService;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockUpdateService;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockUpdateSpecificService;

@Service
@AllArgsConstructor
public class StockUpdateService implements IStockUpdateService {

    private final IStockFindAllService stockFindAllService;

    private final IStockUpdateSpecificService stockUpdateSpecificService;

    
    @Override
    @Transactional
    @Scheduled(cron = "0 0 1 * * *") // Se ejecuta todos los días a la 1:00 AM
    //@Scheduled(cron = "*/20 * * * * *") //Para pruebas, se ejecuta cada 10 segundos
    public void cu78updateStock() {
        // Recorre todos los stocks y actualiza su precio
        for (Stock stock : stockFindAllService.execute()){
            stockUpdateSpecificService.execute(stock);
        }
    }
    
}
