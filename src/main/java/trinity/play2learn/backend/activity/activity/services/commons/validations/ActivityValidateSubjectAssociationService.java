package trinity.play2learn.backend.activity.activity.services.commons.validations;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import trinity.play2learn.backend.activity.activity.repositories.IActivityRepository;
import trinity.play2learn.backend.activity.activity.services.interfaces.IActivityValidateSubjectAssociationService;
import trinity.play2learn.backend.admin.subject.models.Subject;
import trinity.play2learn.backend.configs.exceptions.ConflictException;
import trinity.play2learn.backend.configs.messages.ConflictExceptionMessages;

@Service
@AllArgsConstructor
public class ActivityValidateSubjectAssociationService implements IActivityValidateSubjectAssociationService{

    private final IActivityRepository activityRepository;

    @Override
    public void validatePublishedActivitiesWithSubject(Subject subject) {
        if(activityRepository.existsBySubjectAndEndDateAfter(subject, LocalDateTime.now())) {
            throw new ConflictException(
                ConflictExceptionMessages.resourceDeletionNotAllowedDueToAssociations(
                    "Materia", 
                    String.valueOf(subject.getId()), 
                    "actividades publicadas"   
                )
            );
        }
    }
}
