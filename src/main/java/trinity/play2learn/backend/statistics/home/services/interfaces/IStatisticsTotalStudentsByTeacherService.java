package trinity.play2learn.backend.statistics.home.services.interfaces;

import java.util.List;

import trinity.play2learn.backend.admin.subject.models.Subject;

public interface IStatisticsTotalStudentsByTeacherService {

    public int execute (List<Subject> subjects);
    
} 
