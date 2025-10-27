package trinity.play2learn.backend.investment.fixedTermDeposit.services.commons;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermDays;
import trinity.play2learn.backend.investment.fixedTermDeposit.services.interfaces.IFixedTermDepositCalculateInterestService;

@Service
@AllArgsConstructor
public class FixedTermDepositCalculateRewardAmountService implements IFixedTermDepositCalculateInterestService {
    
    @Override
    public Double execute(Double amountInvested, FixedTermDays fixedTermDays) {

        Double interest = amountInvested * (1.5) * (fixedTermDays.getValor() / 365.0);

        return interest;

    }
    
}
