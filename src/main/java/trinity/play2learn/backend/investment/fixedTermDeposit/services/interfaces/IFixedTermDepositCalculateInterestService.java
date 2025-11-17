package trinity.play2learn.backend.investment.fixedTermDeposit.services.interfaces;

import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermDays;

public interface IFixedTermDepositCalculateInterestService {
    
    public Double execute (Double amountInvested, FixedTermDays fixedTermDays);

}
