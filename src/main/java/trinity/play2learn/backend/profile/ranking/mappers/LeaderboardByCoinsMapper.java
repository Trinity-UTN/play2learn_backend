package trinity.play2learn.backend.profile.ranking.mappers;

import java.util.ArrayList;
import java.util.List;

import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.profile.ranking.dtos.response.LeaderboardParticipantResponseDto;
import trinity.play2learn.backend.profile.ranking.dtos.response.LeaderboardResponseDto;

public class LeaderboardByCoinsMapper {
    
    public static LeaderboardParticipantResponseDto toParticipantDto(Wallet wallet, Long position) {
        return LeaderboardParticipantResponseDto.builder()
            .position(position)
            .name(wallet.getStudent().getLastname()+ " " + wallet.getStudent().getName())
            .quantity(Math.round((wallet.getBalance()+wallet.getInvertedBalance()) * 100.0) / 100.0)
            .build();
    }

    public static List<LeaderboardParticipantResponseDto> toDtoList(List<Wallet> wallets) {
        Long position = 1L;
        List<LeaderboardParticipantResponseDto> participants = new ArrayList<>();
        for (Wallet wallet : wallets) {
            participants.add(toParticipantDto(wallet, position));
            position++;
        }
        return participants;
    }

    public static LeaderboardResponseDto toDto (
        List<LeaderboardParticipantResponseDto> participants, 
        LeaderboardParticipantResponseDto currentUserPosition
    ) {
        return LeaderboardResponseDto.builder()
            .currentUserPosition(currentUserPosition)
            .participants(participants)
            .build();
    }
}
