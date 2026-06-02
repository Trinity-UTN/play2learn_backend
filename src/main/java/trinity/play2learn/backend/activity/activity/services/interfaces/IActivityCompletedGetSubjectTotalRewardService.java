package trinity.play2learn.backend.activity.activity.services.interfaces;

import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.subject.models.Subject;

public interface IActivityCompletedGetSubjectTotalRewardService {
    
    Double getSubjectTotalRewardByStudent(Student student, Subject subject);
}
