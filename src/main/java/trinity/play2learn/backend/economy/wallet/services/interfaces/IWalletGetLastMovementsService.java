package trinity.play2learn.backend.economy.wallet.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.economy.wallet.dtos.response.MovementResponseDto;
import trinity.play2learn.backend.user.models.User;

public interface IWalletGetLastMovementsService {
    
    public List<MovementResponseDto> cu65GetLastMovements (User user);
    
}
