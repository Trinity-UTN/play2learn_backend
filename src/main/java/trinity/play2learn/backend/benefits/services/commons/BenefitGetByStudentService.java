package trinity.play2learn.backend.benefits.services.commons;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectGetByStudentService;
import trinity.play2learn.backend.benefits.models.Benefit;
import trinity.play2learn.backend.benefits.repositories.IBenefitRepository;
import trinity.play2learn.backend.benefits.services.interfaces.IBenefitGetByStudentService;

@Service
@AllArgsConstructor
public class BenefitGetByStudentService implements IBenefitGetByStudentService {
    
    private final ISubjectGetByStudentService subjectGetByStudentService;
    private final IBenefitRepository benefitRepository;

    @Override
    public List<Benefit> getByStudent(Student student) {
        
        List<Subject> subjects = subjectGetByStudentService.getByStudent(student);

        List<Benefit> benefits = benefitRepository.findBySubjectIn(subjects);

        return benefits;
    
    }
    

}
