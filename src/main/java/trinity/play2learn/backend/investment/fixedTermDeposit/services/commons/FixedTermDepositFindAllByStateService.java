package trinity.play2learn.backend.investment.fixedTermDeposit.services.commons;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermDeposit;
import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermState;
import trinity.play2learn.backend.investment.fixedTermDeposit.repositories.IFixedTermDepositRepository;
import trinity.play2learn.backend.investment.fixedTermDeposit.services.interfaces.IFixedTermDepositFindAllByStateService;

@Service
@AllArgsConstructor
public class FixedTermDepositFindAllByStateService implements IFixedTermDepositFindAllByStateService {
    
    private final IFixedTermDepositRepository fixedTermDepositRepository;
    
    @Override
    public List<FixedTermDeposit> execute(FixedTermState fixedTermState) {
        return fixedTermDepositRepository.findByFixedTermState(fixedTermState);
    }
    
}
