package trinity.play2learn.backend.economy.wallet.mappers;

import java.util.List;

import trinity.play2learn.backend.economy.wallet.dtos.response.MovementResponseDto;
import trinity.play2learn.backend.economy.wallet.dtos.response.WalletCompleteResponseDto;
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

    public static WalletCompleteResponseDto toCompleteDto(Wallet wallet, List<MovementResponseDto> movements) {
        return WalletCompleteResponseDto.builder()
            .id(wallet.getId())
            .balance(wallet.getBalance())
            .invertedBalance(wallet.getInvertedBalance())
            .movements(movements)
            .build();
    }
    
}
