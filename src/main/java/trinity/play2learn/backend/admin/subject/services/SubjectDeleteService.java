package trinity.play2learn.backend.admin.subject.services;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.repositories.ISubjectRepository;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectGetByIdService;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectDeleteService;
import trinity.play2learn.backend.configs.exceptions.ConflictException;

@Service
@AllArgsConstructor
public class SubjectDeleteService implements ISubjectDeleteService {
    private final ISubjectRepository subjectRepository;
    private final ISubjectGetByIdService findSubjectByIdService;

    @Override
    public void cu30DeleteSubject(Long id) {

        Subject subject = findSubjectByIdService.findById(id);

        //No es posible eliminar una materia con estudiantes asociados
        if (!subject.getStudents().isEmpty()) {
            throw new ConflictException("Cannot delete subject with ID " + id + " because it has associated students.");
        }

        subject.delete();

        subjectRepository.save(subject);
        
    }
}
