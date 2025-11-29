package trinity.play2learn.backend.activity.activity.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.admin.student.models.Student;

public interface IActivityGetRankingByStudentsService {

    List<Object[]> execute (List<Student> students);

}
