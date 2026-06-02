package trinity.play2learn.backend.activity.activity.services.commons.ranking;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;
import trinity.play2learn.backend.activity.activity.repositories.IActivityCompletedRepository;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetPositionRankingByStudentsService;
import trinity.play2learn.backend.admin.student.models.Student;

@Service
@AllArgsConstructor
public class ActivityGetPositionRankingByStudentsService implements IActivityGetPositionRankingByStudentsService {
    
    private final IActivityCompletedRepository activityCompletedRepository;

    @Override
    public Object[] execute (Student targetStudent, List<Student> students) {
        // Obtener todos los estudiantes ordenados
        List<Object[]> allResults = activityCompletedRepository.findAllStudentsByApprovedActivitiesInStudentList(students, ActivityCompletedState.APPROVED);
        
        // Buscar la posición del estudiante objetivo
        for (int i = 0; i < allResults.size(); i++) {
            Object[] result = allResults.get(i);
            if (result.length >= 2 && result[0] instanceof Student) {
                Student student = (Student) result[0];
                if (student.getId().equals(targetStudent.getId())) {
                    // Encontramos al estudiante, devolver posición (i+1) y cantidad
                    Object countObj = result[1];
                    Long count = countObj instanceof Long ? (Long) countObj : ((Double) countObj).longValue();
                    return new Object[]{(long) (i + 1), count};
                }
            }
        }
        
        // Si no se encuentra en la lista, calcular posición y count manualmente
        Long count = (long) activityCompletedRepository.countByStudentAndState(targetStudent, ActivityCompletedState.APPROVED);
        // Contar cuántos estudiantes tienen más actividades aprobadas o la misma cantidad con ID menor
        long position = 1;
        for (Object[] result : allResults) {
            if (result.length >= 2) {
                Object countObj = result[1];
                Long studentCount = countObj instanceof Long ? (Long) countObj : ((Double) countObj).longValue();
                if (studentCount > count) {
                    position++;
                } else if (studentCount.equals(count) && result[0] instanceof Student) {
                    Student student = (Student) result[0];
                    if (student.getId() < targetStudent.getId()) {
                        position++;
                    } else if (student.getId().equals(targetStudent.getId())) {
                        // Encontramos al estudiante, devolver posición y count
                        return new Object[]{position, count};
                    } else {
                        break;
                    }
                } else {
                    break;
                }
            }
        }
        
        return new Object[]{position, count};
    }
}
