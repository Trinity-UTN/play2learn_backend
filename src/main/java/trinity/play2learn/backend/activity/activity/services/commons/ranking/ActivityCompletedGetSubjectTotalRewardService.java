package trinity.play2learn.backend.activity.activity.services.commons.ranking;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;
import trinity.play2learn.backend.activity.activity.repositories.IActivityCompletedRepository;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCompletedGetSubjectTotalRewardService;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.subject.models.Subject;

@Service
@AllArgsConstructor
public class ActivityCompletedGetSubjectTotalRewardService implements IActivityCompletedGetSubjectTotalRewardService {
    
    private final IActivityCompletedRepository activityCompletedRepository;

    // Trae la suma de las recompensas de un estudiante por materia y estado (Para el ranking de monedas por materia) 
    @Override
    public Double getSubjectTotalRewardByStudent(Student student, Subject subject) {
        return activityCompletedRepository.sumRewardBySubjectAndStateAndStudent(subject, ActivityCompletedState.APPROVED, student);
    }
}
