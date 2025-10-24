package trinity.play2learn.backend.investment.stock.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.investment.stock.models.Order;
import trinity.play2learn.backend.investment.stock.models.OrderState;
import trinity.play2learn.backend.investment.stock.models.Stock;

public interface IOrderRepository extends CrudRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    
    List<Order> findByWalletAndStock (Wallet wallet, Stock stock);

    List<Order> findByWalletAndStockAndOrderState (Wallet wallet, Stock stock, OrderState orderState);
    
    List<Order> findByStockAndOrderStateOrderByCreatedAtAsc (Stock stock, OrderState orderState);

} 