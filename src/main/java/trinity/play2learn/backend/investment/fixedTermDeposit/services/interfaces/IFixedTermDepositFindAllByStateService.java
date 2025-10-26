package trinity.play2learn.backend.investment.fixedTermDeposit.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermDeposit;
import trinity.play2learn.backend.investment.fixedTermDeposit.models.FixedTermState;

public interface IFixedTermDepositFindAllByStateService {
    
    public List<FixedTermDeposit> execute (FixedTermState fixedTermState);
    
}

