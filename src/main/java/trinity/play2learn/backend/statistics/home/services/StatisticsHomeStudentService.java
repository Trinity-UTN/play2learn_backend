package trinity.play2learn.backend.statistics.home.services;

import java.util.Random;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCountByStudent;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetLast5RealizationsService;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.statistics.home.dtos.response.StatisticsHomeStudentResponseDto;
import trinity.play2learn.backend.statistics.home.mappers.StatisticsHomeStudentMapper;
import trinity.play2learn.backend.statistics.home.services.interfaces.IStatisticsHomeStudentService;
import trinity.play2learn.backend.user.models.User;

@Service
@AllArgsConstructor
public class StatisticsHomeStudentService implements IStatisticsHomeStudentService {

    private final IStudentGetByEmailService studentGetByEmailService;

    private final IActivityCountByStudent activityCountByStudent;

    private final IActivityGetLast5RealizationsService activityGetLast5RealizationsService;
    
    @Override
    public StatisticsHomeStudentResponseDto cu73StatisticsHomeStudent(User user) {
        /**
         * Estadisticas
         * 
         *  - Obtener el monto actual de monedas de la billetera.
         * 
         *  - Posicion en el ranking (Por ahora aleatorio)
         * 
         *  - Cantidad total de actividades
         * 
         *  - Cantidad total de actividades completadas (En estado aprobado, desaprobado o pendiente)
         * 
         *  - Listado de las ultimas 5 realizaciones de actividad con:
         *      - Nombre de la actividad
         *      - Nombre de la materia
         *      - Nombre del estado de resultado
         *      - Recompensa obtenida
         *      - Calculo de hace cuanto ocurrio
         */

        Student student = studentGetByEmailService.getByEmail(user.getEmail());

        Random random = new Random();

        int[] activityCount = activityCountByStudent.execute(student);
        
        return StatisticsHomeStudentMapper.toDto(
            student.getWallet().getBalance().intValue()+student.getWallet().getInvertedBalance().intValue(), 
            (random.nextInt(3) + 1), 
            activityCount[0], 
            activityCount[1], 
            activityGetLast5RealizationsService.execute(student)
        );
    }
    
}
