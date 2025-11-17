package trinity.play2learn.backend.statistics.home.services.interfaces;

import trinity.play2learn.backend.statistics.home.dtos.response.StatisticsHomeTeacherResponseDto;
import trinity.play2learn.backend.user.models.User;

public interface IStatisticsHomeTeacherService {
    
    public StatisticsHomeTeacherResponseDto cu68GetStatisticsHomeTeacher (User user);

}
