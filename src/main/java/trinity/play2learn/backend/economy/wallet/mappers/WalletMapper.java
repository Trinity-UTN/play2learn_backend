package trinity.play2learn.backend.economy.wallet.mappers;

import trinity.play2learn.backend.economy.wallet.dtos.response.WalletResponseDto;
import trinity.play2learn.backend.economy.wallet.models.Wallet;

public class WalletMapper {

    public static WalletResponseDto toDto(Wallet wallet) {
        return WalletResponseDto.builder()
            .id(wallet.getId())
            .balance(wallet.getBalance())
            .invertedBalance(wallet.getInvertedBalance())
            .build();
    }
    
}
