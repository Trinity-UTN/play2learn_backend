package trinity.play2learn.backend.profile.ranking.mappers;

import java.util.ArrayList;
import java.util.List;

import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.economy.wallet.models.Wallet;
import trinity.play2learn.backend.profile.avatar.mappers.AspectMapper;
import trinity.play2learn.backend.profile.profile.models.Profile;
import trinity.play2learn.backend.profile.ranking.dtos.StudentWithTotalDto;
import trinity.play2learn.backend.profile.ranking.dtos.response.LeaderboardParticipantResponseDto;
import trinity.play2learn.backend.profile.ranking.dtos.response.LeaderboardResponseDto;

public class LeaderboardByCoinsMapper {

    public static LeaderboardParticipantResponseDto toParticipantDto(Wallet wallet, Long position) {
        Profile profile = wallet.getStudent().getProfile();

        return LeaderboardParticipantResponseDto.builder()
                .position(position)
                .name(wallet.getStudent().getLastname() + " " + wallet.getStudent().getName())
                .quantity(Math.round((wallet.getBalance() + wallet.getInvertedBalance()) * 100.0) / 100.0)
                .selectedBody(profile.getSelectedBody() != null ? AspectMapper.toSimpleDto(profile.getSelectedBody()) : null)
                .selectedShirt(profile.getSelectedShirt() != null ? AspectMapper.toSimpleDto(profile.getSelectedShirt()) : null)
                .selectedHat(profile.getSelectedHat() != null ? AspectMapper.toSimpleDto(profile.getSelectedHat()) : null)
                .experienceLevel(profile.getCurrentLevel())
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

    public static LeaderboardResponseDto toDto(
            List<LeaderboardParticipantResponseDto> participants,
            LeaderboardParticipantResponseDto currentUserPosition,
            Integer totalParticipants) {
        return LeaderboardResponseDto.builder()
                .currentUserPosition(currentUserPosition)
                .participants(participants)
                .totalParticipants(totalParticipants)
                .build();
    }

    public static StudentWithTotalDto toStudentWithTotalDto(Student student, Double total) {
        return StudentWithTotalDto.builder()
                .student(student)
                .total(total)
                .build();
    }

    public static LeaderboardParticipantResponseDto toParticipantDto(StudentWithTotalDto studentWithRewardTotalDto,
            Long position) {
        return LeaderboardParticipantResponseDto.builder()
                .position(position)
                .name(studentWithRewardTotalDto.getStudent().getName() + " "
                        + studentWithRewardTotalDto.getStudent().getLastname())
                .quantity(Math.round((studentWithRewardTotalDto.getTotal()) * 100.0) / 100.0)
                .selectedBody(
                        studentWithRewardTotalDto.getStudent().getProfile().getSelectedBody() != null ? AspectMapper
                                .toSimpleDto(studentWithRewardTotalDto.getStudent().getProfile().getSelectedBody())
                                : null)
                .selectedShirt(
                        studentWithRewardTotalDto.getStudent().getProfile().getSelectedShirt() != null ? AspectMapper
                                .toSimpleDto(studentWithRewardTotalDto.getStudent().getProfile().getSelectedShirt())
                                : null)
                .selectedHat(studentWithRewardTotalDto.getStudent().getProfile().getSelectedHat() != null
                        ? AspectMapper.toSimpleDto(studentWithRewardTotalDto.getStudent().getProfile().getSelectedHat())
                        : null)
                .experienceLevel(studentWithRewardTotalDto.getStudent().getProfile().getCurrentLevel())
                .build();
    }

    public static List<LeaderboardParticipantResponseDto> toDtoListByStudentWithTotalDto(
            List<StudentWithTotalDto> studentsWithTotal) {
        Long position = 1L;
        List<LeaderboardParticipantResponseDto> participants = new ArrayList<>();
        for (StudentWithTotalDto studentWithTotal : studentsWithTotal) {
            participants.add(toParticipantDto(studentWithTotal, position));
            position++;
        }
        return participants;
    }
}
