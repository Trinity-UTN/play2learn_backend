package trinity.play2learn.backend.activity.activity.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.admin.student.models.Student;

public interface IActivityGetPositionRankingByStudentsService {
    
    Object[] execute (Student targetStudent, List<Student> students);
}
