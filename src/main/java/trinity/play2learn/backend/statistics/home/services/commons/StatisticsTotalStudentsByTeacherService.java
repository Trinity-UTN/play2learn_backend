package trinity.play2learn.backend.statistics.home.services.commons;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.statistics.home.services.interfaces.IStatisticsTotalStudentsByTeacherService;

@Service
@AllArgsConstructor
public class StatisticsTotalStudentsByTeacherService implements IStatisticsTotalStudentsByTeacherService{

    @Override
    public int execute(List<Subject> subjects) {
        List<Student> students = subjects.stream()
        .flatMap(subject -> subject.getStudents().stream())
        .distinct()
        .toList();

        return students.size();
    }
    
}
