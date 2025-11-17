package trinity.play2learn.backend.statistics.investment.services.interfaces;

import trinity.play2learn.backend.statistics.investment.dtos.response.FixedTermDepositStatisticsResponseDto;
import trinity.play2learn.backend.user.models.User;

public interface IFixedTermDepositStatisticsService {
    
    public FixedTermDepositStatisticsResponseDto execute (User user);

}
