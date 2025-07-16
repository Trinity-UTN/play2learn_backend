package trinity.play2learn.backend.admin.teacher.services.commons;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.teacher.repositories.ITeacherRepository;
import trinity.play2learn.backend.admin.teacher.services.interfaces.ITeacherExistsByDniService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;

@Service
@AllArgsConstructor
public class TeacherExistsByDniService implements ITeacherExistsByDniService{
    
    private final ITeacherRepository teacherRepository;

    @Override
    public void validate(String dni, Long id) {

        if (teacherRepository.existsByDniAndIdNot(dni, id)) {
            throw new ConflictException("Teacher with dni " + dni + " already exists");
        }
        
    }

    @Override
    public void validate(String dni) {

        if (teacherRepository.existsByDni(dni)) {
            throw new ConflictException("Teacher with dni " + dni + " already exists");
        }
    }
}
