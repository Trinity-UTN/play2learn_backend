package trinity.play2learn.backend.activity.activity.services.commons;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityDidAllStudentsApproveService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityStudentsApprovedCountService;

@Service
@AllArgsConstructor
public class ActivityDidAllStudentsApproveService implements IActivityDidAllStudentsApproveService {
    
    private final IActivityStudentsApprovedCountService activityStudentsApprovedCountService;
    
    @Override
    public boolean didAllStudentsApprove(Activity activity) {
        
        int studentsCount = activity.getSubject().getStudents().size();
        int approvedCount = activityStudentsApprovedCountService.activityGetStudentsApprovedCount(activity);

        return approvedCount == studentsCount;
    }
}
