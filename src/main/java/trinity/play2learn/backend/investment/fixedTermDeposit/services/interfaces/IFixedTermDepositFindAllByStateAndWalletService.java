package trinity.play2learn.backend.investment.fixedTermDeposit.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermDeposit;
import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermState;

public interface IFixedTermDepositFindAllByStateAndWalletService {

    public List<FixedTermDeposit> execute (FixedTermState fixedTermState, Wallet wallet);
    
}
