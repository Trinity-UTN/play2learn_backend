package trinity.play2learn.backend.activity.activity.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.activity.activity.dtos.activityStudent.ActivityStudentApprovedResponseDto;
import trinity.play2learn.backend.activity.activity.models.activity.Activity;
import trinity.play2learn.backend.admin.student.models.Student;

public interface IActivityCreateApprovedDtosService {
    
    List<ActivityStudentApprovedResponseDto> createApprovedDtos(List<Activity> activities, Student student);
}
