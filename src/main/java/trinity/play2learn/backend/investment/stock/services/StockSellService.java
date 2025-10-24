package trinity.play2learn.backend.investment.stock.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.configs.exceptions.BadRequestException;
import trinity.play2learn.backend.economy.transaction.models.TransactionActor;
import trinity.play2learn.backend.economy.transaction.models.TypeTransaction;
import trinity.play2learn.backend.economy.transaction.services.interfaces.ITransactionGenerateService;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.investment.stock.dtos.request.StockBuyRequestDto;
import trinity.play2learn.backend.investment.stock.dtos.response.StockSellResponseDto;
import trinity.play2learn.backend.investment.stock.mappers.OrderMapper;
import trinity.play2learn.backend.investment.stock.models.Order;
import trinity.play2learn.backend.investment.stock.models.OrderState;
import trinity.play2learn.backend.investment.stock.models.OrderType;
import trinity.play2learn.backend.investment.stock.models.Stock;
import trinity.play2learn.backend.investment.stock.repositories.IOrderRepository;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockCalculateByWalletService;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockFindByIdService;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockMoveService;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockSellService;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockUpdateSpecificService;
import trinity.play2learn.backend.user.models.User;

@Service
@AllArgsConstructor
public class StockSellService implements IStockSellService {

    private final IStockFindByIdService stockFindByIdService;

    private final IStudentGetByEmailService studentGetByEmailService;

    private final IStockCalculateByWalletService stockCalculateByWalletService;

    private final IOrderRepository orderRepository;

    private final ITransactionGenerateService transactionGenerateService;

    private final IStockMoveService stockMoveService;

    private final IStockUpdateSpecificService stockUpdateSpecificService;
    
    
    @Override
    @Transactional
    public StockSellResponseDto cu90sellStock(StockBuyRequestDto dto, User user) {

        Stock stock = stockFindByIdService.execute(dto.getStockId());

        Wallet wallet = studentGetByEmailService.getByEmail(user.getEmail()).getWallet();

        if (stockCalculateByWalletService.execute(stock, wallet).compareTo(dto.getQuantity()) < 0){
            throw new BadRequestException(
                "El wallet no cuenta con las acciones suficientes para realizar la venta"
            );
        }

        Order order = orderRepository.save(
            OrderMapper.toEntity(
                OrderType.VENTA, 
                OrderState.EJECUTADA, 
                stock, 
                wallet, 
                dto.getQuantity()
            )
        );

        transactionGenerateService.generate(
            TypeTransaction.STOCK, 
            stock.getCurrentPrice() * dto.getQuantity().doubleValue(), 
            "Venta de acciones", 
            TransactionActor.SISTEMA, 
            TransactionActor.ESTUDIANTE, 
            wallet, 
            null, 
            null,
            null,
            order
        );

        stockMoveService.toAvailable(stock, order.getQuantity());

        stockUpdateSpecificService.execute(stock);

        return OrderMapper.toSellDto(order);
    }
    
}
