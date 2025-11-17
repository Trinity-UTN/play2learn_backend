package trinity.play2learn.backend.economy.wallet.services.interfaces;

import trinity.play2learn.backend.economy.wallet.dtos.response.WalletCompleteResponseDto;
import trinity.play2learn.backend.user.models.User;

public interface IWalletGetService {
    
    public WalletCompleteResponseDto cu70GetWallet(User user);

}
