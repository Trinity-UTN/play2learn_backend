package trinity.play2learn.backend.investment.stock.services.commons;

import java.math.BigInteger;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.configs.exceptions.BadRequestException;
import trinity.play2learn.backend.investment.stock.models.Stock;
import trinity.play2learn.backend.investment.stock.repositories.IStockRepository;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockMoveService;

@Service
@AllArgsConstructor
public class StockMoveService implements IStockMoveService {

    private final IStockRepository stockRepository;

    @Override
    public void toSold(Stock stock, BigInteger quantity) {
        if (stock.getAvailableAmount().compareTo(quantity) < 0) {
            throw new BadRequestException("No hay acciones disponibles suficientes para mover a vendidas.");
        }

        stock.setAvailableAmount(stock.getAvailableAmount().subtract(quantity));

        stock.setSoldAmount(stock.getSoldAmount().add(quantity));

        stockRepository.save(stock);
        
    }

    @Override
    public void toAvailable(Stock stock, BigInteger quantity){
        if (stock.getSoldAmount().compareTo(quantity) < 0) {
            throw new BadRequestException("No hay acciones vendidas suficientes para mover a disponibles.");
        }

        stock.setSoldAmount(stock.getSoldAmount().subtract(quantity));

        stock.setAvailableAmount(stock.getAvailableAmount().add(quantity));

        stockRepository.save(stock);
    }
    
}
