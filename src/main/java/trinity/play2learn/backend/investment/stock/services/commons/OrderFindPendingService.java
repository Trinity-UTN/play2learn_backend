package trinity.play2learn.backend.investment.stock.services.commons;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.investment.stock.dtos.response.StockSellResponseDto;
import trinity.play2learn.backend.investment.stock.mappers.OrderMapper;
import trinity.play2learn.backend.investment.stock.models.Order;
import trinity.play2learn.backend.investment.stock.models.OrderState;
import trinity.play2learn.backend.investment.stock.models.Stock;
import trinity.play2learn.backend.investment.stock.repositories.IOrderRepository;
import trinity.play2learn.backend.investment.stock.services.interfaces.IOrderFindPendingService;

@Service
@AllArgsConstructor
public class OrderFindPendingService implements IOrderFindPendingService {
    
    private final IOrderRepository orderRepository;
    
    @Override
    public List<StockSellResponseDto> execute(Stock stock, Wallet wallet) {
        List<Order> orders = orderRepository.findByWalletAndStockAndOrderState(
            wallet, 
            stock, 
            OrderState.PENDIENTE
        );

        return OrderMapper.toSellDtoList(orders);
    }


    
}
