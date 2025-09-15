package trinity.play2learn.backend.statistics.home.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.course.repositories.ICourseRepository;
import trinity.play2learn.backend.admin.student.repositories.IStudentRepository;
import trinity.play2learn.backend.admin.teacher.repositories.ITeacherRepository;
import trinity.play2learn.backend.admin.year.repositories.IYearRepository;
import trinity.play2learn.backend.economy.reserve.models.Reserve;
import trinity.play2learn.backend.economy.reserve.services.interfaces.IReserveFindLastService;
import trinity.play2learn.backend.statistics.home.dtos.response.StatisticsHomeAdminResponseDto;
import trinity.play2learn.backend.statistics.home.mappers.StatisticsHomeAdminMapper;
import trinity.play2learn.backend.statistics.home.services.interfaces.IStatisticsHomeAdminService;


@Service
@AllArgsConstructor
public class StatisticsHomeAdminService implements IStatisticsHomeAdminService{

    private final IYearRepository yearRepository;

    private final ICourseRepository courseRepository;

    private final IStudentRepository studentRepository;

    private final ITeacherRepository teacherRepository;

    private final IReserveFindLastService reserveFindLastService;

    @Override
    @Transactional(readOnly = true)
    public StatisticsHomeAdminResponseDto cu67GetStatisticsHomeAdmin() {

        Reserve reserve = reserveFindLastService.get();
        
        return StatisticsHomeAdminMapper.toDto(
            studentRepository.countByDeletedAtIsNull(),
            teacherRepository.countByDeletedAtIsNull(),
            yearRepository.countByDeletedAtIsNull(),
            courseRepository.countByDeletedAtIsNull(),
            (reserve.getCirculationBalance()+reserve.getReserveBalance()),
            reserve.getCirculationBalance(),
            reserve.getReserveBalance()
        );
    }
    
}
