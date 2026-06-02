package trinity.play2learn.backend.activity.activity.services.commons.ranking;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompletedState;
import trinity.play2learn.backend.activity.activity.repositories.IActivityCompletedRepository;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetTotalRankingService;

@Service
@AllArgsConstructor
public class ActivityGetTotalRankingService implements IActivityGetTotalRankingService {
    
    private final IActivityCompletedRepository activityCompletedRepository;

    @Override
    public List<Object[]> execute () {
        List<Object[]> allResults = activityCompletedRepository.findAllStudentsByApprovedActivities(ActivityCompletedState.APPROVED);
        return allResults.stream().limit(10).collect(Collectors.toList());
    }
}
