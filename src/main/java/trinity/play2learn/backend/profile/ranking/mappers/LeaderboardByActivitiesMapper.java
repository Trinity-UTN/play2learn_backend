package trinity.play2learn.backend.profile.ranking.mappers;

import java.util.ArrayList;
import java.util.List;

import trinity.play2learn.backend.profile.avatar.mappers.AspectMapper;
import trinity.play2learn.backend.profile.ranking.dtos.response.LeaderboardParticipantResponseDto;
import trinity.play2learn.backend.profile.ranking.dtos.response.LeaderboardResponseDto;
import trinity.play2learn.backend.admin.student.models.Student;

public class LeaderboardByActivitiesMapper {
    
    public static LeaderboardParticipantResponseDto toParticipantDto (Student student, Long position, Double quantity) {
        return LeaderboardParticipantResponseDto.builder()
            .position(position)
            .name(student.getLastname()+" "+student.getName())
            .quantity(quantity)
            .selectedBody(student.getProfile().getSelectedBody() != null ? AspectMapper.toSimpleDto(student.getProfile().getSelectedBody()) : null)
            .selectedShirt(student.getProfile().getSelectedShirt() != null ? AspectMapper.toSimpleDto(student.getProfile().getSelectedShirt()) : null)
            .selectedHat(student.getProfile().getSelectedHat() != null ? AspectMapper.toSimpleDto(student.getProfile().getSelectedHat()) : null)
            .experienceLevel(student.getProfile().getCurrentLevel())
            .build();
    }

    public static List<LeaderboardParticipantResponseDto> toParticipantDtoList (List<Object[]> results) {
        // La posicion 0 de cada Object[] es un objeto Student y la posicion 1 representa la cantidad
        List<LeaderboardParticipantResponseDto> participants = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            Object[] result = results.get(i);
            if (result.length < 2) {
                // Si solo hay 1 elemento, asumimos que es el Student y el count es 0 o hay un error
                continue;
            }
            Object countObj = result[1];
            Double count = countObj instanceof Long ? ((Long) countObj).doubleValue() : (Double) countObj;
            participants.add(toParticipantDto((Student) result[0], Long.valueOf(i+1), count));
        }
        return participants;
    }

    public static LeaderboardResponseDto toDto (List<LeaderboardParticipantResponseDto> participants, LeaderboardParticipantResponseDto currentUserPosition, Integer totalParticipants) {
        return LeaderboardResponseDto.builder()
            .participants(participants)
            .currentUserPosition(currentUserPosition)
            .totalParticipants(totalParticipants)
            .build();
    }
}
