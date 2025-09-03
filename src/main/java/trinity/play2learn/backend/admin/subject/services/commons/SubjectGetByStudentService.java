package trinity.play2learn.backend.admin.subject.services.commons;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.repositories.ISubjectRepository;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectGetByStudentService;

@Service
@AllArgsConstructor
public class SubjectGetByStudentService implements ISubjectGetByStudentService {
    
    private final ISubjectRepository subjectRepository;

    @Override
    public List<Subject> getByStudent(Student student) {
        
        return subjectRepository.findAllByStudentsContainingAndDeletedAtIsNull(student);
    }
    
    
}
