package trinity.play2learn.backend.economy.wallet.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.economy.transaction.dtos.TransactionResponseDto;
import trinity.play2learn.backend.user.models.User;

public interface IWalletGetLastTransactionsService {
    
    public List<TransactionResponseDto> cu65GetLastTransactions (User user);
    
}
