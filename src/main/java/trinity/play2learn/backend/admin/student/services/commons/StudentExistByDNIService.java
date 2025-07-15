package trinity.play2learn.backend.admin.student.services.commons;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.student.repositories.IStudentRepository;
import trinity.play2learn.backend.admin.student.services.interfaces.IStudentExistByDNIService;

@Service
@AllArgsConstructor
public class StudentExistByDNIService implements IStudentExistByDNIService {

    private final IStudentRepository studentRepository;

    @Override
    public boolean validate(String dni) {
        return studentRepository.existsByDniAndDeletedAtIsNull(dni);
    }
    
}
