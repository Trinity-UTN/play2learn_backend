package trinity.play2learn.backend.admin.subject.services.commons;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectHasStudentService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;

@Service
@AllArgsConstructor
public class SubjectHasStudentService implements ISubjectHasStudentService{
    
    @Override
    public void subjectHasStudent(Subject subject, Student student) {
        
        if (!subject.getStudents().contains(student)) {
            throw new ConflictException("El estudiante no puede comprar este beneficio ya que no esta asignado a la materia " + subject.obtainSubjectName());
        }
    }
    
    
}
