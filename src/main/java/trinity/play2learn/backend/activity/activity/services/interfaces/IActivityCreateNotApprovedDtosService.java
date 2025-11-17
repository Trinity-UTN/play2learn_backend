package trinity.play2learn.backend.activity.activity.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.activity.activity.dtos.activityStudent.ActivityStudentNotApprovedResponseDto;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.admin.student.models.Student;

public interface IActivityCreateNotApprovedDtosService {
    
    List<ActivityStudentNotApprovedResponseDto> createNotApprovedDtos(List<Activity> activities, Student student);
}
