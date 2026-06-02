package trinity.play2learn.backend.activity.activity.services.interfaces;

import trinity.play2learn.backend.admin.subject.models.Subject;

public interface IActivityValidateSubjectAssociationService {
    void validatePublishedActivitiesWithSubject(Subject subject);
}
