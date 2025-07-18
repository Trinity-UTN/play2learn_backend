package trinity.play2learn.backend.admin.student.services;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.repositories.IStudentRepository;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentDeleteService;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByIdService;

@Service
@AllArgsConstructor
public class StudentDeleteService implements IStudentDeleteService {

    private final IStudentGetByIdService studentGetByIdService;

    private final IStudentRepository studentRepository;

    @Override
    public void cu19DeleteStudent(Long id) {
        
        Student student = studentGetByIdService.get(id); 

        student.delete();

        studentRepository.save(student);
        
    }

    
}
