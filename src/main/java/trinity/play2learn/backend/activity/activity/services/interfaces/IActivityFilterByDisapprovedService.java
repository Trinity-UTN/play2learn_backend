package trinity.play2learn.backend.activity.activity.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.admin.student.models.Student;

public interface IActivityFilterByDisapprovedService {
    
    List<Activity> filterByDisapproved(List<Activity> activities, Student student, Boolean disapproved);
}
