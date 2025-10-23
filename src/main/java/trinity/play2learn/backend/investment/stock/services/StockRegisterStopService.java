package trinity.play2learn.backend.investment.stock.services;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.configs.exceptions.BadRequestException;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.investment.stock.dtos.request.StockOrderStopRequestDto;
import trinity.play2learn.backend.investment.stock.dtos.response.StockBuyResponseDto;
import trinity.play2learn.backend.investment.stock.mappers.OrderMapper;
import trinity.play2learn.backend.investment.stock.models.Order;
import trinity.play2learn.backend.investment.stock.models.OrderState;
import trinity.play2learn.backend.investment.stock.models.OrderType;
import trinity.play2learn.backend.investment.stock.models.Stock;
import trinity.play2learn.backend.investment.stock.repositories.IOrderRepository;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockCalculateByWalletService;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockFindByIdService;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockRegisterStopService;
import trinity.play2learn.backend.user.models.User;

@Service
@AllArgsConstructor
public class StockRegisterStopService implements IStockRegisterStopService{
    
    private final IStockFindByIdService stockFindByIdService;

    private final IStudentGetByEmailService studentGetByEmailService;

    private final IStockCalculateByWalletService stockCalculateByWalletService;

    private final IOrderRepository orderRepository;
    
    
    @Override
    public StockBuyResponseDto cu91registerStopStock(StockOrderStopRequestDto dto, User user) {
        Stock stock = stockFindByIdService.execute(dto.getStockId());

        Wallet wallet = studentGetByEmailService.getByEmail(user.getEmail()).getWallet();

        if (stockCalculateByWalletService.execute(stock, wallet).compareTo(dto.getQuantity()) < 0){
            throw new BadRequestException(
                "El wallet no cuenta con las acciones suficientes para realizar el stop."
            );
        }

        Order order = orderRepository.save(
            OrderMapper.toStopEntity(
                OrderType.VENTA, 
                OrderState.PENDIENTE, 
                stock, 
                wallet, 
                dto.getQuantity(),
                dto.getPricePerUnit(),
                dto.getOrderStop()
            )
        );

        return OrderMapper.toBuyDto(order);
    }
    
}
