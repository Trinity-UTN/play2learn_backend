package trinity.play2learn.backend.statistics.home.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class StatisticsSubjectDataDto {

    public Long id;

    public String name;

    public Integer totalStudents;

    public Integer totalActivities;
    
}
