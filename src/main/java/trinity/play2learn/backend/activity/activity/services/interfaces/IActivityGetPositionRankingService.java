package trinity.play2learn.backend.activity.activity.services.interfaces;

import trinity.play2learn.backend.admin.student.models.Student;

public interface IActivityGetPositionRankingService {
    
    Object[] execute (Student student);
    
}
