package trinity.play2learn.backend.admin.student.services;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetSubjectsService;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.admin.subject.dtos.SubjectSimplifiedResponseDto;
import trinity.play2learn.backend.admin.subject.mappers.SubjectMapper;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectGetByStudentService;
import trinity.play2learn.backend.user.models.User;

@Service    
@AllArgsConstructor
public class StudentGetSubjectsService implements IStudentGetSubjectsService {
    
    private final IStudentGetByEmailService studentGetByEmailService;

    private final ISubjectGetByStudentService subjectGetByStudentService;

    @Override
    public List<SubjectSimplifiedResponseDto> cu124GetSubjectsByStudent(User user) {

        Student student = studentGetByEmailService.getByEmail(user.getEmail());

        List<Subject> subjects = subjectGetByStudentService.getByStudent(student);

        return SubjectMapper.toSimplifiedDtoList(subjects);
    }
}
