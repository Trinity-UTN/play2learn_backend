package trinity.play2learn.backend.investment.investment.services;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.investment.fixedTermDeposit.services.interfaces.IFixedTermDepositCalculateAmountInvestedService;
import trinity.play2learn.backend.investment.investment.services.interfaces.IInvestmentCalculateTotalInvestedService;
import trinity.play2learn.backend.investment.savingAccount.services.commons.SavingAccountCalculateAmountInvestedService;
import trinity.play2learn.backend.investment.stock.services.interfaces.IStockCalculateAmountInvestedService;

@Service
@AllArgsConstructor
public class InvestemtCalculateTotalInvestedService implements IInvestmentCalculateTotalInvestedService{
    
    private final IStockCalculateAmountInvestedService stockCalculateAmountInvestedService;

    private final IFixedTermDepositCalculateAmountInvestedService fixedTermDepositCalculateAmountInvestedService;

    private final SavingAccountCalculateAmountInvestedService savingAccountCalculateAmountInvestedService;
    
    @Override
    public Double cu110calculateTotalInvested(Wallet wallet) {
        return stockCalculateAmountInvestedService.execute(wallet) + fixedTermDepositCalculateAmountInvestedService.execute(wallet) + savingAccountCalculateAmountInvestedService.execute(wallet);
    }
    
}
