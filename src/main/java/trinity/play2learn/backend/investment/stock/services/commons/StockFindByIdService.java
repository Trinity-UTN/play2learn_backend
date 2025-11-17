package trinity.play2learn.backend.investment.stock.services.commons;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;
import trinity.play2learn.backend.investment.stock.models.Stock;
import trinity.play2learn.backend.investment.stock.repositories.IStockRepository;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockFindByIdService;

@Service
@AllArgsConstructor
public class StockFindByIdService implements IStockFindByIdService{
    
    private final IStockRepository stockRepository;
    
    @Override
    public Stock execute(Long stockId) {
        return stockRepository.findById(stockId).orElseThrow(
            () -> new NotFoundException("Accion no encontrada"));
    }
    
}
