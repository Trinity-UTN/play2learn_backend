package trinity.play2learn.backend.admin.student.services.commons;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.models.Student;
import trinity.play2learn.backend.admin.student.repositories.IStudentRepository;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentGetByIdService;

@Service
@AllArgsConstructor
public class StudentGetByIdService implements IStudentGetByIdService {

    private final IStudentRepository studentRepository;

    @Override
    public Student get(Long id) {
        return studentRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + id));
    }
    
}
