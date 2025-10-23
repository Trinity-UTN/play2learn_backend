package trinity.play2learn.backend.investment.stock.services.commons;

import java.math.BigInteger;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.investment.stock.models.Order;
import trinity.play2learn.backend.investment.stock.models.OrderType;
import trinity.play2learn.backend.investment.stock.models.Stock;
import trinity.play2learn.backend.investment.stock.repositories.IOrderRepository;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockCalculateByWalletService;

@Service
@AllArgsConstructor
public class StockCalculateByWalletService implements IStockCalculateByWalletService {
    
    private IOrderRepository orderRepository;
    
    @Override
    public BigInteger execute(Stock stock, Wallet wallet) {
        BigInteger total = BigInteger.ZERO;
        for (Order order : orderRepository.findByWalletAndStock(wallet, stock)) {
            total = total.add((order.getOrderType() == OrderType.COMPRA) ? order.getQuantity() : order.getQuantity().negate());
        }
        return total;
    }
    
}
