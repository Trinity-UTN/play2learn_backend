package trinity.play2learn.backend.activity.activity.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.statistics.home.dtos.response.StatisticsActivityRealizationResponseDto;

public interface IActivityGetLast5RealizationsService {
    
    public List<StatisticsActivityRealizationResponseDto> execute(Student student);
    
}
