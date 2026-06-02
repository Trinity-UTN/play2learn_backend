package trinity.play2learn.backend.activity.activity.services.commons;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityIsBeingDoneService;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activityCompleted.ActivityCompleted;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityCompletedGetLastStartedService;

@Service
@AllArgsConstructor
public class ActivityIsBeingDoneService implements IActivityIsBeingDoneService {

    private final IActivityCompletedGetLastStartedService activityCompletedGetLastStartedService;

    @Override
    public boolean execute(Activity activity, Student student) {
        Optional<ActivityCompleted> lastStartedInProgress = activityCompletedGetLastStartedService.getLastStartedInProgress(activity, student);
        return lastStartedInProgress.isPresent();
    }

}
