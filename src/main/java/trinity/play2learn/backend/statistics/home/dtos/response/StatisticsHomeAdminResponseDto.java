package trinity.play2learn.backend.statistics.home.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class StatisticsHomeAdminResponseDto {

    public int totalStudents;

    public int totalTeachers;

    public int totalYears;

    public int totalCourses;

    public Double reserveTotalBalance;

    public Double reserveTotalBalanceOnCirculation;

    public Double reserveTotalBalanceOnReserve;

}
