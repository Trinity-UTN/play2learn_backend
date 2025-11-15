package trinity.play2learn.backend.admin.teacher.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.course.models.Course;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectGetByTeacherService;
import trinity.play2learn.backend.admin.teacher.dtos.TeacherSubjectsDto;
import trinity.play2learn.backend.admin.teacher.mapper.TeacherMapper;
import trinity.play2learn.backend.admin.teacher.models.Teacher;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherGetByEmailService;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherGetSubjectsService;
import trinity.play2learn.backend.admin.year.models.Year;
import trinity.play2learn.backend.user.models.User;

@Service
@AllArgsConstructor
public class TeacherGetSubjectsService implements ITeacherGetSubjectsService {

    private final ITeacherGetByEmailService teacherGetByEmailService;
    private final ISubjectGetByTeacherService subjectGetByTeacherService;

    @Override
    @Transactional(readOnly = true)
    public TeacherSubjectsDto getSubjectsCoursesAndYears(User user) {
        
        Teacher teacher = teacherGetByEmailService.getByEmail(user.getEmail());

        List<Subject> subjects = subjectGetByTeacherService.getSubjectsByTeacher(teacher);
        List<Course> courses = subjects.stream().map(Subject::getCourse).distinct().toList();
        List<Year> years = courses.stream().map(Course::getYear).distinct().toList();
        
        return TeacherMapper.toTeacherSubjectsDto(subjects, courses, years);
    }
    
}
