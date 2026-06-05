package trinity.play2learn.backend.activity.activity.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.profile.ranking.dtos.StudentWithTotalDto;

public interface IActivityCompletedGetTotalRewardService {

    Double getTotalRewardByStudent(Student student);

    List<StudentWithTotalDto> getTotalRewardByStudents(List<Student> students);
}
