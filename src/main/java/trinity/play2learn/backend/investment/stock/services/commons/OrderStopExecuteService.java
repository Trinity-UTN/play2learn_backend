package trinity.play2learn.backend.investment.stock.services.commons;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.transaction.models.TypeTransaction;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionGenerateService;
import trinity.play2learn.backend.economy.wallet.services.interfaces.IWalletUpdateInvestedBalanceService;
import trinity.play2learn.backend.investment.stock.models.Order;
import trinity.play2learn.backend.investment.stock.models.OrderState;
import trinity.play2learn.backend.investment.stock.models.OrderStop;
import trinity.play2learn.backend.investment.stock.models.Stock;
import trinity.play2learn.backend.investment.stock.models.StockHistory;
import trinity.play2learn.backend.investment.stock.repositories.IOrderRepository;
import trinity.play2learn.backend.investment.stock.services.interfaces.IOrderStopExecuteService;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockCalculateByWalletService;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockHistoryFindLastService;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockMoveService;

@Service
@AllArgsConstructor
public class OrderStopExecuteService implements IOrderStopExecuteService {

    private final IOrderRepository orderRepository;

    private final IStockCalculateByWalletService stockCalculateByWalletService;

    private final IStockHistoryFindLastService stockHistoryFindLastService;

    private final ITransactionGenerateService transactionGenerateService;

    private final IStockMoveService stockMoveService;

    private final IWalletUpdateInvestedBalanceService walletUpdateInvestedBalanceService;
    
    @Override
    public void execute(Stock stock) {

        List<Order> orders = orderRepository.findByStockAndOrderStateOrderByCreatedAtAsc(stock, OrderState.PENDIENTE);

        if (orders.isEmpty()) {
            return;
        }

        for (Order order : orders) {

            StockHistory stockHistory = stockHistoryFindLastService.execute(stock);

            /*
             * Esto valida lo siguiente:
             * Si la orden es de perdida y el precio de la accion es mayor o igual al 
             * precio de la orden, en este caso no se deberia de ejecutar la orden de venta.
             * Si la orden es de ganancia y el precio de la accion es menor o igual al
             * precio de la orden, en este caso no se deberia de ejecutar la orden de venta
             */
            if (
                (
                    order.getOrderStop().equals(OrderStop.LOSS)
                    && stockHistory.getPrice().compareTo(order.getPricePerUnit()) >= 0
                )
                || 
                (
                    order.getOrderStop().equals(OrderStop.PROFIT)
                    && stockHistory.getPrice().compareTo(order.getPricePerUnit()) <= 0
                )
            ) {
                continue;
            }

            if (stockCalculateByWalletService.execute(stock, order.getWallet()).compareTo(order.getQuantity()) < 0){
                order.setOrderState(OrderState.CANCELADA);

                orderRepository.save(order);
                
                continue;
            }
            
            order.setOrderState(OrderState.EJECUTADA);

            order.setPricePerUnit(stockHistory.getPrice());

            orderRepository.save(order);

            transactionGenerateService.generate(
                TypeTransaction.STOCK, 
                stock.getCurrentPrice() * order.getQuantity().doubleValue(), 
                "Venta de acciones", 
                TransactionActor.SISTEMA, 
                TransactionActor.ESTUDIANTE, 
                order.getWallet(), 
                null, 
                null,
                null,
                order,
                null,
                null
            );

            stockMoveService.toAvailable(stock, order.getQuantity());

            walletUpdateInvestedBalanceService.execute(order.getWallet());

        }

    }
    
}
