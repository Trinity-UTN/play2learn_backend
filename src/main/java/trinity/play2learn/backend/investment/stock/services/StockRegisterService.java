package trinity.play2learn.backend.investment.stock.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.investment.stock.dtos.request.StockRegisterRequestDto;
import trinity.play2learn.backend.investment.stock.dtos.response.StockResponseDto;
import trinity.play2learn.backend.investment.stock.mappers.StockHistoryMapper;
import trinity.play2learn.backend.investment.stock.mappers.StockMapper;
import trinity.play2learn.backend.investment.stock.models.Stock;
import trinity.play2learn.backend.investment.stock.repositories.IStockHistoryRepository;
import trinity.play2learn.backend.investment.stock.repositories.IStockRepository;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockRegisterService;

@Service
@AllArgsConstructor
public class StockRegisterService implements IStockRegisterService {
    
    private final IStockRepository stockRepository;

    private final IStockHistoryRepository stockHistoryRepository;
    

    @Override
    @Transactional
    public StockResponseDto cu77registerStock (StockRegisterRequestDto stockDto) {

        Stock stock = stockRepository.save (StockMapper.toModel (stockDto));

        stockHistoryRepository.save(StockHistoryMapper.toModel (stock, 0.0));

        return StockMapper.toDto (stock, null, null);
    }

    
}
