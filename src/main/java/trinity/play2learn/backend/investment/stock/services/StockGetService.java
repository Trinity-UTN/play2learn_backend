package trinity.play2learn.backend.investment.stock.services;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.investment.stock.dtos.response.StockResponseDto;
import trinity.play2learn.backend.investment.stock.mappers.StockMapper;
import trinity.play2learn.backend.investment.stock.models.Stock;
import trinity.play2learn.backend.investment.stock.services.interfaces.IOrderFindPendingService;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockCalculateByWalletService;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockFindByIdService;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockGetService;
import trinity.play2learn.backend.user.models.User;

@Service
@AllArgsConstructor
public class StockGetService implements IStockGetService{

    private final IStockFindByIdService stockFindByIdService;

    private final IStudentGetByEmailService studentGetByEmailService;

    private final IStockCalculateByWalletService stockCalculateByWalletService;

    private final IOrderFindPendingService orderFindPendingService;
    
    @Override
    public StockResponseDto cu100GetStock(Long id, User user) {

        Stock stock = stockFindByIdService.execute(id);

        Wallet wallet = studentGetByEmailService.getByEmail(user.getEmail()).getWallet();

        return StockMapper.toDto(
            stock, 
            stockCalculateByWalletService.execute(stock, wallet),
            orderFindPendingService.execute(stock, wallet)
        );
    }
    
}
