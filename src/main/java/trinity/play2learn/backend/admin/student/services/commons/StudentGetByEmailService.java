package trinity.play2learn.backend.admin.student.services.commons;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.repositories.IStudentRepository;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByEmailService;
import trinity.play2learn.backend.configs.exceptions.NotFoundException;

@Service
@AllArgsConstructor
public class StudentGetByEmailService implements IStudentGetByEmailService{

    private final IStudentRepository studentRepository;

    @Override
    @Transactional(readOnly = true)
    public Student getByEmail(String email) {
        return studentRepository.findByUserEmailAndDeletedAtIsNull(email)
            .orElseThrow(() -> new NotFoundException("Estudiante con email " + email + " no encontrado"));
    }
    
}
