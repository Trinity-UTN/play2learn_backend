package trinity.play2learn.backend.admin.subject.services;

import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.admin.subject.repositories.ISubjectRepository;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectGetByIdService;
import trinity.play2learn.backend.admin.subject.services.interfaces.ISubjectDeleteService;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityValidateSubjectAssociationService;

@Service
@AllArgsConstructor
public class SubjectDeleteService implements ISubjectDeleteService {
    private final ISubjectRepository subjectRepository;
    private final ISubjectGetByIdService findSubjectByIdService;
    private final IActivityValidateSubjectAssociationService validateSubjectAssociationService;

    @Override
    public void cu30DeleteSubject(Long id) {

        Subject subject = findSubjectByIdService.findById(id);
        
        //Valida si la materia tiene actividades publicadas o pendientes de publicar
        validateSubjectAssociationService.validatePublishedActivitiesWithSubject(subject);

        subject.delete();

        subjectRepository.save(subject);
    }
}
