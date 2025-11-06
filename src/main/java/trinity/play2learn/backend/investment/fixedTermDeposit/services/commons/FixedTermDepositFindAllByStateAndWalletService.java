package trinity.play2learn.backend.investment.fixedTermDeposit.services.commons;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermDeposit;
import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermState;
import trinity.play2learn.backend.investment.fixedTermDeposit.repositories.IFixedTermDepositRepository;
import trinity.play2learn.backend.investment.fixedTermDeposit.services.interfaces.IFixedTermDepositFindAllByStateAndWalletService;

@Service
@AllArgsConstructor
public class FixedTermDepositFindAllByStateAndWalletService implements IFixedTermDepositFindAllByStateAndWalletService {
    
    private final IFixedTermDepositRepository fixedTermDepositRepository;

    @Override
    public List<FixedTermDeposit> execute(FixedTermState fixedTermState, Wallet wallet) {
        return fixedTermDepositRepository.findByWalletAndFixedTermState(wallet, fixedTermState);
    }
    
}
