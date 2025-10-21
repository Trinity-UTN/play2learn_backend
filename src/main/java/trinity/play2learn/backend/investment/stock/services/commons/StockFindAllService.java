package trinity.play2learn.backend.investment.stock.services.commons;


import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.investment.stock.models.Stock;
import trinity.play2learn.backend.investment.stock.repositories.IStockRepository;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockFindAllService;

@Service
@AllArgsConstructor
public class StockFindAllService implements IStockFindAllService {
    
    private final IStockRepository stockRepository;

    @Override
    public Iterable<Stock> execute() {
        return stockRepository.findAll();
    }
}
