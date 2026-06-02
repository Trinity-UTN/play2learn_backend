package trinity.play2learn.backend.admin.subject.services.commons;

import org.springframework.stereotype.Service;

import java.util.List;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.subject.repositories.ISubjectRepository;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectGetStudentsService;

@Service
@AllArgsConstructor
public class SubjectGetStudentsService implements ISubjectGetStudentsService {
    
    private final ISubjectRepository subjectRepository;

    @Override
    public List<Student> findStudentsBySubjectId(Long subjectId) {
        return subjectRepository.findStudentsBySubjectId(subjectId);
    }
}
