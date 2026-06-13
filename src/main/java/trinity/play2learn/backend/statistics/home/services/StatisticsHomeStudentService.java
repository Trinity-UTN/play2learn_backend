package trinity.play2learn.backend.statistics.home.services;

import java.util.List;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.services.commons.activityStatistics.ActivityCountAvailableByStudentService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetLast5RealizationsService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetPositionRankingByStudentsService;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByCourseService;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.statistics.home.dtos.response.StatisticsHomeStudentResponseDto;
import trinity.play2learn.backend.statistics.home.mappers.StatisticsHomeStudentMapper;
import trinity.play2learn.backend.statistics.home.services.interfaces.IStatisticsHomeStudentService;
import trinity.play2learn.backend.user.models.User;

@Service
@AllArgsConstructor
public class StatisticsHomeStudentService implements IStatisticsHomeStudentService {

    private final IStudentGetByEmailService studentGetByEmailService;
    private final IActivityGetLast5RealizationsService activityGetLast5RealizationsService;
    private final ActivityCountAvailableByStudentService activityCountAvailableByStudentService;
    private final IActivityGetPositionRankingByStudentsService activityGetPositionRankingByStudentsService;
    private final IStudentGetByCourseService studentGetByCourseService;

    @Override
    public StatisticsHomeStudentResponseDto cu73StatisticsHomeStudent(User user) {
        /**
         * Estadisticas
         * 
         *  - Obtener el monto actual de monedas de la billetera.
         * 
         *  - Posicion en el ranking del curso por actividades aprobadas
         * 
         *  - Cantidad total de actividades
         *          * 
         *  - Listado de las ultimas 5 realizaciones de actividad con:
         *      - Nombre de la actividad
         *      - Nombre de la materia
         *      - Nombre del estado de resultado
         *      - Recompensa obtenida
         *      - Calculo de hace cuanto ocurrio
         */

        Student student = studentGetByEmailService.getByEmail(user.getEmail());

        //Trae la cantidad de actividades disponibles para realizar del estudiante (Actividades publicadas que no fueron aprobadas o desaprobadas)
        int totalActivitiesAvailable = activityCountAvailableByStudentService.countAvailableByStudent(student);

        List<Student> students = studentGetByCourseService.getStudentsByCourseId(student.getCourse().getId());

        //Trae la posicion del estudiante en el ranking del curso por actividades aprobadas
        Object[] positionResult = activityGetPositionRankingByStudentsService.execute(student, students);

        int courseRankingByActivitiesPosition = ((Long) positionResult[0]).intValue();

        return StatisticsHomeStudentMapper.toDto(
            student.getWallet().getBalance().intValue()+student.getWallet().getInvertedBalance().intValue(), 
            courseRankingByActivitiesPosition, 
            totalActivitiesAvailable, 
            activityGetLast5RealizationsService.execute(student)
        );
    }
    
}
