package trinity.play2learn.backend.activity.activity.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.dtos.activityStudent.ActivityStudentCountResponseDto;
import trinity.play2learn.backend.activity.activity.mappers.ActivityMapper;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.activity.activity.models.activity.ActivityStatus;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityFilterApprovedService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityFilterByDisapprovedService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetByStudentService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityGetStatusService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityStudentCountService;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.user.models.User;

@Service
@AllArgsConstructor
public class ActivityStudentCountService implements IActivityStudentCountService{
    
    private final IStudentGetByEmailService studentGetByEmailService;
    private final IActivityGetByStudentService activityGetByStudentService;
    private final IActivityFilterApprovedService activityFilterApprovedService;
    private final IActivityFilterByDisapprovedService activityFilterByDisapprovedService;
    private final IActivityGetStatusService activityGetStatusService;

    @Override
    @Transactional(readOnly = true)
    public ActivityStudentCountResponseDto cu88CountActivitiesPerState(User user) {
        
        Student student = studentGetByEmailService.getByEmail(user.getEmail());
        
        List<Activity> activities = activityGetByStudentService.getByStudent(student);

        List<Activity> approvedActivities = activityFilterApprovedService.filterByApproved(activities, student);

        List<Activity> disapprovedActivities = activityFilterByDisapprovedService.filterByDisapproved(activities, student, true);

        activities.removeAll(approvedActivities);
        activities.removeAll(disapprovedActivities);

        List<Activity> availableActivities = new ArrayList<>();

        List<Activity> expiredActivities = new ArrayList<>();

        activities.stream().forEach(activity -> {
            
            ActivityStatus status = activityGetStatusService.getStatus(activity);

            if (status == ActivityStatus.PUBLISHED) {
                availableActivities.add(activity);
            } else if (status == ActivityStatus.EXPIRED) {
                expiredActivities.add(activity);
            }
        });

        return ActivityMapper.toCountDto(availableActivities.size(), approvedActivities.size(), disapprovedActivities.size(), expiredActivities.size());
    }
    
    
}
