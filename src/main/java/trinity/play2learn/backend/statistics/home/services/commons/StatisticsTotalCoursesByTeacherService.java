package trinity.play2learn.backend.statistics.home.services.commons;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.statistics.home.services.interfaces.IStatisticsTotalCoursesByTeacherService;

@Service
@AllArgsConstructor
public class StatisticsTotalCoursesByTeacherService implements IStatisticsTotalCoursesByTeacherService{

    @Override
    public int execute(List<Subject> subjects) {
        return (int) subjects.stream()
                            .map(Subject::getCourse)
                            .distinct()
                            .count();
    }
    

}
