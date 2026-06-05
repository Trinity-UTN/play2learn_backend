package trinity.play2learn.backend.profile.ranking.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.profile.ranking.dtos.StudentWithTotalDto;

public interface IRankingGetStudentPositionService {

    public Long getStudentRankingPositionByCoins(List<StudentWithTotalDto> list, StudentWithTotalDto targetDto);
}
