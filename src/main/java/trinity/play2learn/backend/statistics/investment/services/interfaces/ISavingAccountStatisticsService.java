package trinity.play2learn.backend.statistics.investment.services.interfaces;

import trinity.play2learn.backend.statistics.investment.dtos.response.SavingAccountStatisticsResponseDto;
import trinity.play2learn.backend.user.models.User;

public interface ISavingAccountStatisticsService {

    public SavingAccountStatisticsResponseDto execute(User user);
    
}
