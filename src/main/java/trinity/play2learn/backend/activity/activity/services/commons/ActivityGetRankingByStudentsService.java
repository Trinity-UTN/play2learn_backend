package trinity.play2learn.backend.activity.activity.services.commons;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;
import trinity.play2learn.backend.activity.activity.repositories.IActivityCompletedRepository;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetRankingByStudentsService;
import trinity.play2learn.backend.admin.student.models.Student;

@Service
@AllArgsConstructor
public class ActivityGetRankingByStudentsService implements IActivityGetRankingByStudentsService {

    private final IActivityCompletedRepository activityCompletedRepository;

    @Override
    public List<Object[]> execute (List<Student> students) {
        List<Object[]> allResults = activityCompletedRepository.findAllStudentsByApprovedActivitiesInStudentList(students, ActivityCompletedState.APPROVED);
        return allResults.stream().limit(10).collect(Collectors.toList());
    }
}
