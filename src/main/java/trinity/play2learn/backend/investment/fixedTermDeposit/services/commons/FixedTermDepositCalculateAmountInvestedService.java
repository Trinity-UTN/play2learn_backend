package trinity.play2learn.backend.investment.fixedTermDeposit.services.commons;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermDeposit;
import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermState;
import trinity.play2learn.backend.investment.fixedTermDeposit.repositories.IFixedTermDepositRepository;
import trinity.play2learn.backend.investment.fixedTermDeposit.services.interfaces.IFixedTermDepositCalculateAmountInvestedService;

@Service
@AllArgsConstructor
public class FixedTermDepositCalculateAmountInvestedService implements IFixedTermDepositCalculateAmountInvestedService {
    
    private final IFixedTermDepositRepository fixedTermDepositRepository;
    
    @Override
    public Double execute(Wallet wallet) {
        Double total = 0.0;

        for (FixedTermDeposit fixedTermDeposit : fixedTermDepositRepository.findByFixedTermState(FixedTermState.IN_PROGRESS)){
            total += fixedTermDeposit.getAmountReward();
        }

        return total;
    }
    
}
