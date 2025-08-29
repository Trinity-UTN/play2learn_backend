package trinity.play2learn.backend.admin.subject.services.commons;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.course.models.Course;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.repositories.ISubjectRepository;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectAddStudentByCourseService;

@Service
@AllArgsConstructor
public class SubjectAddStudentByCourseService implements ISubjectAddStudentByCourseService{

    private final ISubjectRepository subjectRepository;

    @Override
    @Transactional
    public void addStudentByCourse(Course course, Student student) {
        List<Subject> subjects = subjectRepository.findAllByCourseAndOptionalIsFalseAndDeletedAtIsNull(course);

        subjects.forEach(subject -> subject.addStudent(student));

        subjectRepository.saveAll(subjects);
    }
    
}
