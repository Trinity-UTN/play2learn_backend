package trinity.play2learn.backend.investment.fixedTermDeposit.services.interfaces;

import trinity.play2learn.backend.investment.fixedTermDeposit.dtos.request.FixedTermDepositRegisterRequestDto;
import trinity.play2learn.backend.investment.fixedTermDeposit.dtos.response.FixedTermDepositResponseDto;
import trinity.play2learn.backend.user.models.User;

public interface IFixedTermDepositRegisterService {
    
    public FixedTermDepositResponseDto cu92registerFixedTermDeposit (
        FixedTermDepositRegisterRequestDto fixedTermDepositRegisterRequestDto,
        User user
    );

}
