package trinity.play2learn.backend.profile.ranking.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.profile.ranking.dtos.StudentWithTotalDto;

public interface IRankingGetTop10StudentsService {

    List<StudentWithTotalDto> getTop10StudentsByCoins(List<StudentWithTotalDto> students);
}