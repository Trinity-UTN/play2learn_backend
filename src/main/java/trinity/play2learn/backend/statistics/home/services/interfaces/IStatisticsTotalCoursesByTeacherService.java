package trinity.play2learn.backend.statistics.home.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.admin.subject.models.Subject;

public interface IStatisticsTotalCoursesByTeacherService {
    
    public int execute (List<Subject> subjects);
    
}
