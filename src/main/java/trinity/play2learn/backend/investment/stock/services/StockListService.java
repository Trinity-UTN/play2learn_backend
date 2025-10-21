package trinity.play2learn.backend.investment.stock.services;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.investment.stock.dtos.response.StockResponseDto;
import trinity.play2learn.backend.investment.stock.mappers.StockMapper;
import trinity.play2learn.backend.investment.stock.repositories.IStockRepository;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockListService;

@Service
@AllArgsConstructor
public class StockListService implements IStockListService {
    
    private final IStockRepository stockRepository;
    
    @Override
    public List<StockResponseDto> cu86listStocks() {
        return StockMapper.toDtoList(
            stockRepository.findAll()
        );
    }
    
}
