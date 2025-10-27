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
import trinity.play2learn.backend.investment.stock.dtos.response.StockBuyResponseDto;
import trinity.play2learn.backend.investment.stock.mappers.OrderMapper;
import trinity.play2learn.backend.investment.stock.models.Order;
import trinity.play2learn.backend.investment.stock.models.OrderState;
import trinity.play2learn.backend.investment.stock.models.OrderType;
import trinity.play2learn.backend.investment.stock.models.Stock;
import trinity.play2learn.backend.investment.stock.repositories.IOrderRepository;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockBuyService;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockFindByIdService;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockMoveService;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockUpdateSpecificService;
import trinity.play2learn.backend.user.models.User;

@Service
@AllArgsConstructor
public class StockBuyService implements IStockBuyService {

    private final IStudentGetByEmailService studentGetByEmailService;

    private final IStockFindByIdService stockFindByIdService;

    private final IOrderRepository orderRepository;

    private final ITransactionGenerateService transactionGenerateService;

    private final IStockMoveService stockMoveService;

    private final IStockUpdateSpecificService stockUpdateSpecificService;
    
    @Override
    @Transactional
    public StockBuyResponseDto cu84buystocks(StockBuyRequestDto stockBuyRequestDto, User user) {
        Wallet wallet = studentGetByEmailService
                .getByEmail(user.getEmail())
                .getWallet();

        Stock stock = stockFindByIdService
                .execute(stockBuyRequestDto.getStockId());

        if (stock.getAvailableAmount().compareTo(stockBuyRequestDto.getQuantity()) < 0) {
            throw new BadRequestException("No hay acciones disponibles suficientes para comprar.");
        }

        if (wallet.getBalance().compareTo(
                stock.getCurrentPrice() * stockBuyRequestDto.getQuantity().doubleValue()
            ) < 0) {
            throw new BadRequestException("No hay saldo suficiente en el wallet para realizar la compra.");
        }

        Order order = orderRepository.save(OrderMapper.toEntity(
            OrderType.COMPRA,
            OrderState.EJECUTADA,
            stock,
            wallet,
            stockBuyRequestDto.getQuantity()
        ));

        transactionGenerateService.generate(
            TypeTransaction.STOCK, 
            stock.getCurrentPrice() * stockBuyRequestDto.getQuantity().doubleValue(), 
            "Compra de acciones", 
            TransactionActor.ESTUDIANTE, 
            TransactionActor.SISTEMA, 
            wallet, 
            null, 
            null,
            null,
            order,
            null
        );

        stockMoveService.toSold(stock, order.getQuantity());

        stockUpdateSpecificService.execute(stock);

        return OrderMapper.toBuyDto(order);
    }
    
}
