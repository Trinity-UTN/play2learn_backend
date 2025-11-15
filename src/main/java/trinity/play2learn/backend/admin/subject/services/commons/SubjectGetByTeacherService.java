package trinity.play2learn.backend.admin.subject.services.commons;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.repositories.ISubjectRepository;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectGetByTeacherService;
import trinity.play2learn.backend.admin.teacher.models.Teacher;

@Service
@AllArgsConstructor
public class SubjectGetByTeacherService implements ISubjectGetByTeacherService {
    
    private final ISubjectRepository subjectRepository;

    @Override
    public List<Subject> getSubjectsByTeacher(Teacher teacher) {
        return subjectRepository.findAllByTeacherAndDeletedAtIsNull(teacher);
    }
    

}
