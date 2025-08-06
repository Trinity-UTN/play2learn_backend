package trinity.play2learn.backend.admin.subject.services.interfaces;

import trinity.play2learn.backend.admin.subject.dtos.SubjectResponseDto;

public interface ISubjectAssignTeacherService {
    
    SubjectResponseDto cu49AssignTeacher(Long subjectId, Long teacherId);
}
