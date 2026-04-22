package trinity.play2learn.backend.profile.ranking.mappers;

import java.util.ArrayList;
import java.util.List;

import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.profile.avatar.mappers.AspectMapper;
import trinity.play2learn.backend.profile.ranking.dtos.StudentWithRewardTotalDto;
import trinity.play2learn.backend.profile.ranking.dtos.response.LeaderboardParticipantResponseDto;
import trinity.play2learn.backend.profile.ranking.dtos.response.LeaderboardResponseDto;

public class LeaderboardByCoinsMapper {
    
    public static LeaderboardParticipantResponseDto toParticipantDto(Wallet wallet, Long position) {
        return LeaderboardParticipantResponseDto.builder()
            .position(position)
            .name(wallet.getStudent().getLastname()+ " " + wallet.getStudent().getName())
            .quantity(Math.round((wallet.getBalance()+wallet.getInvertedBalance()) * 100.0) / 100.0)
            .selectedBody(wallet.getStudent().getProfile().getSelectedBody() != null ? AspectMapper.toSimpleDto(wallet.getStudent().getProfile().getSelectedBody()) : null)
            .selectedShirt(wallet.getStudent().getProfile().getSelectedShirt() != null ? AspectMapper.toSimpleDto(wallet.getStudent().getProfile().getSelectedShirt()) : null)
            .selectedHat(wallet.getStudent().getProfile().getSelectedHat() != null ? AspectMapper.toSimpleDto(wallet.getStudent().getProfile().getSelectedHat()) : null)
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

    public static StudentWithRewardTotalDto toStudentWithRewardTotalDto(Student student, Double totalReward) {
        return StudentWithRewardTotalDto.builder()
            .student(student)
            .totalReward(totalReward)
            .build();
    }

    public static LeaderboardParticipantResponseDto toParticipantDto(StudentWithRewardTotalDto studentWithRewardTotalDto, Long position) {
        return LeaderboardParticipantResponseDto.builder()
            .position(position)
            .name(studentWithRewardTotalDto.getStudent().getName() + " " + studentWithRewardTotalDto.getStudent().getLastname())
            .quantity(Math.round((studentWithRewardTotalDto.getTotalReward()) * 100.0) / 100.0)
            .selectedBody(studentWithRewardTotalDto.getStudent().getProfile().getSelectedBody() != null ? AspectMapper.toSimpleDto(studentWithRewardTotalDto.getStudent().getProfile().getSelectedBody()) : null)
            .selectedShirt(studentWithRewardTotalDto.getStudent().getProfile().getSelectedShirt() != null ? AspectMapper.toSimpleDto(studentWithRewardTotalDto.getStudent().getProfile().getSelectedShirt()) : null)
            .selectedHat(studentWithRewardTotalDto.getStudent().getProfile().getSelectedHat() != null ? AspectMapper.toSimpleDto(studentWithRewardTotalDto.getStudent().getProfile().getSelectedHat()) : null)
            .build();
    }

    public static List<LeaderboardParticipantResponseDto> toDtoListByStudentWithRewardTotalDto(List<StudentWithRewardTotalDto> studentsWithRewardTotal) {
        Long position = 1L;
        List<LeaderboardParticipantResponseDto> participants = new ArrayList<>();
        for (StudentWithRewardTotalDto studentWithRewardTotal : studentsWithRewardTotal) {
            participants.add(toParticipantDto(studentWithRewardTotal, position));
            position++;
        }
        return participants;
    }
}
