package trinity.play2learn.backend.admin.subject.services.interfaces;

import trinity.play2learn.backend.admin.subject.dtos.SubjectResponseDto;

public interface ISubjectUnassignTeacherService {
    
    SubjectResponseDto cu50UnassignTeacher(Long subjectId);
}
