package trinity.play2learn.backend.profile.ranking.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.profile.ranking.dtos.StudentWithRewardTotalDto;

public interface IRankingGetTop10StudentsService {
    
    List<StudentWithRewardTotalDto> getTop10StudentsByCoins(List<StudentWithRewardTotalDto> students);
}