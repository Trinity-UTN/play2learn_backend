package trinity.play2learn.backend.statistics.home.mappers;

import trinity.play2learn.backend.statistics.home.dtos.response.StatisticsHomeAdminResponseDto;

public class StatisticsHomeAdminMapper {

    public static final StatisticsHomeAdminResponseDto toDto (
        int totalStudents,
        int totalTeachers,
        int totalYears,
        int totalCourses,
        Double reserveTotalBalance,
        Double reserveTotalBalanceOnCirculation,
        Double reserveTotalBalanceOnReserve
    ){
        return StatisticsHomeAdminResponseDto.builder()
            .totalStudents(totalStudents)
            .totalTeachers(totalTeachers)
            .totalYears(totalYears)
            .totalCourses(totalCourses)
            .reserveTotalBalance(reserveTotalBalance)
            .reserveTotalBalanceOnCirculation(reserveTotalBalanceOnCirculation)
            .reserveTotalBalanceOnReserve(reserveTotalBalanceOnReserve)
            .build();
    }
    
}
